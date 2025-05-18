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
Ejecutar : git clone https://github.com/vayo11/proyectoordenesypedidos.git
Ejecuto en la terminar : cd proyectoordenesypedidos
Cargar dependencias : ./gradlew clean build --refresh-dependencies (En cuenta que no se ejecuto el proto el error no es ningun problema)
Ejecutar el Proto : ./gradlew generateProto (Genera los request y reponse base para los archivos de inyeccion por eso el error anterior)
Ejecuto la compilacion : ./gradlew build
Levanto la Api: ./gradlew bootRun

#  Insersion:
Probamos el grpc para esto instalamos BloomRPC https://github.com/bloomrpc/bloomrpc/releases
importamos el archivo proto de la ubiacion ya mencionada anterioemente
colocamos la ruta : localhost:9090
configuramos el json: 
{
  "order_id": "1234",
  "customer_id": "039c7d80-318f-496c-ac21-9dc881fb5725",
  "phone": "555-1234",
  "items": [
    {
      "name": "Hello",
      "quantity": 10
    },
    {
      "name": "World",
      "quantity": 5
    }
  ]
}

Ejecutamos y ejecuta la grpc , el actor , envia el sms (Nota: Solo para produccion si se tiene cliente configurado)y guarda en mongodb

#  Consulta:
Se dispone de 2 empoints
Instalar postman: https://www.postman.com/downloads/
http://localhost:989/api/orders/id  (El id debe ser replazado por el id que se inserto segun el ejemplo 1234) deberia quedar asi
http://localhost:989/api/orders/1234 
Respuesta : 
{
  "id": {
    "timestamp": 1747556535,
    "date": 1747556535000
  },
  "orderId": "123",
  "customerId": "039c7d80-318f-496c-ac21-9dc881fb5725",
  "customerPhoneNumber": "555-1234",
  "status": "COMPLETED",
  "items": [
    "Hello (x10)",
    "World (x5)"
  ],
  "ts": 1747556535.151
}

El empoint de consulta por fecha lo puedes consultar asi  http://localhost:989/api/orders/by-date?startDate=2024-05-01T00:00:00Z&endDate=2024-05-31T23:59:59Z
