package ec.edu.espe.agrosmart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.edu.espe.agrosmart.entity.ProductoEntity;

/**
 * Repositorio JPA de productos. Al extender JpaRepository obtengo las
 * operaciones basicas (findAll, findById, save, count...) sin escribir SQL.
 * Es BLOQUEANTE: cada llamada espera la respuesta de la base, por eso en la
 * Fase 4 lo envuelvo en Schedulers.boundedElastic() para no bloquear el event loop.
 */
public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
}
