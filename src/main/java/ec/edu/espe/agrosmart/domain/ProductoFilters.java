package ec.edu.espe.agrosmart.domain;

import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Logica funcional del dominio, definida como variables con lambdas.
 */
public final class ProductoFilters {

    private ProductoFilters() {
        // Clase utilitaria: no se instancia
    }

    /**
     * Predicate: un producto es comercializable si su precio es mayor a 0
     * y su lista de correos no esta vacia. Retorna true o false.
     */
    public static final Predicate<Producto> IS_VALID =
            producto -> producto.getPrecioUsd().compareTo(BigDecimal.ZERO) > 0
                    && !producto.getCorreosNotificacion().isEmpty();

    /**
     * Consumer: efecto de trazabilidad. Imprime por consola el id y el nombre
     * del producto procesado, sin transformarlo.
     */
    public static final Consumer<Producto> LOG_PRODUCTO =
            producto -> System.out.println(
                    "Producto procesado: id=" + producto.getId() + ", nombre=" + producto.getNombre());

    /**
     * Function: devuelve un Producto NUEVO con el nombre en mayusculas.
     * No muta el producto recibido (respeta la inmutabilidad): construye otro.
     */
    public static final Function<Producto, Producto> A_MAYUSCULAS =
            producto -> new Producto(
                    producto.getId(),
                    producto.getNombre().toUpperCase(),
                    producto.getCategoria(),
                    producto.getPrecioUsd(),
                    producto.getCorreosNotificacion());
}
