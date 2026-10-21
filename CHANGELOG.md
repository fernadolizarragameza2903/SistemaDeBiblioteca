# Changelog

Todos los cambios relevantes del proyecto se registran aquí.
Formato basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/) y [Versionado Semántico](https://semver.org/lang/es/).

## [0.2.0] - 2026-10-21

Segundo avance: el sistema cubre el núcleo de la biblioteca (catálogo, usuarios y préstamos) sobre PostgreSQL.

### Agregado
- Script de base de datos `database/01_schema.sql` (roles, usuarios, categorias, libros, prestamos) y datos de prueba `database/02_data.sql`.
- `docker-compose.yml` con PostgreSQL 16 que carga los scripts automáticamente.
- Perfiles `dev`, `prod` y `test` con credenciales por variables de entorno.
- Gestión de categorías: CRUD en `/api/categorias` y listado de libros por categoría.
- Gestión de préstamos en `/api/prestamos`: registro, devolución, activos, vencidos e historial por usuario, con control de stock y límite de préstamos activos.
- Configuración CORS y excepciones genéricas (`RecursoNoEncontrado`, `RecursoDuplicado`, `OperacionNoPermitida`).
- Pruebas de servicio con H2 para préstamos y categorías.
- Guía de colaboración (`CONTRIBUTING.md`), plantilla de Pull Request y plantillas de Issues.

### Cambiado
- Estructura de paquetes normalizada a `com.sistemadebiblioteca` (nombres de carpetas y archivos coherentes con las clases, compila en Windows, Linux y macOS).
- Usuarios: validación de datos de entrada y respuestas HTTP correctas (404/409) en lugar de `RuntimeException`.

## [0.1.0] - 2026-09-15

Primer avance.

### Agregado
- Proyecto base Spring Boot con Maven Wrapper.
- Gestión de libros: entidad, DTOs, repositorio, servicio y controlador REST en `/api/libros`.
- Manejo global de excepciones.
- Gestión de usuarios con contraseña cifrada (BCrypt) y configuración inicial de Spring Security.
