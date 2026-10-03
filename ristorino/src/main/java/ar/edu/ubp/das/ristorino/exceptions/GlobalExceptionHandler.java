package ar.edu.ubp.das.ristorino.exceptions;

import ar.edu.ubp.das.ristorino.beans.ResponseBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String MSG_INTERNAL = "Ocurrió un error interno. Intente nuevamente.";
    private static final String MSG_UNAVAILABLE = "El servicio no está disponible en este momento. Intente nuevamente.";
    private static final String MSG_BAD_REQUEST = "Solicitud inválida.";

    /** Errores SQL con código >= 50000 son los lanzados por nuestros SP con RAISERROR/THROW (reglas de negocio). */
    private static final int FIRST_CUSTOM_SQL_ERROR = 50000;

    // ---------- Excepciones estándar de Spring MVC (405, 415, 406, 404, 400...) ----------

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatusCode statusCode, WebRequest request) {
        if (statusCode.is5xxServerError()) {
            log.error("Error MVC {}: {}", statusCode.value(), ex.getMessage(), ex);
        } else {
            log.warn("Solicitud rechazada {}: {}", statusCode.value(), ex.getMessage());
        }
        return respond(statusCode, headers, defaultMessage(statusCode));
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatusCode status,
                                                                          WebRequest request) {
        log.warn("Falta el parámetro '{}'", ex.getParameterName());
        return respond(status, headers, "Falta el parámetro '" + ex.getParameterName() + "'.");
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
                                                        HttpStatusCode status, WebRequest request) {
        String name = ex instanceof MethodArgumentTypeMismatchException m ? m.getName() : ex.getPropertyName();
        log.warn("Valor inválido para el parámetro '{}': {}", name, ex.getValue());
        return respond(status, headers, "El parámetro '" + name + "' tiene un valor inválido.");
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        log.warn("JSON ausente o malformado: {}", ex.getMessage());
        return respond(status, headers, "El cuerpo de la solicitud es inválido.");
    }

    // ---------- Excepciones propias del dominio ----------

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<Object> handleNegocio(NegocioException e) {
        log.warn("Regla de negocio: {}", e.getMessage());
        return respond(HttpStatus.CONFLICT, HttpHeaders.EMPTY, e.getMessage());
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Object> handleNoEncontrado(RecursoNoEncontradoException e) {
        log.warn("Recurso no encontrado: {}", e.getMessage());
        return respond(HttpStatus.NOT_FOUND, HttpHeaders.EMPTY, e.getMessage());
    }

    @ExceptionHandler(IntegracionException.class)
    public ResponseEntity<Object> handleIntegracion(IntegracionException e) {
        log.error("Fallo de integración: {}", e.getMessage(), e);
        return respond(HttpStatus.SERVICE_UNAVAILABLE, HttpHeaders.EMPTY, MSG_UNAVAILABLE);
    }

    // ---------- Validación de entrada ----------

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<Object> handleNumberFormat(NumberFormatException e) {
        log.warn("Valor numérico inválido: {}", e.getMessage());
        return respond(HttpStatus.BAD_REQUEST, HttpHeaders.EMPTY, "Un valor numérico de la solicitud es inválido.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Solicitud inválida: {}", e.getMessage());
        String message = (e.getMessage() == null || e.getMessage().isBlank()) ? MSG_BAD_REQUEST : e.getMessage();
        return respond(HttpStatus.BAD_REQUEST, HttpHeaders.EMPTY, message);
    }

    // ---------- Base de datos ----------

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Object> handleDuplicate(DuplicateKeyException e) {
        log.warn("Registro duplicado: {}", e.getMessage());
        return respond(HttpStatus.CONFLICT, HttpHeaders.EMPTY, "Ya existe un registro con esos datos.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleIntegrity(DataIntegrityViolationException e) {
        log.warn("Violación de integridad: {}", e.getMessage());
        return respond(HttpStatus.BAD_REQUEST, HttpHeaders.EMPTY, "Los datos enviados no son válidos.");
    }

    @ExceptionHandler({DataAccessResourceFailureException.class, TransientDataAccessException.class})
    public ResponseEntity<Object> handleDbUnavailable(Exception e) {
        log.error("Base de datos no disponible: {}", e.getMessage(), e);
        return respond(HttpStatus.SERVICE_UNAVAILABLE, HttpHeaders.EMPTY, MSG_UNAVAILABLE);
    }

    @ExceptionHandler(UncategorizedSQLException.class)
    public ResponseEntity<Object> handleSql(UncategorizedSQLException e) {
        SQLException cause = e.getSQLException();
        if (cause != null && cause.getErrorCode() >= FIRST_CUSTOM_SQL_ERROR && cause.getMessage() != null) {
            log.warn("Regla de negocio desde SP (código {}): {}", cause.getErrorCode(), cause.getMessage());
            return respond(HttpStatus.CONFLICT, HttpHeaders.EMPTY, cause.getMessage());
        }
        log.error("Error SQL no controlado: {}", e.getMessage(), e);
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, HttpHeaders.EMPTY, MSG_INTERNAL);
    }

    // ---------- Red de seguridad ----------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpected(Exception e) {
        log.error("Error no controlado: {}", e.getMessage(), e);
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, HttpHeaders.EMPTY, MSG_INTERNAL);
    }

    // ---------- Helpers ----------

    private static ResponseEntity<Object> respond(HttpStatusCode status, HttpHeaders headers, String message) {
        HttpStatus resolved = HttpStatus.resolve(status.value());
        String name = (resolved == null || resolved == HttpStatus.INTERNAL_SERVER_ERROR) ? "ERROR" : resolved.name();
        return new ResponseEntity<>(new ResponseBean(false, name, message), headers, status);
    }

    private static String defaultMessage(HttpStatusCode status) {
        return switch (status.value()) {
            case 404 -> "El recurso solicitado no existe.";
            case 405 -> "Método HTTP no permitido para este recurso.";
            case 406 -> "Formato de respuesta no soportado.";
            case 415 -> "Tipo de contenido no soportado.";
            default -> status.is5xxServerError() ? MSG_INTERNAL : MSG_BAD_REQUEST;
        };
    }
}