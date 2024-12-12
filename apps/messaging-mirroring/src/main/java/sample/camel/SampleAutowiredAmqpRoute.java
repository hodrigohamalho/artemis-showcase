package sample.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class SampleAutowiredAmqpRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // SENDING MESSAGES TO IBM
        from("timer:produce-ibm")
            .id("producer-ibm")
            .setBody(constant("Hello from Camel for IBM"))
            .to("wmq:queue:SCIENCEQUEUE")
            .log("Message sent to IBM Broker");

        // CONSUME MESSAGES FROM IBM AND PRODUCE TO KAFKA
        from("wmq:queue:SCIENCEQUEUE?receiveTimeout=10000")
            .id("consumer-ibm")
            .to("amqp:queue:SCIENCEQUEUE")
            .log("Message consumed from IBM Broker and sent to AMQ Broker");
        
        
    }

}
