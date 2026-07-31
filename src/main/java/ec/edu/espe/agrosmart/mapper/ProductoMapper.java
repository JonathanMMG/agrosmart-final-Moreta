package ec.edu.espe.agrosmart.mapper;

import java.util.Arrays;
import java.util.List;

import ec.edu.espe.agrosmart.domain.Producto;
import ec.edu.espe.agrosmart.entity.ProductoEntity;

/**
 * Convierte la entidad del ORM (ProductoEntity) al modelo de dominio inmutable
 * (Producto). Aqui vive la frontera entre la capa de persistencia y el dominio.
 */
public final class ProductoMapper {

    private ProductoMapper() {
    }

    public static Producto toDominio(ProductoEntity entity) {
        return new Producto(
                entity.getIdProducto(),
                entity.getNombreProducto(),
                entity.getCategoria(),
                entity.getPrecioUsd(),
                parsearCorreos(entity.getCorreosNotificacion()));
    }

    /**
     * Convierte la cadena de correos separados por coma en una lista.
     * Una cadena nula o vacia se transforma en una lista vacia.
     */
    private static List<String> parsearCorreos(String correos) {
        if (correos == null || correos.isBlank()) {
            return List.of();
        }
        return Arrays.stream(correos.split(","))
                .map(String::trim)
                .filter(correo -> !correo.isBlank())
                .toList();
    }
}
