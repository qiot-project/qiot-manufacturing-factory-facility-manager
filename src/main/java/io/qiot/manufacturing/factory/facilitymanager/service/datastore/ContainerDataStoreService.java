/**
 * 
 */
package io.qiot.manufacturing.factory.facilitymanager.service.datastore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Typed;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.qiot.manufacturing.all.commons.exception.DataValidationException;
import io.qiot.manufacturing.factory.facilitymanager.domain.dto.FactoryDataDTO;

/**
 * @author andreabattaglia
 *
 */
@ApplicationScoped
@Typed(ContainerDataStoreService.class)
public class ContainerDataStoreService implements DataStoreService {

    final Logger LOGGER;

    final ObjectMapper MAPPER;

    final String dataFilePathString;

    final boolean DO_RESET;

    private Path dataFilePath;

    public ContainerDataStoreService(Logger LOGGER, ObjectMapper MAPPER,
            @ConfigProperty(name = "qiot.datafile.path") String dataFilePathString,
            @ConfigProperty(name = "qiot.data.reset") boolean DO_RESET) {
        this.LOGGER = LOGGER;
        this.MAPPER = MAPPER;
        this.dataFilePathString = dataFilePathString;
        this.DO_RESET = DO_RESET;
        dataFilePath = Paths.get(dataFilePathString);
    }

    @Override
    public FactoryDataDTO loadFactoryData() throws DataValidationException {
        FactoryDataDTO factoryData = null;
        if (Files.exists(dataFilePath)) {
            LOGGER.debug("Factory is already registered. "
                    + "Loading data from persistent volume...");
            try {
                String datafileContent = Files.readString(dataFilePath);
                factoryData = MAPPER.readValue(datafileContent,
                        FactoryDataDTO.class);
            } catch (Exception e) {
                LOGGER.error("An error occurred loading the factory data file.",
                        e);
                throw new DataValidationException(e);
            }
            LOGGER.debug("Data loaded successfully: {}", factoryData);
        }
        return factoryData;
    }

}
