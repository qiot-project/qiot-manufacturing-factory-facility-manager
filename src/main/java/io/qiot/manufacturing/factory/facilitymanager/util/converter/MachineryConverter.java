package io.qiot.manufacturing.factory.facilitymanager.util.converter;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import io.qiot.manufacturing.all.commons.domain.landscape.MachineryDTO;
import io.qiot.manufacturing.factory.facilitymanager.domain.pojo.MachineryBean;
import io.qiot.ubi.all.utilities.converter.DataObjectConverter;

@ApplicationScoped
public class MachineryConverter
        implements DataObjectConverter<MachineryBean, MachineryDTO> {

    @Override
    public MachineryDTO sourceToDest(MachineryBean src) {

        MachineryDTO dest = new MachineryDTO();
        dest.id = src.id;
        dest.serial = src.serial;
        dest.name = src.name;
        dest.registeredOn = src.registeredOn;
        return dest;
    }

    @Override
    public MachineryBean destToSource(MachineryDTO dest) {
        MachineryBean src = new MachineryBean();
        src.id = dest.id;
        src.serial = dest.serial;
        src.name = dest.name;
        src.registeredOn = dest.registeredOn;
        return src;
    }

    @Override
    public List<MachineryDTO> allSourceToDest(List<MachineryBean> srcs) {
        List<MachineryDTO> machineryDTOs = new ArrayList<>(srcs.size());
        for (MachineryBean machineryBean : srcs)
            machineryDTOs.add(sourceToDest(machineryBean));
        return machineryDTOs;
    }

    @Override
    public List<MachineryBean> allDestToSource(List<MachineryDTO> dests) {
        // TODO Auto-generated method stub
        return null;
    }
}
