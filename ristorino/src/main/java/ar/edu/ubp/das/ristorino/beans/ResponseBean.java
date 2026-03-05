package ar.edu.ubp.das.ristorino.beans;
import lombok.Data;

@Data
public class ResponseBean {

    private boolean success;
    private String status;
    private String message;

    public ResponseBean() {
    }

    public ResponseBean(boolean success, String status, String message) {
        this.success = success;
        this.status = status;
        this.message = message;
    }

}
