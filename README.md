# Sistema de Biblioteca

API REST para la gestión de una biblioteca: catálogo de libros y categorías, usuarios y préstamos. Construida con Spring Boot y PostgreSQL.

**Versión actual:** `v0.2.0` — ver [CHANGELOG](CHANGELOG.md) · **Guía del equipo:** [CONTRIBUTING](CONTRIBUTING.md)

## Stack tecnológico

- **Java 21**
- **Spring Boot 4.1.1**
- **Spring Web MVC** para la capa web
- **Spring Data JPA / Hibernate** para persistencia
- **Spring Security** (BCrypt para contraseñas)
- **Bean Validation** para validar datos de entrada
- **PostgreSQL 16** como base de datos relacional
- **H2** para las pruebas automatizadas
- **Thymeleaf** para las vistas del lado del servidor (próximo avance)
- **Maven Wrapper** para compilar y ejecutar sin instalar Maven

## Avance del proyecto

| Módulo                 | Estado        | Endpoints base      |
|------------------------|---------------|---------------------|
| Libros                 | ✅ Completado | `/api/libros`       |
| Usuarios               | ✅ Completado | `/api/usuarios`     |
| Categorías             | ✅ Completado | `/api/categorias`   |
| Préstamos y devoluciones | ✅ Completado | `/api/prestamos`  |
| Base de datos (scripts)| ✅ Completado | `database/`         |
| Autenticación / login por roles | ⏳ Pendiente | — |
| Reservas               | ⏳ Pendiente  | —                   |
| Multas por retraso     | ⏳ Pendiente  | —                   |
| Vistas web (Thymeleaf) | ⏳ Pendiente  | —                   |
| Reportes               | ⏳ Pendiente  | —                   |

## Estructura del proyecto

```text
SistemaDeBiblioteca/
├── database/
│   ├── 01_schema.sql                 # Tablas, relaciones, restricciones e índices
│   └── 02_data.sql                   # Datos de prueba
├── docker-compose.yml                # PostgreSQL 16 listo para usar (opcional)
├── src/
│   ├── main/
│   │   ├── java/com/sistemadebiblioteca/
│   │   │   ├── SistemaDeBibliotecaApplication.java
│   │   │   ├── config/               # CORS y seguridad
│   │   │   ├── controller/           # Controladores REST
│   │   │   ├── dto/                  # Objetos de transferencia de datos
│   │   │   ├── exception/            # Excepciones y manejo global de errores
│   │   │   ├── model/                # Entidades JPA
│   │   │   ├── repository/           # Repositorios Spring Data
│   │   │   └── service/              # Interfaces e implementaciones (impl/)
│   │   └── resources/
│   │       ├── application.properties        # Configuración común
│   │       ├── application-dev.properties    # PostgreSQL local
│   │       └── application-prod.properties   # Producción (variables de entorno)
│   └── test/                          # Pruebas con H2 en memoria
├── CONTRIBUTING.md                    # Reglas de ramas, commits, PR y versiones
├── CHANGELOG.md                       # Historial de versiones
└── pom.xml
```

## Base de datos

### Modelo

```text
roles 1 ──< usuarios 1 ──< prestamos >── 1 libros >── 1 categorias
```

| Tabla        | Descripción                                                        |
|--------------|--------------------------------------------------------------------|
| `roles`      | ADMIN, BIBLIOTECARIO, LECTOR                                       |
| `usuarios`   | Datos personales, email único, contraseña cifrada, rol, baja lógica |
| `categorias` | Clasificación de libros, nombre único                              |
| `libros`     | Catálogo con ISBN único y stock (`CHECK stock >= 0`)               |
| `prestamos`  | Usuario + libro, fechas de préstamo/devolución y estado (ACTIVO/DEVUELTO) |

### Opción A: PostgreSQL instalado

```bash
psql -U postgres -c "CREATE DATABASE sistema_biblioteca;"
psql -U postgres -d sistema_biblioteca -f database/01_schema.sql
psql -U postgres -d sistema_biblioteca -f database/02_data.sql
```

También puedes abrir los scripts en pgAdmin y ejecutarlos en orden.

### Opción B: Docker

```bash
docker compose up -d
```

Crea la base `sistema_biblioteca` y ejecuta los scripts de `database/` automáticamente.

### Credenciales

La aplicación lee la conexión desde variables de entorno (valores por defecto entre paréntesis):

| Variable        | Por defecto (perfil dev)                                |
|-----------------|---------------------------------------------------------|
| `DB_URL`        | `jdbc:postgresql://localhost:5432/sistema_biblioteca`   |
| `DB_USERNAME`   | `postgres`                                              |
| `DB_PASSWORD`   | `postgres`                                              |

