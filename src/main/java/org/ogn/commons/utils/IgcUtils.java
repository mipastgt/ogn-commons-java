/**
 * Copyright (c) 2015 OGN, All Rights Reserved.
 */

package org.ogn.commons.utils;

import org.ogn.commons.beacon.AircraftBeacon;
import org.ogn.commons.beacon.AircraftDescriptor;

public class IgcUtils {

    public static String toIgcLogFileId(final AircraftBeacon beacon, final AircraftDescriptor descriptor) {
        StringBuilder id = new StringBuilder(beacon.getId());
        if (descriptor.isKnown()) {
            String reg = descriptor.getRegNumber();
            String cn = descriptor.getCN();

            if (reg != null && !reg.isEmpty() && cn != null && !cn.isEmpty())
                id.append("_").append(reg).append("_").append(cn);
            else if (reg != null && !reg.isEmpty())
                id.append("_").append(reg);
            else if (cn != null && !cn.isEmpty())
                id.append("_").append(cn);
        }

        return id.toString();
    }
}