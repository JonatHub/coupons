
# Ejercicio entrevista: Cupón

Esta aplicación está construida con **Spring Boot** y permite la generacion de cupones como ejercicio para el cargo de Ingeniero Senior, utilizando **Java 17** y **Docker** para facilitar el despliegue y la ejecución.

## Descripción

La aplicación proporciona funcionalidades para crear, gestionar y aplicar cupones en un entorno de comercio electrónico. Usando **Spring Boot**, este proyecto facilita la integración de cupones en sistemas backend. Además, está optimizado para ejecutarse en contenedores **Docker**, lo que permite un despliegue rápido y sencillo.

### Características

- Generacion de cupón para clientes de acuerdo al monto.
- Peticiones **asincronas** para obtener los precios de los productos.
- Manejo de **caché** con redis.
- Integración fácil con aplicaciones frontend a través de una API **RESTful**.
- Despliegue sencillo en contenedores **Docker**.
- **Java 17** y **Spring Boot** como principales tecnologías.

## Tecnologías Utilizadas

- **Java 17**: Lenguaje de programación principal.
- **Spring Boot**: Framework para el desarrollo de aplicaciones backend.
- **Docker**: Contenerización de la aplicación para facilitar su despliegue.
- **Gradle**: Sistema de construcción y gestión de dependencias.
- **Redis**: Manejo de caché.

## Requisitos

Antes de comenzar, asegúrate de tener instaladas las siguientes herramientas:

