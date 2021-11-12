package io.qiot.manufacturing.factory.facilitymanager.rest;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.qiot.manufacturing.all.commons.domain.landscape.MachineryDTO;
import io.qiot.manufacturing.all.commons.domain.landscape.SubscriptionResponse;
import io.qiot.manufacturing.all.commons.exception.SubscriptionException;
import io.qiot.manufacturing.datacenter.commons.domain.subscription.MachinerySubscriptionRequest;
import io.qiot.manufacturing.factory.commons.domain.registration.EdgeSubscriptionRequest;
import io.qiot.manufacturing.factory.facilitymanager.service.factory.SubscriptionService;
import io.qiot.manufacturing.factory.facilitymanager.service.machinery.MachineryService;
import io.qiot.manufacturing.factory.facilitymanager.service.machinery.MachinerySubscriptionStreamProducer;

/**
 * Validation through hibernate validator:
 * https://quarkus.io/guides/validation#rest-end-point-validation
 * 
 * @author andreabattaglia
 *
 */
@Path("/machinery")
public class MachineryResource {

    @Inject
    Logger LOGGER;

    @Inject
    MachineryService machineryService;

    @Inject
    SubscriptionService subscriptionService;

    @Inject
    MachinerySubscriptionStreamProducer producer;

    @GET
    @Path("/all")
    public List<MachineryDTO> getAll() {
        return machineryService.getAllStations();
    }

    @GET
    @Path("/id/{id}")
    public MachineryDTO getById(@PathParam("id") @NotNull UUID id) {
        return machineryService.getById(id);
    }

    @POST
    public SubscriptionResponse subscribe(
            @Valid EdgeSubscriptionRequest request)
            throws SubscriptionException, JsonProcessingException {
        LOGGER.info("Received subscription request by machinery {}",
                request.name);

        /*
         * Subscribe the new machinery
         */
        SubscriptionResponse response = machineryService.subscribe(request);

        /*
         * Delegate to subscription notification service
         */
        MachinerySubscriptionRequest msRequest = new MachinerySubscriptionRequest();
        msRequest.id = response.id;
        msRequest.factoryId = subscriptionService.getFactoryId();
        msRequest.name = request.name;
        msRequest.serial = request.serial;
        msRequest.keyStorePassword = request.keyStorePassword;
        msRequest.subscribedOn = response.subscribedOn;
        producer.notifyMachinerySubscriptionAsync(msRequest);

        return response;
    }

}