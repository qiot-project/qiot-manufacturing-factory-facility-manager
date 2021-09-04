package io.qiot.manufacturing.factory.facilitymanager.service.machinery;

import java.util.List;
import java.util.UUID;

import io.qiot.manufacturing.all.commons.domain.landscape.MachineryDTO;
import io.qiot.manufacturing.all.commons.domain.landscape.SubscriptionResponse;
import io.qiot.manufacturing.commons.domain.registration.EdgeSubscriptionRequest;

/**
 * @author andreabattaglia
 *
 */
public interface MachineryService {

    SubscriptionResponse subscribe(EdgeSubscriptionRequest request);

    MachineryDTO getById(UUID id);

    List<MachineryDTO> getAllStations();

}