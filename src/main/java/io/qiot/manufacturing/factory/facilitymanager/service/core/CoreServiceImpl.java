/**
 * 
 */
package io.qiot.manufacturing.factory.facilitymanager.service.core;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.resteasy.plugins.providers.DocumentProvider;
import org.jboss.resteasy.plugins.providers.FormUrlEncodedProvider;
import org.jboss.resteasy.plugins.providers.IIOImageProvider;
import org.jboss.resteasy.plugins.providers.JaxrsFormProvider;
import org.jboss.resteasy.plugins.providers.SourceProvider;
import org.jboss.resteasy.plugins.providers.sse.SseEventProvider;
import org.slf4j.Logger;

import io.qiot.manufacturing.all.commons.domain.cdi.BootstrapCompletedEventDTO;
import io.qiot.manufacturing.factory.facilitymanager.service.factory.SubscriptionService;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * @author andreabattaglia
 *
 */
// @Startup(1)
@ApplicationScoped
@RegisterForReflection(targets = { IIOImageProvider.class,
        SseEventProvider.class, FormUrlEncodedProvider.class,
        SourceProvider.class, DocumentProvider.class, JaxrsFormProvider.class })
public class CoreServiceImpl implements CoreService {

    @Inject
    Logger LOGGER;

    @Inject
    SubscriptionService subscriptionService;

    @Inject
    Event<BootstrapCompletedEventDTO> event;

    void onStart(@Observes StartupEvent ev) throws Exception {
        LOGGER.debug("The application is starting...{}");
        subscriptionService.checkRegistration();
        event.fire(new BootstrapCompletedEventDTO());
    }
}
