package org.iegs.services.sap.destinator;

import com.sap.conn.jco.JCoException;
import org.iegs.services.sap.destinator.core.SapReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Launcher {

    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) {

        SapReader sapReader = SapReader.createInstance();

        try {
            sapReader.initialize();
            sapReader.makeDestinations();
        } catch (IOException | JCoException e) {
            LOG.error(e.getMessage());
        }

    }

}
