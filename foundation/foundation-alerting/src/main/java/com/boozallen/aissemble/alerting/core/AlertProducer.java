package com.boozallen.aissemble.alerting.core;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Alerting::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.alerting.core.Alert.Status;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;


/**
 * This class acts as our interface between the imperative parts of our code
 * (regular CDI beans and Java) to the reactive parts of our code (anything with
 * an @Incoming or @Outgoing annotation). Reactive messaging uses the concept of
 * {@link Emitter} and {@link Channel} to send the messages or payloads between
 * the non-reactive parts of the code to the reactive parts.
 * 
 * In order to use the {@link Emitter} and {@link Channel}, you must either have
 * an matching @Incoming of the same channel somewhere in your code, or you must
 * have an outbound connector for that channel configured for the channel
 * (mp.messaging.outgoing.alerts…​). The outbound connector could then be
 * configured to JMS, Kafka, etc so you could connect to the broker of your
 * choice. If you do not specify, then the messaging is done in-memory. The
 * channel is also set up to be a queue by default. The @Broadcast annotation is
 * currently experimental, but demonstrates how a channel is configured to
 * conform to the publish/subscribe pattern of a topic rather than the 1 message
 * to 1 consumer pattern of a queue.
 * 
 */
@ApplicationScoped
public class AlertProducer implements AlertProducerApi {

    private static final Logger logger = LoggerFactory.getLogger(AlertProducer.class);

    public static final String ALERT_TOPIC = "alerts";

    @Inject
    @Broadcast
    @Channel(ALERT_TOPIC)
    Emitter<Alert> emitter;

    /**
     * Publishes an alert to the alert topic
     * 
     * @param status
     *            the status of the alert to publish
     * @param message
     *            the message of the alert to publish
     * @return the id of the published alert
     */
    @Override
    public UUID sendAlert(Status status, String message) {
        // create alert object with given status and message
        Alert alert = new Alert();
        alert.setId(UUID.randomUUID());
        alert.setStatus(status);
        alert.setMessage(message);

        try {
            // publish the alert record
            // can register callbacks when message is acknowledged
            // https://smallrye.io/smallrye-reactive-messaging/smallrye-reactive-messaging/2.1/emitter/emitter.html#streams
            emitter.send(Message.of(alert));
            logger.info("Published {} alert: {}", status, message);
        } catch (Exception e) {
            logger.error("Error publishing alert", e);
        }

        return alert.getId();
    }

}
