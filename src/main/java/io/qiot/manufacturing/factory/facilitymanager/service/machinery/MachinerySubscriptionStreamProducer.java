package io.qiot.manufacturing.factory.facilitymanager.service.machinery;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.qiot.manufacturing.datacenter.commons.domain.subscription.MachinerySubscriptionRequest;

@ApplicationScoped
public class MachinerySubscriptionStreamProducer {

    @Inject
    Logger LOGGER;

    @Inject
    ObjectMapper MAPPER;

    @Inject
    @Channel("machinerysubscription")
    Emitter<MachinerySubscriptionRequest> producer;

    @Transactional
    public void notifyMachinerySubscriptionAsync(
            MachinerySubscriptionRequest msRequest)
            throws JsonProcessingException {
        String writeValueAsString = MAPPER.writerWithDefaultPrettyPrinter()
                .writeValueAsString(msRequest);
        LOGGER.info(
                "Sending async notificatio about the new machinery to the plant-manager: \n{}",
                writeValueAsString);
        try {
            producer.send(msRequest);
        } catch (Exception e) {
            LOGGER.warn(
                    "An error occurred sending the new machinery subscription to the datacenter: {}",
                    writeValueAsString);
        }
    }

}
