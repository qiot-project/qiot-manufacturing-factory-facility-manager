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
import io.qiot.manufacturing.commons.domain.registration.EdgeSubscriptionRequest;
import io.qiot.manufacturing.datacenter.commons.domain.registration.MachinerySubscriptionRequest;
import io.qiot.manufacturing.factory.facilitymanager.domain.pojo.MachineryBean;
import io.qiot.manufacturing.factory.facilitymanager.persistence.MachineryRepository;
import io.qiot.manufacturing.factory.facilitymanager.service.factory.FactoryService;
import io.qiot.manufacturing.factory.facilitymanager.service.registration.PlantManagerClient;
import io.qiot.manufacturing.factory.facilitymanager.util.converter.MachineryConverter;

/**
 * @author andreabattaglia
 *
 */
@ApplicationScoped
class MachineryServiceImpl implements MachineryService {

    @Inject
    Logger LOGGER;

    @Inject
    FactoryService facilityService;

    @Inject
    MachineryRepository machineryRepository;

    @Inject
    MachineryConverter converter;

    @Inject
    @RestClient
    PlantManagerClient plantManagerClient;

    @Override
    public SubscriptionResponse subscribe(EdgeSubscriptionRequest request)
            throws SubscriptionException {
        /*
         * subscribe machinery
         */
        MachinerySubscriptionRequest msRequest = new MachinerySubscriptionRequest();
        msRequest.name = request.name;
        msRequest.serial = request.serial;
        msRequest.keyStorePassword = request.keyStorePassword;

        SubscriptionResponse subscriptionResponse = null;
        // while (subscriptionResponse == null) {
        // // TODO: put sleep time in application.properties
        // long sleepTime = 2000;
        // try {
        // subscriptionResponse = plantManagerClient
        // .subscribeMachinery(msRequest);
        // } catch (Exception e) {
        // LOGGER.info(
        // "An error occurred registering the machinery. "
        // + "Retrying in {} millis.\n Error message: {}",
        // sleepTime, e.getMessage());
        // try {
        // Thread.sleep(sleepTime);
        // } catch (InterruptedException ie) {
        // Thread.currentThread().interrupt();
        // }
        // }
        // }
        try {
            subscriptionResponse = plantManagerClient
                    .subscribeMachinery(msRequest);
        } catch (Exception e) {
            LOGGER.info("An error occurred registering the machinery. "
                    + "Error message: {}", e.getMessage());
            throw new SubscriptionException(e);
        }

        /*
         * persist & flush the new entity (and get the generated ID)
         */
        MachineryBean machineryBean = new MachineryBean();
        machineryBean.serial = request.serial;
        machineryBean.name = request.name;
        machineryBean.id = subscriptionResponse.id;
        machineryRepository.persist(machineryBean);

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
