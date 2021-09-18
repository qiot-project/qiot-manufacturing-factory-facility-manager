package io.qiot.manufacturing.factory.facilitymanager.rest;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;

import io.qiot.manufacturing.all.commons.domain.landscape.MachineryDTO;
import io.qiot.manufacturing.all.commons.domain.landscape.SubscriptionResponse;
import io.qiot.manufacturing.all.commons.exception.SubscriptionException;
import io.qiot.manufacturing.factory.commons.domain.registration.EdgeSubscriptionRequest;
import io.qiot.manufacturing.factory.facilitymanager.service.machinery.MachineryService;

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

    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/all")
    public List<MachineryDTO> getAll() {
        return machineryService.getAllStations();
    }

    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/id/{id}")
    public MachineryDTO getById(@PathParam("id") @NotNull UUID id) {
        return machineryService.getById(id);
    }

    @Transactional
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SubscriptionResponse subscribe(
            @Valid EdgeSubscriptionRequest request)
            throws SubscriptionException {
        LOGGER.debug("Received subscription request by machinery {}",
                request.name);
        SubscriptionResponse response = machineryService.subscribe(request);
        return response;
    }

}