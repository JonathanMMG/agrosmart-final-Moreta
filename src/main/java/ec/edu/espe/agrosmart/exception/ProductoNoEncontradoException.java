package ec.edu.espe.agrosmart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// La lanzo cuando busco un producto por id que no existe; con NOT_FOUND WebFlux la vuelve un HTTP 404.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException(Long id) {
        super("No existe el producto con id " + id);
    }
}
