package io.qiot.manufacturing.factory.facilitymanager.service.factory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.qiot.manufacturing.all.commons.exception.DataValidationException;
import io.qiot.manufacturing.all.commons.exception.SubscriptionException;
import io.qiot.manufacturing.factory.facilitymanager.domain.dto.FactoryDataDTO;
import io.qiot.manufacturing.factory.facilitymanager.service.registration.RegistrationService;

/**
 * @author andreabattaglia
 *
 */
@ApplicationScoped
class FactoryServiceImpl implements FactoryService {

    @Inject
    Logger LOGGER;

    @ConfigProperty(name = "qiot.datafile.path")
    String dataFilePathString;

    @Inject
    ObjectMapper MAPPER;

    @Inject
    RegistrationService registrationService;

    @ConfigProperty(name = "qiot.data.reset")
    boolean DO_RESET;

    @ConfigProperty(name = "qiot.factory.serial")
    String FACILITY_SERIAL;
    @ConfigProperty(name = "qiot.factory.name")
    String FACILITY_NAME;

    @ConfigProperty(name = "qiot.certstore.password")
    String ksPassword;

    private Path dataFilePath;
    private FactoryDataDTO factoryData;

    @PostConstruct
    void init() {
        dataFilePath = Paths.get(dataFilePathString);
        if (DO_RESET)
            resetFactoryData();
    }

    private void resetFactoryData() {
        try {
            if (!Files.deleteIfExists(dataFilePath))
                LOGGER.debug(
                        "Factory data file does not exists. Nothing to delete");
        } catch (IOException e) {
            LOGGER.error("Failed resetting Factory data: {}",
                    dataFilePathString);
        }
        registrationService.resetFactoryData();
    }

    @Override
    public FactoryDataDTO checkRegistration()
            throws DataValidationException, SubscriptionException {
        // Path dataFilePath = Paths.get(dataFilePathString);
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
        } else {
            LOGGER.debug("Factory is not registered. "
                    + "Stepping through the registration process...");

            factoryData = new FactoryDataDTO();
            factoryData.serial = FACILITY_SERIAL;
            factoryData.name = FACILITY_NAME;
            UUID factoryId = null;
            // if (ProfileManager.getActiveProfile()
            // .equals(LaunchMode.DEVELOPMENT.getDefaultProfile())) {
            // factoryId = UUID.randomUUID();
            // }
            // else

            factoryId = registrationService.register(factoryData.serial,
                    factoryData.name, ksPassword);

            factoryData.id = factoryId;
            LOGGER.debug("Received factory ID: {}", factoryId);
            try {
                Files.createFile(dataFilePath);

                String factoryDataString = MAPPER
                        .writeValueAsString(factoryData);
                Files.writeString(dataFilePath, factoryDataString);
            } catch (Exception e) {
                LOGGER.error("An error occurred saving data to volume.", e);
                throw new DataValidationException(e);
            }

            LOGGER.debug("Data Created successfully: {}", factoryData);
        }

        return factoryData;

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
