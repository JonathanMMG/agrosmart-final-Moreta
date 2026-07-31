package ec.edu.espe.agrosmart.domain;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Pruebo mi Predicate IS_VALID: el caso valido y los dos invalidos.
class ProductoFiltersTest {

    @Test
    void isValid_conPrecioPositivoYCorreos_debeSerTrue() {
        Producto producto = new Producto(1L, "Cacao", "Cacao",
                new BigDecimal("10.00"), List.of("a@b.ec"));
        assertTrue(ProductoFilters.IS_VALID.test(producto));
    }

    @Test
    void isValid_conPrecioCero_debeSerFalse() {
        Producto producto = new Producto(1L, "Cacao", "Cacao",
                BigDecimal.ZERO, List.of("a@b.ec"));
        assertFalse(ProductoFilters.IS_VALID.test(producto));
    }

    @Test
    void isValid_conCorreosVacios_debeSerFalse() {
        Producto producto = new Producto(1L, "Cacao", "Cacao",
                new BigDecimal("10.00"), List.of());
        assertFalse(ProductoFilters.IS_VALID.test(producto));
    }
}
