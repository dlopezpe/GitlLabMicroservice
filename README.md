# Gitlab Microservice

Este microservicio se comunica con Gitlab para crear un repositorio con dos ramas y un tag.
Cuando ejecuta la imagen de la ventana acoplable, debe ser posible visitarla en un navegador donde se encuentre la imagen principal.
La página debe ser una interfaz de usuario Swagger.
En la página Swagger, debería haber un controlador de servicio que cree un repositorio en
Gitlab: este repositorio debe tener dos ramas (maestra y desarrollo) y una etiqueta (0.0.1).
Puedes utilizar la API oficial de Gitlab para realizar esta acción.
Esta aplicación debe crearse con SpringBoot.


Datos:
- Anfitrión: gitlab.localhost.com:9090

#### Nota:
Se ha realizado con github en vez de gitlab.

### Instrucciones para el desarrollo y tests en local

1. Crear token en el gitHub.com
   ```bash
   https://github.com/settings/tokens

2. Construir la imagen de Docker:

   ```bash
   docker build -t gitlab_service_api:v0.0.1 .

3. Ejecutar el contenedor:

   ```bash
   docker run -p 8080:8080 gitlab_service_api:v0.0.1

4. Acceder a la API Swagger:

   ```bash
   http://localhost:8080/seido/swagger-ui.html

### Instrucciones para despliegue en forma Devops

Estando en la ruta del proyecto que nos hayamos descargado lanzamos este script sh:

 ```bash
   $ ./launch_local.sh 


