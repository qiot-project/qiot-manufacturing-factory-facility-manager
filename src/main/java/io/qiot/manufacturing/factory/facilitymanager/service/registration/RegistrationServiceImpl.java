package io.qiot.manufacturing.factory.facilitymanager.service.registration;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;

import io.qiot.manufacturing.all.commons.domain.landscape.SubscriptionResponse;
import io.qiot.manufacturing.all.commons.exception.SubscriptionException;
import io.qiot.manufacturing.datacenter.commons.domain.subscription.FactorySubscriptionRequest;
import io.qiot.ubi.all.registration.client.RegistrationServiceClient;
import io.qiot.ubi.all.registration.domain.CAIssuerRequest;

/**
 * @author andreabattaglia
 *
 */
@ApplicationScoped
public class RegistrationServiceImpl implements RegistrationService {
    @Inject
    Logger LOGGER;

    @ConfigProperty(name = "qiot.runtime.ks.path")
    String ksPathString;
    @ConfigProperty(name = "qiot.runtime.ts.path")
    String tsPathString;

    @Inject
    @RestClient
    PlantManagerClient plantManagerClient;

    @Inject
    @RestClient
    RegistrationServiceClient registrationServiceClient;

    @Override
    public UUID register(String serial, String name, String ksPassword)
            throws SubscriptionException {
        SubscriptionResponse subscriptionResponse = null;
        FactorySubscriptionRequest registerRequest = new FactorySubscriptionRequest();

        registerRequest.serial = serial;
        registerRequest.name = name;
        registerRequest.keyStorePassword = ksPassword;

        LOGGER.debug(
                "Attempting subscription process with the following data: {}",
                registerRequest);

        while (subscriptionResponse == null) {
            // TODO: put sleep time in application.properties
            long sleepTime = 2000;
            try {

                subscriptionResponse = plantManagerClient
                        .subscribeFactory(registerRequest);
            } catch (Exception e) {
                LOGGER.info(
                        "An error occurred registering the factory. "
                                + "Retrying in {} millis.\n Error message: {}",
                        sleepTime, e.getMessage());
                LOGGER.error("",e);                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        // create factory issuer
        CAIssuerRequest issuerRequest=new CAIssuerRequest();
        issuerRequest.tlsCert=subscriptionResponse.tlsCert;
        issuerRequest.tlsKey=subscriptionResponse.tlsKey;
        registrationServiceClient.provisionIssuer(issuerRequest);        

        LOGGER.debug("Registratior process results: {}", subscriptionResponse);

        LOGGER.debug("Acquired ID: {}", subscriptionResponse.id);
        String encodedTSString = subscriptionResponse.truststore;
        String encodedKSString = subscriptionResponse.keystore;

        try {
            writeTS(encodedTSString);
            writeKS(encodedKSString);
        } catch (IOException e) {
            LOGGER.error("An error occurred storing certificates to disk.", e);
            throw new SubscriptionException(e);
        }

        return subscriptionResponse.id;
    }

    private void writeKS(String encodedKSString) throws IOException {
        byte[] content = Base64.getDecoder()
                .decode(encodedKSString.getBytes(StandardCharsets.UTF_8));
        writeToFile(content, ksPathString);
    }

    private void writeTS(String encodedTSString) throws IOException {
        byte[] content = Base64.getDecoder()
                .decode(encodedTSString.getBytes(StandardCharsets.UTF_8));
        writeToFile(content, tsPathString);
    }

    private void writeToFile(byte[] content, String destination)
            throws IOException {
        Path file = Paths.get(destination);
        Files.createDirectories(file.getParent());
        Files.createFile(file);
        try (OutputStream outputStream = Files.newOutputStream(file);) {
            outputStream.write(content);
        }
    }

    @Override
    public void resetFactoryData() {
        Path ksPath = Paths.get(ksPathString);
        try {
            if (!Files.deleteIfExists(ksPath))
                LOGGER.debug(
                        "Factory data file does not exists. Nothing to delete");
        } catch (IOException e) {
            LOGGER.error("Failed resetting Factory data: {}", ksPathString);
        }
        ksPath = null;
        Path tsPath = Paths.get(tsPathString);
        try {
            if (!Files.deleteIfExists(tsPath))
                LOGGER.debug(
                        "Factory data file does not exists. Nothing to delete");
        } catch (IOException e) {
            LOGGER.error("Failed resetting Factory data: {}", tsPathString);
        }
        tsPath = null;
    }
}
