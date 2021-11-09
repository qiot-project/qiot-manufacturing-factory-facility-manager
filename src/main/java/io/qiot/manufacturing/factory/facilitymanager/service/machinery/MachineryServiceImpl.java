package io.qiot.manufacturing.factory.facilitymanager.service.machinery;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;

import io.qiot.manufacturing.all.commons.domain.landscape.MachineryDTO;
import io.qiot.manufacturing.all.commons.domain.landscape.SubscriptionResponse;
import io.qiot.manufacturing.all.commons.exception.SubscriptionException;
import io.qiot.manufacturing.datacenter.commons.domain.subscription.MachinerySubscriptionRequest;
import io.qiot.manufacturing.factory.commons.domain.registration.EdgeSubscriptionRequest;
import io.qiot.manufacturing.factory.facilitymanager.domain.pojo.MachineryBean;
import io.qiot.manufacturing.factory.facilitymanager.persistence.MachineryRepository;
import io.qiot.manufacturing.factory.facilitymanager.service.factory.FactoryService;
import io.qiot.manufacturing.factory.facilitymanager.service.registration.PlantManagerClient;
import io.qiot.manufacturing.factory.facilitymanager.util.converter.MachineryConverter;
import io.qiot.ubi.all.registration.client.RegistrationServiceClient;
import io.qiot.ubi.all.registration.domain.CertificateRequest;
import io.qiot.ubi.all.registration.domain.CertificateResponse;

/**
 * @author andreabattaglia
 *
 */
@ApplicationScoped
class MachineryServiceImpl implements MachineryService {

    @Inject
    Logger LOGGER;

    @Inject
    FactoryService factoryService;

    @Inject
    MachineryRepository machineryRepository;

    @Inject
    MachineryConverter converter;

    @Inject
    @RestClient
    RegistrationServiceClient registrationServiceClient;

    @Inject
    @RestClient
    PlantManagerClient plantManagerClient;

    @Override
    public SubscriptionResponse subscribe(EdgeSubscriptionRequest request)
            throws SubscriptionException {

        SubscriptionResponse subscriptionResponse = null;

        try {

            /*
             * 1 - Provision machinery certificates from the local (factory)
             * issuer (delegate)
             */
            CertificateRequest certificateRequest = new CertificateRequest();
            certificateRequest.domain = "." + factoryService.getFactoryId();
            ;
            certificateRequest.serial = request.serial;
            certificateRequest.name = request.name;
            certificateRequest.keyStorePassword = request.keyStorePassword;
            certificateRequest.ca = false;
            CertificateResponse certificateResponse = registrationServiceClient
                    .provisionCertificate(certificateRequest);

            LOGGER.debug("Certificates for the new Machinery created:"//
                    + "\nKEYSTORE:\n{}"//
                    + "\n"//
                    + "\nTRUSTSTORE:\n{}", //
                    certificateResponse.keystore,
                    certificateResponse.truststore);

            /*
             * 2 - Subscribe machinery (Globally)
             */
            MachinerySubscriptionRequest msRequest = new MachinerySubscriptionRequest();
            msRequest.factoryId = factoryService.getFactoryId();
            msRequest.name = request.name;
            msRequest.serial = request.serial;
            msRequest.keyStorePassword = request.keyStorePassword;
            while (subscriptionResponse == null) {
                // TODO: put sleep time in application.properties
                long sleepTime = 2000;
                try {
                    subscriptionResponse = plantManagerClient
                            .subscribeMachinery(msRequest);
                } catch (Exception e) {
                    LOGGER.info("An error occurred registering the machinery. "
                            + "Retrying in {} millis.\n Error message: {}",
                            sleepTime, e.getMessage());
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            /*
             * add certs to subs response
             */
            subscriptionResponse.keystore = certificateResponse.keystore;
            subscriptionResponse.truststore = certificateResponse.truststore;

            /*
             * persist & flush the new entity locally
             */
            MachineryBean machineryBean = new MachineryBean();
            machineryBean.serial = request.serial;
            machineryBean.name = request.name;
            machineryBean.id = subscriptionResponse.id;
            machineryBean.registeredOn = subscriptionResponse.subscribedOn;

            machineryRepository.persist(machineryBean);

        } catch (Exception e) {
            // machineryRepository.delete(machineryBean);
            LOGGER.error(
                    "An error occurred retrieving the certificates for the factory.",
                    e);
            // TODO: improve exception handling
            throw new SubscriptionException(e);
        }

        return subscriptionResponse;
    }

    @Override
    public MachineryDTO getById(UUID id) {
        MachineryBean machineryBean = machineryRepository.findById(id);
        return converter.sourceToDest(machineryBean);
    }

    @Override
    public List<MachineryDTO> getAllStations() {
        List<MachineryDTO> machineryDTOs = null;
        List<MachineryBean> machineryBeans = machineryRepository.findAll()
                .list();
        machineryDTOs = converter.allSourceToDest(machineryBeans);
        return machineryDTOs;
    }
}
