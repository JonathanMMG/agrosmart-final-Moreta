package ec.edu.espe.agrosmart.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Pruebo mi modelo inmutable: getters y las dos copias defensivas.
class ProductoTest {

    @Test
    void getters_conProductoCreado_devuelvenLoQueRecibioElConstructor() {
        // Arrange
        List<String> correos = new ArrayList<>();
        correos.add("ventas@agrosmart.ec");

        // Act
        Producto producto = new Producto(1L, "Cacao fino", "Cacao",
                new BigDecimal("120.50"), correos);

        // Assert
        assertEquals(1L, producto.getId());
        assertEquals("Cacao fino", producto.getNombre());
        assertEquals("Cacao", producto.getCategoria());
        assertEquals(new BigDecimal("120.50"), producto.getPrecioUsd());
        assertEquals(correos, producto.getCorreosNotificacion());
    }

    @Test
    void getCorreosNotificacion_alMutarLaListaOriginal_noDebeAfectarAlProducto() {
        // Arrange
        List<String> correos = new ArrayList<>();
        correos.add("ventas@agrosmart.ec");
        Producto producto = new Producto(1L, "Cacao fino", "Cacao",
                new BigDecimal("120.50"), correos);

        // Act: muto la lista original despues de construir el objeto
        correos.add("intruso@mail.com");

        // Assert: la copia de entrada protegio el estado interno
        assertEquals(1, producto.getCorreosNotificacion().size());
        assertNotSame(correos, producto.getCorreosNotificacion());
    }

    @Test
    void getCorreosNotificacion_alIntentarModificarla_debeSerDeSoloLectura() {
        // Arrange
        Producto producto = new Producto(1L, "Cacao fino", "Cacao",
                new BigDecimal("120.50"), List.of("ventas@agrosmart.ec"));

        // Act + Assert: la copia de salida es inmodificable
        assertThrows(UnsupportedOperationException.class,
                () -> producto.getCorreosNotificacion().add("otro@mail.com"));
    }
}
