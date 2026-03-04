# ⚙️ Ristorino - REST API (Backend)

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![Gemini AI](https://img.shields.io/badge/Google%20Gemini-8E75B2?style=for-the-badge&logo=google%20bard&logoColor=white)

Ristorino REST API es el servicio backend que alimenta la plataforma de reservas gastronómicas. Está construido con Java y Spring Boot, y se encarga de procesar la lógica de negocio, la gestión de la base de datos y la seguridad de los usuarios.

Este repositorio contiene exclusivamente el código del servidor (Backend). La interfaz de usuario (Frontend) desarrollada en Angular se encuentra en el repositorio [ristorino-angular].

## ✨ Características Principales

* **🤖 Integración con Inteligencia Artificial (Gemini API):** Implementación de IA generativa para la creación automática de promociones atractivas y un motor de búsqueda semántica/inteligente para mejorar la experiencia del usuario al explorar restaurantes.
* **Arquitectura RESTful:** Exposición de endpoints claros y estructurados para la comunicación fluida con el cliente web.
* **Seguridad y Autenticación:** Implementación de manejo de sesiones para proteger rutas críticas (como la confirmación de reservas o datos de clientes).
* **Gestión de Datos:** Operaciones CRUD completas para restaurantes, promociones y turnos de reserva.
* **Integridad y Validaciones:** Manejo de excepciones globales y validación de datos de entrada desde los controladores.
* **Integración de Servicios:** Consumo de APIs de terceros simulando un entorno distribuido. Integración con un servicio REST y un servicio SOAP para obtener disponibilidad y promociones de restaurantes externos. 

## 🛠️ Tecnologías Utilizadas

* **Lenguaje:** Java [17]
* **Framework Core:** Spring Boot [ 3.5.6]
* **Acceso a Datos:** Spring Data JPA / Hibernate
* **Base de Datos:** [MySQL]
* **Gestor de Dependencias:** [Maven]

## 👨‍💻 Autor
Nicolás Moresco
Email: nicolasmoresco02@gmail.com
