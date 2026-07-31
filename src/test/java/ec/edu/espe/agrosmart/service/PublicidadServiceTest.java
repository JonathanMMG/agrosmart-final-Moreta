package ec.edu.espe.agrosmart.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;

// Pruebo mi integracion de IA mockeando el modelo: camino feliz y camino de fallo.
class PublicidadServiceTest {

    @Test
    void generarPublicidad_caminoFeliz_debeEmitirElTextoDelModelo() {
        // Arrange
        AgroSmartAIService ia = Mockito.mock(AgroSmartAIService.class);
        Mockito.when(ia.generarPublicidad(any(), any())).thenReturn("Cacao delicioso para Europa");
        PublicidadService service = new PublicidadService(ia);

        // Act + Assert
        StepVerifier.create(service.generarPublicidad("Cacao", "exportadores"))
                .expectNext("Cacao delicioso para Europa")
                .verifyComplete();
    }

    @Test
    void generarPublicidad_cuandoElProveedorFalla_debeEmitirMensajeDeRespaldo() {
        // Arrange: el modelo lanza una excepcion (p. ej. cuota agotada)
        AgroSmartAIService ia = Mockito.mock(AgroSmartAIService.class);
        Mockito.when(ia.generarPublicidad(any(), any()))
                .thenThrow(new RuntimeException("429 Too Many Requests"));
        PublicidadService service = new PublicidadService(ia);

        // Act + Assert: el onErrorResume da el mensaje de respaldo, sin propagar el error
        StepVerifier.create(service.generarPublicidad("Cacao", "exportadores"))
                .expectNextMatches(texto -> texto.contains("no disponible"))
                .verifyComplete();
    }
}
