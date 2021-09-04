package io.qiot.manufacturing.factory.facilitymanager.service.factory;

import java.util.UUID;

import io.qiot.manufacturing.all.commons.exception.DataValidationException;
import io.qiot.manufacturing.all.commons.exception.SubscriptionException;
import io.qiot.manufacturing.factory.facilitymanager.domain.dto.FactoryDataDTO;

/**
 * @author andreabattaglia
 *
 */
public interface FactoryService {

    UUID getFactoryId();

    String getFactorySerial();

    String getFactoryName();

    FactoryDataDTO checkRegistration()
            throws DataValidationException, SubscriptionException;

}
