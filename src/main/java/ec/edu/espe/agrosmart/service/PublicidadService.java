package ec.edu.espe.agrosmart.service;

import java.time.Duration;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

// Envuelvo la llamada bloqueante a la IA para exponerla reactiva y tolerante a fallos.
@Service
public class PublicidadService {

    private final AgroSmartAIService aiService;

    public PublicidadService(AgroSmartAIService aiService) {
        this.aiService = aiService;
    }

    public Mono<String> generarPublicidad(String producto, String audiencia) {
        return Mono.fromCallable(() -> aiService.generarPublicidad(producto, audiencia)) // Difiero la llamada.
                .subscribeOn(Schedulers.boundedElastic())   // La llamada HTTP a la IA bloquea: la saco del event loop.
                .timeout(Duration.ofSeconds(30))            // Si el modelo tarda demasiado, corto.
                .onErrorResume(e -> Mono.just(              // Si el proveedor falla, no tumbo el endpoint: doy respaldo.
                        "Publicidad no disponible en este momento (" + e.getClass().getSimpleName() + ")"));
    }
}
