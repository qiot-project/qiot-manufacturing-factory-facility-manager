package io.qiot.manufacturing.factory.facilitymanager.service.factory;

import java.util.UUID;

import io.qiot.manufacturing.all.commons.exception.DataValidationException;
import io.qiot.manufacturing.all.commons.exception.SubscriptionException;
import io.qiot.manufacturing.factory.facilitymanager.domain.dto.FactoryDataDTO;

/**
 * @author andreabattaglia
 *
 */
public interface SubscriptionService {

    UUID getFactoryId();

    String getFactorySerial();

    String getFactoryName();

    void checkRegistration()
            throws DataValidationException;

}
