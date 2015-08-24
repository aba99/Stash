/*
 * HardcodedEndpointResolver.java
 *
 * Copyright: Copyright (c) 2010
 *
 * Company: Orsyp Logiciels Inc.
 */

package com.orsyp.owls.impl;

import com.orsyp.Area;
import com.orsyp.api.Context;
import com.orsyp.std.ConnectionFactory.Service;

/**
 * An <code>EndpointResolver</code> for testing purposes.
 *
 * @author jjt
 * @version $Revision: 1.3 $
 */
public class HardcodedEndpointResolver implements EndpointResolver {

    public String getHost(Context context, Service service) {

        if (service == Service.IO) {
            return AbstractTest.DUAS_HOSTNAME;
        }
        return null;
    }


    public int getPort(Context context, Service service) {

        if (service == Service.IO) {
            final Area area = context.getEnvironment().getArea();
            if (area == Area.Exploitation) {
                return 10641;
            } else if (area == Area.Application) {
                return 10644;
            }
        }
        return -1;
    }

}
