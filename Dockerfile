# Usa una imagen base con Java
FROM openjdk:11-jre-slim

# Establece el directorio de trabajo
WORKDIR /app

# Copia el JAR o el código fuente (dependiendo de tu elección) al contenedor
COPY target/gitlab-microservice.jar /app/gitlab-microservice.jar

# Expone el puerto en el que la aplicación se ejecuta
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "gitlab-microservice.jar"]
