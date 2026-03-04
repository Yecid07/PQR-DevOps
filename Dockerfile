# Etapa 1: Build con Maven
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

RUN tree
# Copiamos el pom y descargamos dependencias (para aprovechar caché)
COPY pom.xml .
RUN mvn dependency:go-offline
# 👇 DEBUG
# Copiamos el código y empaquetamos
COPY src ./src

RUN tree
RUN mvn clean package -DskipTests

# Etapa 2: Producción (Runtime)
FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app

# En Maven, el JAR se genera en la carpeta /target/
COPY --from=build /app/target/*.jar app.jar

# Configuración de usuario seguro
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]