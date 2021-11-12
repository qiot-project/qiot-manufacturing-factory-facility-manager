package io.qiot.manufacturing.factory.facilitymanager.domain.event;

import io.qiot.manufacturing.datacenter.commons.domain.subscription.MachinerySubscriptionRequest;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * @author andreabattaglia
 *
 */
@RegisterForReflection
public class NewMachinerySubscribedEventDTO {
    public MachinerySubscriptionRequest machinerySubscriptionRequest;

}
