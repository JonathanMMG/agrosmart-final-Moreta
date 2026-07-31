package ec.edu.espe.agrosmart.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.espe.agrosmart.domain.Producto;
import ec.edu.espe.agrosmart.service.ProductoService;
import ec.edu.espe.agrosmart.service.PublicidadService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Mi API reactiva: todas las firmas devuelven Mono/Flux, nunca List ni block().
@RestController
public class AgroSmartController {

    private final ProductoService productoService;
    private final PublicidadService publicidadService;

    public AgroSmartController(ProductoService productoService, PublicidadService publicidadService) {
        this.productoService = productoService;
        this.publicidadService = publicidadService;
    }

    // Devuelvo el flujo de productos comercializables.
    @GetMapping("/api/productos")
    public Flux<Producto> obtenerProductos() {
        return productoService.obtenerProductosComercializables();
    }

    // Devuelvo un producto por id; si no existe, el servicio lanza el 404.
    @GetMapping("/api/productos/{id}")
    public Mono<Producto> obtenerProductoPorId(@PathVariable Long id) {
        return productoService.buscarPorId(id);
    }

    // Devuelvo solo el texto de publicidad; recibo producto y audiencia por la URL.
    @GetMapping(value = "/api/agrosmart/publicidad", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> generarPublicidad(@RequestParam String producto,
                                          @RequestParam String audiencia) {
        return publicidadService.generarPublicidad(producto, audiencia);
    }
}
