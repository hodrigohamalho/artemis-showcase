package sample.camel;

import java.util.Arrays;
import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class SampleAutowiredAmqpRoute extends RouteBuilder {

    @Value("${queue.whitelist}")
    private String whitelistQueues;

    @Override
    public void configure() throws Exception {

        // Cria uma lista de filas permitidas
        List<String> queues = Arrays.asList(whitelistQueues.split(","));

        for (String queue : queues) {
            queue = queue.trim();

            // Rota para consumir mensagens da fila do IBM MQ e enviar para o ActiveMQ Artemis
            from("wmq:queue:" + queue)
                .routeId("replicate-" + queue)
                .log("Replicando mensagem da fila: " + queue + " no IBM para " + queue + " no Artemis")
                .to("amqp:queue:" + queue);
        }

        // // SENDING MESSAGES TO IBM
        // from("timer:produce-ibm")
        //     .id("producer-ibm")
        //     .setBody(constant("Hello from Camel for IBM"))
        //     .to("wmq:queue:SCIENCEQUEUE")
        //     .log("Message sent to IBM Broker");
        
    }

}
