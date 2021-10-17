package io.qiot.manufacturing.factory.facilitymanager.service.registration;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.qiot.ubi.all.registration.domain.CertificateRequest;
import io.qiot.ubi.all.registration.domain.CertificateResponse;

/**
 * @author andreabattaglia
 *
 */
@Path("/v1/register")
@RegisterRestClient(configKey = "registration-service-api")
public interface RegistrationServiceClient {

    /**
     * @param request
     * @return
     */
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CertificateResponse provisionCertificate(CertificateRequest request);

}