package eu.iotfeds.marketplace.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import eu.h2020.symbiote.core.cci.PlatformRegistryResponse;
import eu.h2020.symbiote.core.cci.SspRegistryResponse;
import eu.h2020.symbiote.security.communication.payloads.ErrorResponseContainer;
import eu.h2020.symbiote.security.communication.payloads.OwnedService;
import eu.h2020.symbiote.security.communication.payloads.UserManagementRequest;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Class used for all internal communication using RabbitMQ AMQP implementation.
 *
 * RabbitManager works as a Spring Bean, and should be used via autowiring.
 * It uses properties taken from CoreConfigServer to set up communication 
 * (exchange parameters, routing keys etc.)
 */
@Component
public class RabbitManager {
    private static Log log = LogFactory.getLog(RabbitManager.class);

    @Value("${rabbit.host}")
    private String rabbitHost;
    @Value("${rabbit.username}")
    private String rabbitUsername;
    @Value("${rabbit.password}")
    private String rabbitPassword;
    @Value("${rabbit.timeoutMillis}")
    private Long rabbitTimeout;

    @Value("${rabbit.routingKey.ownedservices.request}")
    private String getOwnedPlatformDetailsRoutingKey;

    // ------------ Registry communication ----------------
    @Value("${rabbit.exchange.platform.name}")
    private String platformExchangeName;
    @Value("${rabbit.exchange.platform.type}")
    private String platformExchangeType;
    @Value("${rabbit.exchange.platform.durable}")
    private boolean platformExchangeDurable;
    @Value("${rabbit.exchange.platform.autodelete}")
    private boolean platformExchangeAutodelete;
    @Value("${rabbit.exchange.platform.internal}")
    private boolean platformExchangeInternal;

    @Value("${rabbit.exchange.platform.name}")
    private String informationModelExchangeName;
    @Value("${rabbit.exchange.platform.type}")
    private String informationModelExchangeType;
    @Value("${rabbit.exchange.platform.durable}")
    private boolean informationModelExchangeDurable;
    @Value("${rabbit.exchange.platform.autodelete}")
    private boolean informationModelExchangeAutodelete;
    @Value("${rabbit.exchange.platform.internal}")
    private boolean informationModelExchangeInternal;

    @Value("${rabbit.exchange.mapping.name}")
    private String mappingExchangeName;
    @Value("${rabbit.exchange.mapping.type}")
    private String mappingExchangeType;
    @Value("${rabbit.exchange.mapping.durable}")
    private boolean mappingExchangeDurable;
    @Value("${rabbit.exchange.mapping.autodelete}")
    private boolean mappingExchangeAutodelete;
    @Value("${rabbit.exchange.mapping.internal}")
    private boolean mappingExchangeInternal;

    @Value("${rabbit.exchange.ssp.name}")
    private String sspExchangeName;
    @Value("${rabbit.exchange.ssp.type}")
    private String sspExchangeType;
    @Value("${rabbit.exchange.ssp.durable}")
    private boolean sspExchangeDurable;
    @Value("${rabbit.exchange.ssp.autodelete}")
    private boolean sspExchangeAutodelete;
    @Value("${rabbit.exchange.ssp.internal}")
    private boolean sspExchangeInternal;

    @Value("${rabbit.exchange.resource.name}")
    private String resourceExchangeName;
    @Value("${rabbit.exchange.resource.type}")
    private String resourceExchangeType;
    @Value("${rabbit.exchange.resource.durable}")
    private boolean resourceExchangeDurable;
    @Value("${rabbit.exchange.resource.autodelete}")
    private boolean resourceExchangeAutodelete;
    @Value("${rabbit.exchange.resource.internal}")
    private boolean resourceExchangeInternal;

    @Value("${rabbit.routingKey.platform.platformDetailsRequested}")
    private String platformDetailsRequestedRoutingKey;

    @Value("${rabbit.routingKey.resource.clearDataRequested}")
    private String clearPlatformResourcesRoutingKey;

    @Value("${rabbit.routingKey.ssp.sspDetailsRequested}")
    private String sspDetailsRequestedRoutingKey;

    // ------------ Core AAM communication ----------------

