package io.qiot.manufacturing.factory.facilitymanager.service.registration;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.qiot.manufacturing.all.commons.domain.landscape.SubscriptionResponse;
import io.qiot.manufacturing.datacenter.commons.domain.subscription.FactorySubscriptionRequest;
import io.qiot.manufacturing.datacenter.commons.domain.subscription.MachinerySubscriptionRequest;

/**
 * @author andreabattaglia
 *
 */
@Path("/v1")
@RegisterRestClient(configKey = "plant-manager-api")
public interface PlantManagerClient {

    @POST
    @Path("/factory")
    public SubscriptionResponse subscribeFactory(
            FactorySubscriptionRequest request);

    @POST
    @Path("/machinery")
    public SubscriptionResponse subscribeMachinery(
            MachinerySubscriptionRequest request);

}