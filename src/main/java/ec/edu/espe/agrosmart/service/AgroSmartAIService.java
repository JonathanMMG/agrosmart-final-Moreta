package ec.edu.espe.agrosmart.service;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

// Mi contrato declarativo de IA: LangChain4j crea el proxy y llama al modelo por mi.
@AiService
public interface AgroSmartAIService {

    @UserMessage("""
            Redacta una frase publicitaria de máximo 100 caracteres para vender \
            {{producto}} dirigido a {{audiencia}}.""")
    String generarPublicidad(@V("producto") String producto,
                             @V("audiencia") String audiencia);
}
