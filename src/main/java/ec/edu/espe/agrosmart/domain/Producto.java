package ec.edu.espe.agrosmart.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Modelo de dominio 100% inmutable (distinto de ProductoEntity, que es el ORM
 * mutable). La clase es final, todos los atributos son private final y no hay
 * setters. Aplico copia defensiva de la lista de correos en el constructor y
 * en el getter.
 */
public final class Producto {

    private final Long id;
    private final String nombre;
    private final String categoria;
    private final BigDecimal precioUsd;
    private final List<String> correosNotificacion;

    public Producto(Long id, String nombre, String categoria,
                    BigDecimal precioUsd, List<String> correosNotificacion) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precioUsd = precioUsd;
        // Copia defensiva de ENTRADA: guardo mi propia copia. Si guardara la
        // referencia recibida, quien me la paso podria seguir mutandola por
        // fuera y cambiaria mi estado interno.
        this.correosNotificacion = new ArrayList<>(correosNotificacion);
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public BigDecimal getPrecioUsd() {
        return precioUsd;
    }

    public List<String> getCorreosNotificacion() {
        // Copia defensiva de SALIDA: devuelvo una vista de solo lectura sobre
        // una copia. Asi quien la recibe no puede modificar mi estado interno.
        return Collections.unmodifiableList(new ArrayList<>(correosNotificacion));
    }

    @Override
    public String toString() {
        return "Producto{id=" + id + ", nombre='" + nombre + '\'' +
                ", categoria='" + categoria + '\'' + ", precioUsd=" + precioUsd +
                ", correosNotificacion=" + correosNotificacion + '}';
    }
}
