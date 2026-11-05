# Parqueadero Autos Colombia

API REST con Spring Boot y MySQL para gestionar entradas y salidas de vehículos.

## Cómo ejecutarlo
1. Crear la base de datos con `database/parqueadero.sql`.
2. Crear `src/main/resources/application-local.properties` con:
   spring.datasource.username=root
   spring.datasource.password=
3. Ejecutar `./mvnw spring-boot:run`. La API queda en http://localhost:8082/api