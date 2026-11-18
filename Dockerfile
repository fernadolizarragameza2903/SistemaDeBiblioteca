# =====================================================================
# Sistema de Biblioteca - Imagen Docker (multi-stage)
#   Etapa 1 (build):   compila el JAR con Maven Wrapper sobre JDK 21
#   Etapa 2 (runtime): solo el JRE 21 + el JAR, con usuario sin privilegios
# Construir:  docker build -t sistema-biblioteca .
# Ejecutar:   docker compose up -d --build   (levanta app + PostgreSQL)
# =====================================================================

# ---------- Etapa 1: build ----------
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

# Primero solo el wrapper y el pom: la capa de dependencias se cachea
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

# Luego el codigo fuente (las pruebas se ejecutan en el pipeline de CI)
COPY src src
RUN ./mvnw -B -q package -DskipTests && cp target/*.jar app.jar

# ---------- Etapa 2: runtime ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usuario sin privilegios: el contenedor no corre como root
RUN addgroup -S biblioteca && adduser -S biblioteca -G biblioteca
COPY --from=build --chown=biblioteca:biblioteca /workspace/app.jar app.jar
USER biblioteca

# Valores por defecto NO sensibles; las credenciales llegan por variables de entorno
ENV SPRING_PROFILES_ACTIVE=prod \
    PORT=8080 \
    JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC"

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD wget -qO- "http://localhost:${PORT}/actuator/health" | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
