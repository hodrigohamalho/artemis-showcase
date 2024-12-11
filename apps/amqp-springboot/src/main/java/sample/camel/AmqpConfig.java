package sample.camel;

import com.ibm.mq.jakarta.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;

import org.apache.camel.component.jms.JmsComponent;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;


@Configuration
public class AmqpConfig {

    // configuration of the AMQP connection factory.
    @Value("${AMQP_SERVICE_USERNAME}")
    private String artemisUserName;
   
    @Value("${AMQP_SERVICE_PASSWORD}")
    private String artemisPassword;

    @Value("${AMQP_REMOTE_URI}")
    private String remoteUri;

    @Value("${MQ_HOSTNAME}")
    private String mqHostname;

    @Value("${MQ_CHANNEL}")
    private String mqChannel;

    @Value("${MQ_QUEUE_MANAGER}")
    private String mqQueueManager;

    @Value("${MQ_PORT}")
    private int mqPort;

    @Value("${MQ_USERNAME}")
    private String mqUserName;

    @Value("${MQ_PASSWORD}")
    private String mqPassword;
    
    // @Bean
    public org.apache.qpid.jms.JmsConnectionFactory amqpConnectionFactory() {
        org.apache.qpid.jms.JmsConnectionFactory jmsConnectionFactory = new org.apache.qpid.jms.JmsConnectionFactory();
        jmsConnectionFactory.setRemoteURI(remoteUri);
        jmsConnectionFactory.setUsername(artemisUserName);
        jmsConnectionFactory.setPassword(artemisPassword);
        return jmsConnectionFactory;
    }

    @Bean 
    public JmsComponent amqp() throws Exception {
        org.apache.qpid.jms.JmsConnectionFactory jmsConnectionFactory = new org.apache.qpid.jms.JmsConnectionFactory();
        jmsConnectionFactory.setRemoteURI(remoteUri);
        jmsConnectionFactory.setUsername(artemisUserName);
        jmsConnectionFactory.setPassword(artemisPassword);
        // Create the Camel JMS component and wire it to our Artemis connectionfactory         
        JmsComponent jms = new JmsComponent();
        jms.setConnectionFactory(jmsConnectionFactory);
        return jms;
    }

    /* Recommendation is to use connection pooling. 
       By using a named bean we could directly reference the connection factory
       in camel.component.amqp.connection-factory = #connectionPoolFactory
       but its technically not needed if there is only one connectionFactory registered in 
       the Spring Boot registry.
    */ 
    @Bean(name = "connectionPoolFactory", initMethod = "start", destroyMethod = "stop")
    public JmsPoolConnectionFactory jmsPoolConnectionFactory() {
        JmsPoolConnectionFactory jmsPoolConnectionFactory = new JmsPoolConnectionFactory();
        jmsPoolConnectionFactory.setConnectionFactory(amqpConnectionFactory());
        return jmsPoolConnectionFactory;
    }

    @Bean 
    public JmsComponent wmq() {
        JmsComponent jmsComponent = new JmsComponent();
        jmsComponent.setConnectionFactory(mqQueueConnectionFactory());
        return jmsComponent;
    }

    @Bean 
    public MQQueueConnectionFactory mqQueueConnectionFactory() {
        MQQueueConnectionFactory cf = new MQQueueConnectionFactory();
        
        try {
            cf.setHostName(mqHostname);
            cf.setPort(mqPort);
            cf.setQueueManager(mqQueueManager);
            cf.setChannel(mqChannel);
            cf.setTransportType(WMQConstants.WMQ_CM_CLIENT);
            cf.setStringProperty(WMQConstants.USERID, mqUserName);
            cf.setStringProperty(WMQConstants.PASSWORD, mqPassword);
            cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cf;
    }

    // @Bean 
    // public UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter(MQQueueConnectionFactory mqQueueConnectionFactory) {
    //     UserCredentialsConnectionFactoryAdapter adapter = new UserCredentialsConnectionFactoryAdapter();
    //     adapter.setUsername(mqUserName);
    //     adapter.setPassword(mqPassword);
    //     adapter.setTargetConnectionFactory(mqQueueConnectionFactory);

    //     return adapter; 
    // }
    
}
