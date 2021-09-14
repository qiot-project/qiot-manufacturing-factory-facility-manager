package io.qiot.manufacturing.factory.facilitymanager.rest;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;

import io.qiot.manufacturing.factory.facilitymanager.service.factory.FactoryService;

/**
 * Validation through hibernate validator:
 * https://quarkus.io/guides/validation#rest-end-point-validation
 * 
 * @author andreabattaglia
 *
 */
@Path("/factory")
public class FactoryResource {

    @Inject
    Logger LOGGER;

    @Inject
    FactoryService factoryService;

    @GET
    @Path("/id")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public UUID getFactoryId() {
        return factoryService.getFactoryId();
    }
}