    @Value("${rabbit.exchange.aam.name}")
    private String aamExchangeName;
    @Value("${rabbit.exchange.aam.type}")
    private String aamExchangeType;
    @Value("${rabbit.exchange.aam.durable}")
    private boolean aamExchangeDurable;
    @Value("${rabbit.exchange.aam.autodelete}")
    private boolean aamExchangeAutodelete;
    @Value("${rabbit.exchange.aam.internal}")
    private boolean aamExchangeInternal;

    // ------------ Federations ----------------

    @Value("${rabbit.exchange.federation.name}")
    private String federationExchangeName;
    @Value("${rabbit.exchange.federation.type}")
    private String federationExchangeType;
    @Value("${rabbit.exchange.federation.durable}")
    private boolean federationExchangeDurable;
    @Value("${rabbit.exchange.federation.autodelete}")
    private boolean federationExchangeAutodelete;
    @Value("${rabbit.exchange.federation.internal}")
    private boolean federationExchangeInternal;


    // ----------------------------------------------------

    private Connection connection;
    private Channel channel;

    @Autowired
    private ObjectMapper mapper;


    /**
     * Method used to initialise RabbitMQ connection and declare all required exchanges.
     * This method should be called once, after bean initialization (so that properties from CoreConfigServer are obtained),
     * but before using RabbitManager to send any message.
     */
    public void initCommunication() {


        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(this.rabbitHost);
            factory.setUsername(this.rabbitUsername);
            factory.setPassword(this.rabbitPassword);

            this.connection = factory.newConnection();

            this.channel = this.connection.createChannel();
            this.channel.exchangeDeclare(this.platformExchangeName,
                    this.platformExchangeType,
                    this.platformExchangeDurable,
                    this.platformExchangeAutodelete,
                    this.platformExchangeInternal,
                    null);

            this.channel.exchangeDeclare(this.informationModelExchangeName,
                    this.informationModelExchangeType,
                    this.informationModelExchangeDurable,
                    this.informationModelExchangeAutodelete,
                    this.informationModelExchangeInternal,
                    null);

            this.channel.exchangeDeclare(this.mappingExchangeName,
                    this.mappingExchangeType,
                    this.mappingExchangeDurable,
                    this.mappingExchangeAutodelete,
                    this.mappingExchangeInternal,
                    null);

            this.channel.exchangeDeclare(this.sspExchangeName,
                    this.sspExchangeType,
                    this.sspExchangeDurable,
                    this.sspExchangeAutodelete,
                    this.sspExchangeInternal,
                    null);

            this.channel.exchangeDeclare(this.resourceExchangeName,
                    this.resourceExchangeType,
                    this.resourceExchangeDurable,
                    this.resourceExchangeAutodelete,
                    this.resourceExchangeInternal,
                    null);

            this.channel.exchangeDeclare(this.aamExchangeName,
                    this.aamExchangeType,
                    this.aamExchangeDurable,
                    this.aamExchangeAutodelete,
                    this.aamExchangeInternal,
                    null);

            this.channel.exchangeDeclare(this.federationExchangeName,
                    this.federationExchangeType,
                    this.federationExchangeDurable,
                    this.federationExchangeAutodelete,
                    this.federationExchangeInternal,
                    null);

        } catch (IOException | TimeoutException e) {
            log.error("", e);
        }
    }

    /**
     * Cleanup method, used to close RabbitMQ channel and connection.
     */
    @PreDestroy
    private void cleanup() {
        try {
            if (this.channel != null && this.channel.isOpen())
                this.channel.close();
            if (this.connection != null && this.connection.isOpen())
                this.connection.close();
        } catch (IOException | TimeoutException e) {
            log.error("", e);
        }
    }


    /**
     * Method used to send message via RPC (Remote Procedure Call) pattern.
     * In this implementation it covers asynchronous Rabbit communication with synchronous one, as it is used by conventional REST facade.
     * Before sending a message, a temporary response queue is declared and its name is passed along with the message.
     * When a consumer handles the message, it returns the result via the response queue.
     * Since this is a synchronous pattern, it uses timeout of 20 seconds. 
     * If the response doesn't come in that time, the method returns with null result.
     *
     * @param exchangeName name of the exchange to send message to
     * @param routingKey   routing key to send message to
     * @param message      message to be sent
     * @param contentType  the content type of the message
     * @return response from the consumer or null if timeout occurs
     */
    public String sendRpcMessage(String exchangeName, String routingKey, String message, String contentType) {
        String correlationId = UUID.randomUUID().toString();
        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                if (properties.getCorrelationId().equals(correlationId)) {
                    response.offer(new String(body, "UTF-8"));
                }
            }
        };

        try {
            // log.debug("Sending message...");

            String replyQueueName = this.channel.queueDeclare().getQueue();

            AMQP.BasicProperties props = new AMQP.BasicProperties()
                    .builder()
                    .correlationId(correlationId)
                    .contentType(contentType)
                    .replyTo(replyQueueName)
                    .build();

            this.channel.basicPublish(exchangeName, routingKey, props, message.getBytes());
            this.channel.basicConsume(replyQueueName, true, consumer);


            return response.poll(rabbitTimeout, TimeUnit.MILLISECONDS);
        } catch (IOException | InterruptedException e) {
            log.warn("", e);
        } finally {
            try {
                this.channel.basicCancel(consumer.getConsumerTag());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

    // #################################################
    // Interaction with Registry
    // #################################################

    /**
     * Helper method that provides JSON marshalling, unmarshalling and RabbitMQ communication with the Registry
     *
     * @param platformId     id of the platform which want the details
     * @return response from the consumer or null if timeout occurs
     */
    public PlatformRegistryResponse sendGetPlatformDetailsMessage(String platformId) throws CommunicationException {

        log.debug("sendGetPlatformDetailsMessage for platform: " + platformId);

        String responseMsg = this.sendRpcMessage(this.platformExchangeName, this.platformDetailsRequestedRoutingKey,
                platformId, "text/plain");

        if (responseMsg == null)
            return null;

        try {
            PlatformRegistryResponse response = mapper.readValue(responseMsg, PlatformRegistryResponse.class);
            log.trace("Received response from Registry.");
            return response;

        } catch (Exception e){

            log.error("Error in response from Registry.", e);
            throw new CommunicationException(e);
        }
    }

    /**
     * Helper method that provides JSON marshalling, unmarshalling and RabbitMQ communication with the Registry
     *
     * @param sspId     id of the ssp which want the details
     * @return response from the consumer or null if timeout occurs
     */
    public SspRegistryResponse sendGetSSPDetailsMessage(String sspId) throws CommunicationException {

        log.debug("sendGetSSPDetailsMessage for platform: " + sspId);

        String responseMsg = this.sendRpcMessage(this.sspExchangeName, this.sspDetailsRequestedRoutingKey,
                sspId, "text/plain");

        if (responseMsg == null)
            return null;

        try {
            SspRegistryResponse response = mapper.readValue(responseMsg, SspRegistryResponse.class);
            log.trace("Received response from Registry.");
            return response;

        } catch (Exception e){

            log.error("Error in response from Registry.", e);
            throw new CommunicationException(e);
        }
    }

    /**
     * Method used to send RPC request to get a platform owner's platform details.
     *
     * @param request  request for user management
     */
    public Set<OwnedService> sendOwnedServiceDetailsRequest(UserManagementRequest request)
            throws CommunicationException {

        log.debug("sendOwnedServiceDetailsRequest to AAM: " + ReflectionToStringBuilder.toString(request));

        try {
            String message = mapper.writeValueAsString(request);

            String responseMsg = this.sendRpcMessage(this.aamExchangeName, this.getOwnedPlatformDetailsRoutingKey, message, "application/json");

            if (responseMsg == null)
                return null;

            try {
                Set<OwnedService> response = mapper.readValue(responseMsg,
                        mapper.getTypeFactory().constructCollectionType(Set.class, OwnedService.class));
                log.trace("Received platform owner details response from AAM.");
                return response;

            } catch (Exception e){

                log.error("Error in owner platform details response from AAM.", e);
                ErrorResponseContainer error = mapper.readValue(responseMsg, ErrorResponseContainer.class);
                throw new CommunicationException(error.getErrorMessage());
            }
        } catch (IOException e) {
            log.error("Failed (un)marshalling of rpc resource message.", e);
        }
        return null;
    }

}