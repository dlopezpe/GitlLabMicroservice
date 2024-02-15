<!-- TOC -->
* [Gitlab Microservice](#gitlab-microservice)
    * [Nota:](#nota)
    * [Instrucciones para el desarrollo y tests en local](#instrucciones-para-el-desarrollo-y-tests-en-local)
    * [Instrucciones para despliegue en forma Devops](#instrucciones-para-despliegue-en-forma-devops)
    * [Cosas a tener en cuenta:](#cosas-a-tener-en-cuenta)
<!-- TOC -->

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

### Nota:
Se ha realizado con github en vez de gitlab.


### Instrucciones para el desarrollo y tests en local

1. Crear token en el gitHub.com y añadirlo en el application.yml

   ```bash
   
   github.api.owner: este recurso es optativo
   github.api.token: token generado en la url:
      https://github.com/settings/tokens

2. Construir la imagen de Docker:

   ```bash
   docker build -t gitlab_service_api:v0.0.1 .

3. Ejecutar el contenedor:

   ```bash
   docker run -p 8082:8082 gitlab_service_api:v0.0.1

4. Acceder a la API Swagger, se cambia el puerto para que no tenga conflictos en jenkins 
   ni el api de jenkins, es todo configurable:

   ```bash
   http://localhost:8082/seido/swagger-ui.html

### Instrucciones para despliegue en forma Devops

Estando en la ruta del proyecto que nos hayamos descargado lanzamos este script sh:

    ```bash
      $ ./launch_local.sh

Este script levanta teniendo el docker desktop que he utilizado levantado, las siguientes imagenes:
   - El servicio gitlab-service-api expuesto al puerto 8082

### Cosas a tener en cuenta:

No se ha podido completar los test a causa de fuerza mayor y falta de tiempo.
Se ha realizado pruebas integradas levantando el docker, teniendo github y ver que se creaba nuevos repositorios, etiquetas y ramas