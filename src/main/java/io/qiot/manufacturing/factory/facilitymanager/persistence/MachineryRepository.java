package io.qiot.manufacturing.factory.facilitymanager.persistence;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import io.qiot.manufacturing.factory.facilitymanager.domain.pojo.MachineryBean;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

/**
 * @author andreabattaglia
 *
 */
@ApplicationScoped
public class MachineryRepository implements PanacheRepositoryBase<MachineryBean, UUID> {

    @Inject
    Logger LOGGER;

}
