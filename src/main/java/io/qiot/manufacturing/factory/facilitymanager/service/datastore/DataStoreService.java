/**
 * 
 */
package io.qiot.manufacturing.factory.facilitymanager.service.datastore;

import io.qiot.manufacturing.all.commons.exception.DataValidationException;
import io.qiot.manufacturing.factory.facilitymanager.domain.dto.FactoryDataDTO;

/**
 * @author andreabattaglia
 *
 */
public interface DataStoreService {
    FactoryDataDTO loadFactoryData() throws DataValidationException;
}
