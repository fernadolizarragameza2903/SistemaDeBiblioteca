# Sistema de Biblioteca

Aplicación web para la gestión de libros y categorías de una biblioteca. El proyecto está construido con Spring Boot y utiliza PostgreSQL como base de datos, con Thymeleaf para la capa de presentación.

## Stack tecnológico

- **Java 21**
- **Spring Boot 4.1.1**
- **Spring Web MVC** para la capa web
- **Maven Wrapper** para compilar y ejecutar el proyecto sin requerir una instalación global de Maven
- **H2** para las pruebas automatizadas
- **Thymeleaf** para las vistas del lado del servidor
- **Spring Data JPA / Hibernate** para persistencia
- **PostgreSQL** como base de datos relacional

## Estructura del proyecto

```text
SistemaDeBiblioteca/
├── pom.xml                                 # Dependencias y configuración de Maven
├── mvnw                                    # Maven Wrapper para Linux/macOS
├── mvnw.cmd                                # Maven Wrapper para Windows
├── src/
│   ├── main/
│   │   ├── java/com/sistemadebiblioteca/
│   │   │   ├── SistemaDeBibliotecaApplication.java
│   │   │   ├── controller/               # Controladores web
│   │   │   ├── dto/                      # Objetos de transferencia de datos
│   │   │   ├── exception/                # Excepciones y manejo global de errores
│   │   │   ├── model/                    # Entidades JPA del dominio
│   │   │   ├── repository/               # Repositorios de acceso a datos
│   │   │   └── service/                  # Interfaces y lógica de negocio
│   │   └── resources/
│   │       ├── application.properties   # Configuración de la aplicación
│   │       ├── static/                    # Recursos estáticos: CSS, JavaScript e imágenes
│   │       └── templates/                 # Vistas Thymeleaf
│   └── test/
│       ├── java/                          # Pruebas automatizadas
│       └── resources/
│           └── application.properties   # Configuración de pruebas con H2
└── README.md

```

## Requisitos previos

Antes de ejecutar la aplicación, instala y verifica lo siguiente:

* JDK 21 o una versión compatible con el proyecto.
* PostgreSQL en ejecución.
* Una base de datos creada para la aplicación, por ejemplo `sistema_biblioteca`.
* Git, si vas a clonar el repositorio.

Puedes comprobar las versiones instaladas con:

```bash
java -version

```

En Windows también puedes comprobar Maven mediante el wrapper incluido, por lo que no es necesario instalar Maven globalmente.

## Configuración de PostgreSQL

1. Inicia el servicio de PostgreSQL.
2. Crea la base de datos de desarrollo:

```sql
CREATE DATABASE sistema_biblioteca;

```

3. Configura la conexión en `src/main/resources/application.properties` con los datos de tu instalación. Ejemplo:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sistema_biblioteca
spring.datasource.username=postgres
spring.datasource.password=TU_CONTRASENA
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

```

No guardes contraseñas reales en el repositorio. Para un entorno compartido, utiliza variables de entorno o un mecanismo de configuración externo.

## Ejecución del script SQL

El esquema y los datos iniciales deben cargarse en PostgreSQL **antes de iniciar la aplicación**. Ejecuta el script SQL desde la consola de PostgreSQL:

```bash
psql -U postgres -d sistema_biblioteca -f ruta/al/script.sql

```

También puedes abrir el archivo desde pgAdmin y ejecutarlo sobre la base de datos `sistema_biblioteca`.

> Actualmente el repositorio no incluye un archivo `.sql`. Antes del primer arranque, agrega o solicita el script de creación de tablas y datos iniciales, y reemplaza `ruta/al/script.sql` por su ubicación real.

## Cómo levantar el proyecto localmente

### Windows

Desde la raíz del proyecto, ejecuta:

```powershell
.\mvnw.cmd clean spring-boot:run

```

### Linux/macOS

Desde la raíz del proyecto, ejecuta:

```bash
./mvnw clean spring-boot:run

```

Cuando el servidor inicie correctamente, abre:

```text
http://localhost:8080

```

## Compilar y ejecutar el archivo JAR

Para generar el artefacto ejecutable:

```bash
# Windows
.\mvnw.cmd clean package

# Linux/macOS
./mvnw clean package

```

Después, ejecuta el JAR generado en `target/`:

```bash
java -jar target/SistemaDeBiblioteca-0.0.1-SNAPSHOT.jar

```

## Ejecutar las pruebas

Las pruebas utilizan una base de datos H2 en memoria y no requieren PostgreSQL:

```bash
# Windows
.\mvnw.cmd test

# Linux/macOS
./mvnw test

```

## Flujo recomendado de inicio

1. Clonar el repositorio y entrar en la carpeta del proyecto.
2. Iniciar PostgreSQL.
3. Crear la base de datos `sistema_biblioteca`.
4. Configurar las credenciales en `application.properties`.
5. Ejecutar el script SQL de estructura y datos iniciales.
6. Levantar la aplicación con Maven Wrapper.
7. Acceder a `http://localhost:8080`.