Si tu contraseña local es distinta, defínela antes de ejecutar (PowerShell):

```powershell
$env:DB_PASSWORD = "tu_contrasena"
```

No guardes contraseñas reales en el repositorio.

Usuarios de prueba (contraseña `Biblioteca123`): `admin@biblioteca.com`, `maria.quispe@biblioteca.com`, `carlos.ramirez@correo.com`, `lucia.fernandez@correo.com`.

## Cómo levantar el proyecto

```powershell
# Windows
.\mvnw.cmd spring-boot:run
```

```bash
# Linux/macOS
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

## Ejecutar las pruebas

Las pruebas usan H2 en memoria y no requieren PostgreSQL:

```bash
.\mvnw.cmd test      # Windows
./mvnw test          # Linux/macOS
```

## Módulos y endpoints

### Libros — `/api/libros`

| Método | Ruta              | Descripción                    |
|--------|-------------------|--------------------------------|
| POST   | `/api/libros`     | Registrar libro                |
| GET    | `/api/libros`     | Listar libros activos          |
| GET    | `/api/libros/{id}`| Obtener libro                  |
| PUT    | `/api/libros/{id}`| Actualizar libro               |
| DELETE | `/api/libros/{id}`| Baja lógica                    |

### Usuarios — `/api/usuarios`

Administra a usuarios y bibliotecarios. Las contraseñas se cifran con `BCryptPasswordEncoder` antes de guardarse, se usa el patrón DTO para no exponer el hash de la contraseña y los usuarios se dan de baja de forma lógica (`activo = false`).

| Método | Ruta                 | Descripción                    |
|--------|----------------------|--------------------------------|
| POST   | `/api/usuarios`      | Crear usuario                  |
| GET    | `/api/usuarios`      | Listar usuarios activos        |
| GET    | `/api/usuarios/{id}` | Obtener usuario                |
| DELETE | `/api/usuarios/{id}` | Baja lógica                    |

### Categorías — `/api/categorias`

| Método | Ruta                          | Descripción                                   |
|--------|-------------------------------|-----------------------------------------------|
| POST   | `/api/categorias`             | Crear categoría (nombre único)                |
| GET    | `/api/categorias`             | Listar categorías activas                     |
| GET    | `/api/categorias/{id}`        | Obtener categoría                             |
| GET    | `/api/categorias/{id}/libros` | Libros activos de la categoría                |
| PUT    | `/api/categorias/{id}`        | Actualizar categoría                          |
| DELETE | `/api/categorias/{id}`        | Baja lógica (no permitida si tiene libros activos) |

### Préstamos — `/api/prestamos`

| Método | Ruta                               | Descripción                        |
|--------|------------------------------------|------------------------------------|
| POST   | `/api/prestamos`                   | Registrar préstamo                 |
| PATCH  | `/api/prestamos/{id}/devolucion`   | Registrar devolución               |
| GET    | `/api/prestamos`                   | Préstamos activos                  |
| GET    | `/api/prestamos/vencidos`          | Préstamos activos fuera de fecha   |
| GET    | `/api/prestamos/{id}`              | Obtener préstamo                   |
| GET    | `/api/prestamos/usuario/{id}`      | Historial de un usuario            |

Reglas de negocio:

- El usuario y el libro deben estar activos y el libro debe tener stock.
- Al prestar se descuenta 1 del stock; al devolver se repone.
- Máximo **3** préstamos activos por usuario y plazo por defecto de **14** días (configurable en `application.properties`).
- Un usuario no puede tener dos préstamos activos del mismo libro.

Ejemplo:

```bash
curl -X POST http://localhost:8080/api/prestamos \
  -H "Content-Type: application/json" \
  -d '{"usuarioId": 3, "libroId": 2, "dias": 7}'
```

### Respuestas de error

| Código | Cuándo                                                         |
|--------|----------------------------------------------------------------|
| 400    | Datos inválidos (validación) — devuelve el error por campo     |
| 404    | Recurso no encontrado                                          |
| 409    | Duplicado (ISBN, email o nombre de categoría)                  |
| 422    | Regla de negocio incumplida (sin stock, límite de préstamos…)  |

## Flujo de trabajo del equipo

Ramas por funcionalidad, commits con Conventional Commits (`feat:`, `fix:`, `docs:`…), Pull Request con revisión de otro integrante y versiones con tags SemVer. Detalle completo en [CONTRIBUTING.md](CONTRIBUTING.md).