- **Java 17**: [Descargar Java 17](https://adoptopenjdk.net/)
- **Docker**: [Descargar Docker](https://www.docker.com/get-started)
- **Postman**: [Descargar Postman](https://www.postman.com/downloads/)

## Instalación

Sigue estos pasos para configurar el proyecto en tu entorno local.

## Instalación de Redis



Para usar esta aplicación, necesitas tener **Redis** instalado y en funcionamiento en tu máquina. Sigue los pasos a continuación según tu sistema operativo.



### Instalación en Ubuntu



#### Paso 1: Actualizar el sistema



Abre la terminal y ejecuta el siguiente comando para actualizar los paquetes de tu sistema:



```bash
sudo  apt  update
```



#### Paso 2: Instalar Redis



Una vez actualizado, instala Redis usando el siguiente comando:



```bash
sudo  apt  install  redis-server
```



#### Paso 3: Verificar la instalación



Después de la instalación, asegúrate de que Redis esté corriendo correctamente con el siguiente comando:



```bash
redis-server
```



Si ves un mensaje como `Ready to accept connections`, significa que Redis está funcionando correctamente.



#### Paso 4: Configuración (Opcional)



Redis debería empezar a ejecutarse automáticamente después de la instalación. Si deseas configurar Redis para que inicie automáticamente al arrancar el sistema, puedes usar el siguiente comando:



```bash
sudo  systemctl  enable  redis-server.service
```



#### Paso 5: Verificar que Redis esté funcionando



Puedes verificar que Redis está funcionando correctamente usando el siguiente comando para conectarte a la base de datos Redis:



```bash
redis-cli
```



Una vez dentro del cliente de Redis, puedes probar algunos comandos simples como:



```bash
ping
```



Si ves la respuesta `PONG`, significa que Redis está funcionando correctamente.



### Instalación en MacOS



Si estás usando **MacOS**, la forma más sencilla de instalar Redis es usando **Homebrew**. Aquí están los pasos:



#### Paso 1: Instalar Homebrew



Si aún no tienes **Homebrew** instalado, puedes instalarlo ejecutando este comando en la terminal:



```bash
/bin/bash  -c  "$(curl  -fsSL  https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```



#### Paso 2: Instalar Redis



Una vez que **Homebrew** esté instalado, ejecuta el siguiente comando para instalar Redis:



```bash
brew  install  redis
```



#### Paso 3: Iniciar Redis



Para iniciar Redis, ejecuta:



```bash
redis-server
```



#### Paso 4: Verificar que Redis esté funcionando



Para verificar que Redis está funcionando correctamente, abre otro terminal y ejecuta:



```bash
redis-cli
```



Luego, prueba con el comando `ping`:



```bash
ping
```



Si ves la respuesta `PONG`, significa que Redis está funcionando correctamente.



### Instalación en Windows



Para **Windows**, la instalación de Redis requiere usar **Windows Subsystem for Linux (WSL)** o utilizar una versión compatible de Redis para Windows.



#### Opción 1: Usar Windows Subsystem for Linux (WSL)



Si ya tienes configurado **WSL** en tu sistema, puedes seguir los mismos pasos que para **Ubuntu**. Si no tienes **WSL** instalado, puedes instalarlo siguiendo los pasos en la documentación oficial de Microsoft: [Instalar WSL](https://docs.microsoft.com/en-us/windows/wsl/install).



Una vez que tengas **WSL** instalado, abre la terminal de **WSL** y sigue los pasos de instalación para **Ubuntu**.



#### Opción 2: Usar Redis para Windows



Si prefieres una solución nativa de Windows, puedes descargar Redis para Windows desde el siguiente repositorio:



- [Redis para Windows](https://github.com/microsoftarchive/redis/releases)



Después de descargar el archivo ZIP, descomprímelo y ejecuta el archivo `redis-server.exe` desde la carpeta descomprimida.



#### Paso 3: Verificar que Redis esté funcionando



Para verificar que Redis está funcionando correctamente, abre otro terminal y ejecuta:



```bash
redis-cli
```



Luego, prueba con el comando `ping`:



```bash
ping
```



Si ves la respuesta `PONG`, significa que Redis está funcionando correctamente.

## Instalación de App
### 1. Clonar el repositorio

```bash
git clone https://github.com/JonatHub/coupons.git
cd coupons
```



### 2. Construir el archivo JAR

```bash
./gradlew  build
```



### 3. Construir la imagen Docker



Una vez que el archivo JAR esté listo, construye la imagen Docker:



```bash
docker  build  -t  coupons-app  .
```

### 4. Ejecutar la aplicación en Docker
Para ejecutar la aplicación dentro de un contenedor Docker:

```bash
docker  run  -p  8080:8080  coupons-app
```

La aplicación ahora estará disponible en `http://localhost:8080`.



## Uso de la API

### Endpoints disponibles


- `GET /coupon/{id}`: Obtiene un cupón específico por su ID.




### Ejemplo de creación de cupón (POST)



**Request**:



```json
{
	"item_ids":  [
	"MLA1448885331",
	"MLA1448885327"
	],
	"amount":  13000
}
```

**Response**:

```json
{
	"item_ids":  [
	"MLA1448885331"
	],
	"total":  12690
}
```

## Pruebas de carga

Para poder testear los 100k rpm esperados se utilizó **Jmeter** en un computador local con las siguientes especificaciones:

Intel(R) Core(TM) i7-3537U CPU @ 2.00GHz   2.50 GHz
RAM 8,00 GB

En la configuración de Jmeter se utilizaron 10000 hilos con un periodo de subida de 60 segundos

![img.png](src/main/resources/images/jmeter.png)

Los resultados fueron satisfactorios con un % de error de solo el 9,02% y un rendimiento de 289 peticiones/seg

![img_1.png](src/main/resources/images/img_1.png)

Adicionalmente se muestra visualmente usando **Redis insight** como guarda en caché los productos

![img.png](src/main/resources/images/img.png)



## Arquitectura del Proyecto

El proyecto está diseñado siguiendo una arquitectura modular y organizada en varios paquetes principales:

- **Controller**: Contiene la lógica para manejar las solicitudes HTTP, incluyendo excepciones globales y personalizadas (`GlobalException`, `BusinessException`).
- **Service**: Define la lógica de negocio principal, separada en interfaces (`IProductService`, `ICouponService`) y sus implementaciones (`ProductService`, `CouponService`).
- **Model**: Modelos de datos que representan las entradas y salidas de la API, como `ItemRequest`, `ItemResponse`, y `ProductsResponse`.
- **Repository**: Gestiona la interacción con Redis a través de `RedisTemplate`.
- **Configuration**: Configuración de Redis y serialización personalizada mediante `RedisConfig` y `FloatRedisSerializer`.
- **ExternalAPI**: Comunicación con APIs externas para obtener información adicional, manejada por `ExternalAPIWebflux`.

El diagrama PlantUML muestra las relaciones entre los paquetes y clases, destacando cómo cada componente interactúa de manera cohesiva dentro del sistema.

![img.png](src/main/resources/images/plantuml.svg)