package ec.edu.espe.agrosmart.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import ec.edu.espe.agrosmart.entity.ProductoEntity;
import ec.edu.espe.agrosmart.repository.ProductoRepository;

/**
 * Siembra los datos iniciales al arrancar. Todos los productos son de mi
 * categoria asignada (Cacao). Es idempotente: solo inserta si la tabla esta
 * vacia (count == 0), para no duplicar registros en cada arranque.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductoRepository repository;

    public DataInitializer(ProductoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return; // ya sembrado
        }

        List<ProductoEntity> productos = List.of(
                // 3 VALIDOS: precio > 0 y con al menos un correo
                new ProductoEntity("Cacao fino de aroma", new BigDecimal("120.50"), 500,
                        "Cacao", "ventas@agrosmart.ec"),
                new ProductoEntity("Cacao nacional CCN-51", new BigDecimal("95.00"), 800,
                        "Cacao", "exportacion@agrosmart.ec,ventas@agrosmart.ec"),
                new ProductoEntity("Cacao organico premium", new BigDecimal("150.75"), 300,
                        "Cacao", "premium@agrosmart.ec"),

                // 1 INVALIDO: precio_usd = 0 (aunque tenga correos)
                new ProductoEntity("Cacao en grano seco", new BigDecimal("0.00"), 200,
                        "Cacao", "info@agrosmart.ec"),

                // 1 INVALIDO: lista de correos vacia (aunque tenga precio > 0)
                new ProductoEntity("Cacao rustico", new BigDecimal("80.00"), 150,
                        "Cacao", ""));

        repository.saveAll(productos);
    }
}
