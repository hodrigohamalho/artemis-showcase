package org.acme;

import javax.ws.rs.core.MediaType;

import org.apache.camel.builder.RouteBuilder;

public class MyRouteBuilder extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {

        from("timer:generate?period=3000")
            .log("Creating Order")
            .bean(OrderService.class, "generateOrder")
            .log("Order ${body.item} generated")
            .log("Saving in database")
            .to(this.insertOrder)
            .to("direct:send-to-broker");

        from("direct:send-to-broker")
            .choice()
                .when(simple("${body.item} == 'Camel'"))
                    .log("Processing a camel book")
                    .marshal().json() // convert to JSON
                    .to("amqp:queue:camel-book") // send to ARTEMIS in the camel-book queue in JSON
                .otherwise()
                    .log("Processing an activemq book")
                    .marshal().jacksonXml() // convert to XML
                    .to("amqp:queue:activemq-book"); // send to ARTEMIS activemq-book queue in XML

        rest("/say").produces(MediaType.TEXT_PLAIN)
            .get("/hello").to("direct:rest-get");

        from("direct:rest-get").log("teste");

    }

    // Query support
    private String ds = "";//"?dataSource=dataSource";
    private String insertOrder = "sql:insert into orders (item, amount, description, processed) values " +
    	                		"(:#${body.item}, :#${body.amount}, :#${body.description}, false)"+ ds;

}
