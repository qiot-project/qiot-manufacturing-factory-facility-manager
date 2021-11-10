package io.qiot.manufacturing.factory.facilitymanager.service.factory;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import io.qiot.manufacturing.all.commons.exception.DataValidationException;
import io.qiot.manufacturing.factory.facilitymanager.domain.dto.FactoryDataDTO;
import io.qiot.manufacturing.factory.facilitymanager.service.datastore.DataStoreService;

/**
 * @author andreabattaglia
 *
 */
@ApplicationScoped
class SubscriptionServiceImpl implements SubscriptionService {

    @Inject
    Logger LOGGER;

    @Inject
    DataStoreService dataStoreService;

    private FactoryDataDTO factoryData;

    @Override
    public void checkRegistration() throws DataValidationException {
        factoryData = dataStoreService.loadFactoryData();
    }

    @Override
    public UUID getFactoryId() {
        return factoryData.id;
    }

    @Override
    public String getFactorySerial() {
        return factoryData.serial;
    }

    @Override
    public String getFactoryName() {
        return factoryData.name;
    }

}
