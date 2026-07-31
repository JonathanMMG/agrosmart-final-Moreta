package ec.edu.espe.agrosmart.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ec.edu.espe.agrosmart.domain.Producto;
import ec.edu.espe.agrosmart.entity.ProductoEntity;
import ec.edu.espe.agrosmart.exception.ProductoNoEncontradoException;
import ec.edu.espe.agrosmart.repository.ProductoRepository;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

// Pruebo mi flujo reactivo con StepVerifier; mockeo el repositorio para no tocar Postgres.
class ProductoServiceTest {

    private List<ProductoEntity> datosDePrueba() {
        return List.of(
                new ProductoEntity("Cacao fino", new BigDecimal("120.50"), 500, "Cacao", "a@b.ec"),
                new ProductoEntity("Cacao ccn", new BigDecimal("95.00"), 800, "Cacao", "c@d.ec"),
                new ProductoEntity("Cacao premium", new BigDecimal("150.75"), 300, "Cacao", "e@f.ec"),
                new ProductoEntity("Cacao precio cero", BigDecimal.ZERO, 200, "Cacao", "g@h.ec"),
                new ProductoEntity("Cacao sin correos", new BigDecimal("80.00"), 150, "Cacao", ""));
    }

    @Test
    void obtenerProductosComercializables_conTresValidosYDosInvalidos_debeEmitirSoloLosValidos() {
        // Arrange
        ProductoRepository repo = Mockito.mock(ProductoRepository.class);
        Mockito.when(repo.findAll()).thenReturn(datosDePrueba());
        ProductoService service = new ProductoService(repo);

        // Act
        Flux<Producto> flujo = service.obtenerProductosComercializables();

        // Assert
        StepVerifier.create(flujo)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void obtenerProductosComercializables_conTodosInvalidos_debeEmitirElGenerico() {
        // Arrange: solo productos invalidos (precio 0 y correos vacios)
        ProductoRepository repo = Mockito.mock(ProductoRepository.class);
        Mockito.when(repo.findAll()).thenReturn(List.of(
                new ProductoEntity("Cacao precio cero", BigDecimal.ZERO, 200, "Cacao", "g@h.ec"),
                new ProductoEntity("Cacao sin correos", new BigDecimal("80.00"), 150, "Cacao", "")));
        ProductoService service = new ProductoService(repo);

        // Act + Assert: el filtro deja el flujo vacio y sale el generico
        StepVerifier.create(service.obtenerProductosComercializables())
                .expectNextMatches(p -> "PRODUCTO GENERICO".equals(p.getNombre()))
                .verifyComplete();
    }

    @Test
    void buscarPorId_conIdInexistente_debeTerminarEnError() {
        // Arrange
        ProductoRepository repo = Mockito.mock(ProductoRepository.class);
        Mockito.when(repo.findById(999L)).thenReturn(Optional.empty());
        ProductoService service = new ProductoService(repo);

        // Act + Assert
        StepVerifier.create(service.buscarPorId(999L))
                .expectError(ProductoNoEncontradoException.class)
                .verify();
    }
}
