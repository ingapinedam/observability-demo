FROM openjdk:17-slim as build
WORKDIR /workspace/app

# Copiar el archivo pom.xml y descargar dependencias para aprovechar el caché de Docker
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente y compilar
COPY src src
RUN ./mvnw package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Imagen final
FROM openjdk:17-slim
VOLUME /tmp

# Exponer puerto
EXPOSE 8080

# Variables para jvmArguments
ARG DEPENDENCY=/workspace/app/target/dependency

# Estructura de la aplicación
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Punto de entrada con opciones mínimas
ENTRYPOINT ["java", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-Dmanagement.metrics.enabled=false", \
            "-cp", "app:app/lib/*", "com.example.demo.DemoApplication"]