package ec.edu.espe.agrosmart.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import ec.edu.espe.agrosmart.domain.Producto;
import ec.edu.espe.agrosmart.domain.ProductoFilters;
import ec.edu.espe.agrosmart.exception.ProductoNoEncontradoException;
import ec.edu.espe.agrosmart.mapper.ProductoMapper;
import ec.edu.espe.agrosmart.repository.ProductoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

// Mi servicio reactivo: el repositorio JPA bloquea, pero aqui nada bloquea el event loop.
@Service
public class ProductoService {

    private final ProductoRepository repository;

    // Mi producto por defecto si el filtro deja el flujo vacio.
    private static final Producto PRODUCTO_GENERICO = new Producto(
            0L, "PRODUCTO GENERICO", "Cacao", BigDecimal.ONE,
            List.of("notificaciones@agrosmart.ec"));

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public Flux<Producto> obtenerProductosComercializables() {
        return Mono.fromCallable(repository::findAll)          // Difiero la consulta bloqueante hasta la suscripcion.
                .subscribeOn(Schedulers.boundedElastic())      // Saco el bloqueo de JPA fuera del event loop.
                .flatMapMany(Flux::fromIterable)               // Convierto el Mono<List> en un Flux de elementos.
                .map(ProductoMapper::toDominio)                // Paso de entidad ORM a mi dominio inmutable.
                .map(ProductoFilters.A_MAYUSCULAS)             // Aplico mi transformacion: nombre en mayusculas.
                .filter(ProductoFilters.IS_VALID)              // Dejo pasar solo los productos comercializables.
                .doOnNext(ProductoFilters.LOG_PRODUCTO)        // Registro cada producto sin transformarlo.
                .defaultIfEmpty(PRODUCTO_GENERICO);            // Si quedo vacio, emito mi producto generico.
    }

    public Mono<Producto> buscarPorId(Long id) {
        return Mono.fromCallable(() -> repository.findById(id)) // Difiero la consulta bloqueante.
                .subscribeOn(Schedulers.boundedElastic())       // La saco del event loop.
                .flatMap(Mono::justOrEmpty)                     // Optional vacio -> Mono vacio.
                .map(ProductoMapper::toDominio)                 // Entidad -> dominio.
                .switchIfEmpty(Mono.error(                      // El "no encontrado" lo lanzo como error, sin block().
                        new ProductoNoEncontradoException(id)));
    }
}
