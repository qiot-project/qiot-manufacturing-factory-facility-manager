/**
 * 
 */
package io.qiot.manufacturing.factory.facilitymanager.service.core;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

import io.qiot.manufacturing.all.commons.domain.event.BootstrapCompletedEventDTO;
import io.qiot.manufacturing.factory.facilitymanager.service.factory.FactoryService;
import io.quarkus.runtime.StartupEvent;

/**
 * @author andreabattaglia
 *
 */
// @Startup(1)
@ApplicationScoped
public class CoreServiceImpl implements CoreService {

    @Inject
    Logger LOGGER;

    @Inject
    FactoryService factoryService;

    @Inject
    Event<BootstrapCompletedEventDTO> event;

    void onStart(@Observes StartupEvent ev) throws Exception {
        LOGGER.debug("The application is starting...{}");
        factoryService.checkRegistration();
        event.fire(new BootstrapCompletedEventDTO());
    }
}
