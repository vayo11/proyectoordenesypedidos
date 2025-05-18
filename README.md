#  OrderService - Microservicio de Gestión de Pedidos
Este microservicio permite registrar y procesar órdenes mediante una API gRPC, utilizando Spring WebFlux, MongoDB como base de datos reactiva y Akka para procesamiento asíncrono (por ejemplo, envío de SMS).

---

##  Tecnologías utilizadas

- Java 17
- Spring WebFlux
- MongoDB (Reactive)
- gRPC
- Protocol Buffers
- Akka
- Gradle
- SMMP

##  Estructura del proyecto

├── src/main/java/com/hacom/orderservice/
│ ├── controller/ -> (Contiene las rutas de consulta por Id y por fecha)
│ │   ├── OrderController.java -> (Contiene las rutas de consulta por Id y por fecha)
│ │   ├── TestController.java ->(Ruta para probar si esta arriba el servicio)
│ ├── config/ -> (cofiguracion mongo , smpp , webFlux)
│ │   ├── MongoConfig.java -> (Configuracion reactiva de mongo)
│ │   ├── SmppConfig.java ->(Configuracion smpp con twilio solo produccion)
│ │   ├── WebFluxConfig.java ->(Configuracion WebFluxConfig)
│ ├── grpc/ -> (Configuracion para server grpc y implementacion )
│ │   ├── GrpcServerRunner.java -> (Configuracion grpc compatible para springBoot 3)
│ │   ├── OrderServiceImpl.java ->(Implementacion de grpc para la insersion de ordenes)
│ ├── model/ -> Modelo Order
│ │   ├── Order.java -> (CModelo orden get y set)
│ ├── repository/ -> ReactiveMongoRepository
│ │   ├── OrderRepository.java -> (Repository hacia mongodb)
│ ├── service/ -> Lógica principal de procesamiento
│ │   ├──config
│ │   ├   ├──AkkaConfig.java(Configuracion bean e inyeccion a actor)
│ │   ├──mock
│ │   ├   ├──SmppServiceMock.java(Mock en desarrollo del smpp)
│ │   ├──OrderService.java (processOrder() y realiza las consultas con los metodos findOrderById() y findOrdersByDateRange())  
│ │   ├──SmppService.java (Service que ejecuta el smpp de envio sms (solo para produccion))
│ ├── smpp/ -> Actor Akka (SmppActor)
│ │   ├──SmppActor.java(Actor de procesos asincronos)
│ ├── OrderserviceApplication.java(Archivo Main)
├── proto/
│ └── OrderService.proto(Crear el request y response de la orden)
├── resources/
│ └── application.yml -> Configuración MongoDB, puertos, etc para desarrollo.
│ └── application-prod.yml -> Configuración MongoDB, puertos, etc para produccion.
│ └──log4j2.yml -> Configuracion para los logs.
├──build.gradle (Archivo de dependencias)

#  Ejecucion 
Clonar el proyecto de github