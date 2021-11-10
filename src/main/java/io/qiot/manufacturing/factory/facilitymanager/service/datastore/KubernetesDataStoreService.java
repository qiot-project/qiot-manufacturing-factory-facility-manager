/**
 * 
 */
package io.qiot.manufacturing.factory.facilitymanager.service.datastore;

import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Typed;

import org.slf4j.Logger;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.qiot.manufacturing.all.commons.exception.DataValidationException;
import io.qiot.manufacturing.factory.facilitymanager.domain.dto.FactoryDataDTO;
import io.qiot.manufacturing.factory.facilitymanager.service.SecretOperation;

/**
 * @author andreabattaglia
 *
 */
@ApplicationScoped
@Typed(KubernetesDataStoreService.class)
public class KubernetesDataStoreService implements DataStoreService {

    public static final String SECRET_NAME = "factory-data";
    public static final String FACTORY_ID = "id";
    public static final String FACTORY_SERIAL = "serial";
    public static final String FACTORY_NAME = "name";

    final Logger LOGGER;
    final SecretOperation secretOperation;

    public KubernetesDataStoreService(Logger LOGGER,
            SecretOperation secretOperation) {
        super();
        this.LOGGER = LOGGER;
        this.secretOperation = secretOperation;
    }

    @Override
    public FactoryDataDTO loadFactoryData() throws DataValidationException {
        LOGGER.info("Trying to load Factoty data from Secret {}", SECRET_NAME);
        FactoryDataDTO factoryData = null;
        Resource<Secret> secretResource = secretOperation.operation()
                .withName(SECRET_NAME);
        if (!secretResource.isReady())
            return null;

        Secret factoryDataSecret = secretResource.get();
        Map<String, String> secretData = factoryDataSecret.getData();

        factoryData = new FactoryDataDTO();
        factoryData.id = UUID.nameUUIDFromBytes(
                Base64.getDecoder().decode(secretData.get(FACTORY_ID)));
        factoryData.serial = new String(
                Base64.getDecoder().decode(secretData.get(FACTORY_SERIAL)));
        factoryData.name = new String(
                Base64.getDecoder().decode(secretData.get(FACTORY_NAME)));
        LOGGER.info("Data loaded successfully: {}", factoryData);
        return factoryData;
    }

}
