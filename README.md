# AgroSmart — Jonathan Moreta

Backend reactivo de comercialización agrícola (examen final de Programación Avanzada, ESPE).
Publica reactivamente los productos comercializables de una categoría, persistidos en
PostgreSQL con JPA/Hibernate, y genera frases publicitarias con un modelo de lenguaje vía
LangChain4j, sin bloquear el event loop de Netty.

## Semilla personal

Derivada de mi cédula **1724530751** (dos últimos dígitos `NN = 51`, último dígito `1`):

| Parámetro | Valor |
|-----------|-------|
| Tabla | `tbl_productos_base_51` |
| Puerto (perfil prod) | `8151` |
| Categoría | Cacao |
| Audiencia para la IA | exportadores europeos |
| Base de datos | `agrosmart_db` |

## Tecnologías

Java 21 · Spring Boot 4.1 (WebFlux/Netty) · Spring Data JPA (Hibernate) · PostgreSQL ·
Project Reactor · LangChain4j 1.0.0-beta1 · JUnit 5 + StepVerifier + Mockito · Maven.

## Cómo ejecutar

Requisitos: Java 21 y Docker Desktop corriendo (la base se levanta sola con Docker Compose).

```bash
# 1. Levantar la aplicación (arranca PostgreSQL en un contenedor automáticamente)
./mvnw spring-boot:run        # en Windows: .\mvnw.cmd spring-boot:run

# 2. Ejecutar las pruebas
./mvnw test                   # en Windows: .\mvnw.cmd test
```

La app arranca en el puerto **8151** con el perfil `prod` activo y siembra 5 productos
(3 válidos + 2 inválidos) la primera vez.

## Regla de negocio

Un producto es **comercializable** si `precioUsd > 0` **y** su lista de correos no está vacía.

## Endpoints

| Método | Ruta | Retorno |
|--------|------|---------|
| GET | `/api/productos` | `Flux<Producto>` — solo los comercializables |
| GET | `/api/productos/{id}` | `Mono<Producto>` — 404 si no existe |
| GET | `/api/agrosmart/publicidad?producto=..&audiencia=..` | `Mono<String>` — texto de la IA |

Ejemplos reales (con mi puerto 8151):

```bash
$ curl http://localhost:8151/api/productos
[{"id":1,"nombre":"CACAO FINO DE AROMA","categoria":"Cacao","precioUsd":120.50,"correosNotificacion":["ventas@agrosmart.ec"]}, ...]

$ curl http://localhost:8151/api/productos/1
{"id":1,"nombre":"Cacao fino de aroma","categoria":"Cacao","precioUsd":120.50,"correosNotificacion":["ventas@agrosmart.ec"]}

$ curl -i http://localhost:8151/api/productos/9999
HTTP/1.1 404 Not Found

$ curl "http://localhost:8151/api/agrosmart/publicidad?producto=Cacao%20fino%20de%20aroma&audiencia=exportadores%20europeos"
"Descubre el sabor único del cacao fino de aroma: tu aliado perfecto para el mercado europeo."
```

## El puente bloqueante → reactivo

JPA/Hibernate y la llamada HTTP a la IA son **bloqueantes**; Netty atiende con un event loop
de pocos hilos que **no se puede bloquear**. Por eso en `ProductoService` y `PublicidadService`
envuelvo cada llamada bloqueante en `Mono.fromCallable(...)` (para diferir su ejecución hasta
la suscripción) y la muevo con `.subscribeOn(Schedulers.boundedElastic())` a un pool elástico
pensado para tareas bloqueantes. Así el event loop nunca se bloquea.

## Justificación de los operadores reactivos

| Operador | Por qué lo uso |
|----------|----------------|
| `Mono.fromCallable(...)` | Difiere la consulta bloqueante hasta que alguien se suscribe. |
| `.subscribeOn(boundedElastic())` | Ejecuta el bloqueo (JPA e IA) fuera del event loop. |
| `.flatMapMany(Flux::fromIterable)` | Convierte el `Mono<List>` en un `Flux` de elementos. |
| `.map(ProductoMapper::toDominio)` | Pasa de la entidad ORM a mi modelo inmutable. |
| `.map(A_MAYUSCULAS)` | Transforma cada producto (nombre en mayúsculas) sin mutarlo. |
| `.filter(IS_VALID)` | Deja pasar solo los productos comercializables. |
| `.doOnNext(LOG_PRODUCTO)` | Traza cada producto emitido, sin transformarlo. |
| `.defaultIfEmpty(PRODUCTO_GENERICO)` | Emite un genérico si el filtro dejó el flujo vacío. |
| `.switchIfEmpty(Mono.error(...))` | Resuelve el "no encontrado" como error, para el 404. |
| `.timeout(...)` + `.onErrorResume(...)` | Cortan y dan respaldo si la IA tarda o falla. |

## Evidencias

Las capturas del proceso están en [`docs/evidencias/`](docs/evidencias/):

| Archivo | Qué muestra |
|---------|-------------|
| `01-arranque.png` | Arranque con perfil `prod` activo y puerto `8151` |
| `02-psql-estructura.png` | `\d tbl_productos_base_51` (columnas, `unique`, `numeric(10,2)`) |
| `03-psql-datos.png` | `SELECT` de los 5 productos sembrados (3 válidos + 2 inválidos) |
| `04-curl-1.png` | `curl` a `GET /api/productos` (3 comercializables, nombre en mayúsculas) |
| `04-curl-2.png` | `curl` a `GET /api/productos/1` |
| `04-curl-3.png` | `curl -i` a `GET /api/productos/9999` → HTTP 404 |
| `04-curl-4.png` | `curl` a `GET /api/agrosmart/publicidad` → texto de la IA |
| `05-pruebas.png` | `mvnw test` en verde (`Tests run: 11, Failures: 0, Errors: 0`) |
| `Docker.png` | Contenedor `agrosmart-postgres` corriendo en Docker Desktop |
| `06-git-log.png` | `git log --oneline --graph --all` con las ramas por fase |

## Estructura

```
ec.edu.espe.agrosmart
├── controller/AgroSmartController      # WebFlux — Mono/Flux
├── service/ProductoService             # flujo reactivo + puente boundedElastic
├── service/PublicidadService           # IA reactiva con timeout y onErrorResume
├── service/AgroSmartAIService          # interfaz @AiService de LangChain4j
├── repository/ProductoRepository       # JpaRepository (bloqueante)
├── entity/ProductoEntity               # @Entity mutable (Hibernate)
├── domain/Producto                     # modelo 100% inmutable
├── domain/ProductoFilters              # Predicate + Consumer + Function
├── mapper/ProductoMapper               # ProductoEntity → Producto
├── exception/ProductoNoEncontradoException
└── config/DataInitializer              # siembra idempotente
```
