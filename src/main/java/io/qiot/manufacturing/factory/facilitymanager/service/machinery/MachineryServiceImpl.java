package io.qiot.manufacturing.factory.facilitymanager.service.machinery;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;

import io.qiot.manufacturing.all.commons.domain.landscape.MachineryDTO;
import io.qiot.manufacturing.all.commons.domain.landscape.SubscriptionResponse;
import io.qiot.manufacturing.all.commons.exception.SubscriptionException;
import io.qiot.manufacturing.factory.commons.domain.registration.EdgeSubscriptionRequest;
import io.qiot.manufacturing.factory.facilitymanager.domain.pojo.MachineryBean;
import io.qiot.manufacturing.factory.facilitymanager.persistence.MachineryRepository;
import io.qiot.manufacturing.factory.facilitymanager.service.factory.SubscriptionService;
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
    SubscriptionService subscriptionService;

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
//
//    @Inject
//    Event<NewMachinerySubscribedEventDTO> event;

    @Transactional
    @Override
    public SubscriptionResponse subscribe(EdgeSubscriptionRequest request)
            throws SubscriptionException {

        SubscriptionResponse subscriptionResponse = null;
        MachineryBean machineryBean = null;

        try {

            /*
             * 1 - persist & flush the new entity locally
             */
            machineryBean = persistEntity(request);

            /*
             * 2 - Provision machinery certificates from the local (factory)
             * issuer (delegate)
             */
            CertificateResponse certificateResponse = requestCertificate(
                    request, machineryBean);

            /*
             * 3 - Add certs to subs response
             */
            LOGGER.info("Building the response to the caller...");

            subscriptionResponse = new SubscriptionResponse();
            subscriptionResponse.id = machineryBean.id;
            subscriptionResponse.keystore = certificateResponse.keystore;
            subscriptionResponse.truststore = certificateResponse.truststore;
            subscriptionResponse.subscribedOn = machineryBean.registeredOn;


        LOGGER.info("Sending the response back to the caller: {}",
                subscriptionResponse);
        return subscriptionResponse;
            

        } catch (Exception e) {
            // machineryRepository.delete(machineryBean);
            LOGGER.error("An error occurred subscribing the machinery.", e);

//            if (Objects.nonNull(machineryBean.id))
//                machineryRepository.deleteById(machineryBean.id);

            // TODO: improve exception handling with the cert removal
            throw new SubscriptionException(e);
//        } finally {
//            MachinerySubscriptionRequest msRequest = new MachinerySubscriptionRequest();
//            msRequest.id = machineryBean.id;
//            msRequest.factoryId = subscriptionService.getFactoryId();
//            msRequest.name = request.name;
//            msRequest.serial = request.serial;
//            msRequest.keyStorePassword = request.keyStorePassword;
//            msRequest.subscribedOn = machineryBean.registeredOn;
//
//            NewMachinerySubscribedEventDTO eventDTO = new NewMachinerySubscribedEventDTO();
//            eventDTO.machinerySubscriptionRequest = msRequest;
//            event.fire(eventDTO);
        }
    }

    private CertificateResponse requestCertificate(
            EdgeSubscriptionRequest request, MachineryBean machineryBean) {
        LOGGER.info(
                "Attempting to request a new certificate for the Machinery...");

        CertificateRequest certificateRequest = new CertificateRequest();
        certificateRequest.id = machineryBean.id;
        certificateRequest.domain = "."
                + subscriptionService.getFactoryName();
        certificateRequest.serial = request.serial;
        certificateRequest.name = request.name;
        certificateRequest.keyStorePassword = request.keyStorePassword;
        certificateRequest.ca = false;
        CertificateResponse certificateResponse = registrationServiceClient
                .provisionCertificate(certificateRequest);

        LOGGER.info("Certificate for the new Machinery {} created:"//
                + "\nKEYSTORE:\n{}"//
                + "\n"//
                + "\nTRUSTSTORE:\n{}", //
                machineryBean, certificateResponse.keystore,
                certificateResponse.truststore);
        return certificateResponse;
    }

    private MachineryBean persistEntity(EdgeSubscriptionRequest request) {
        MachineryBean machineryBean;
        machineryBean = new MachineryBean();
        machineryBean.serial = request.serial;
        machineryBean.name = request.name;

        LOGGER.info("Attempting to persist a new Machinery entity: {}",
                machineryBean);
        machineryRepository.persist(machineryBean);
        LOGGER.info("Machinery entity persisted, machinery ID assigned: {}",
                machineryBean.id);
        return machineryBean;
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
