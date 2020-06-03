package ru.velkomfood.mm.mrp.uploader;

import org.slf4j.LoggerFactory;
import ru.velkomfood.mm.mrp.uploader.config.Generator;
import ru.velkomfood.mm.mrp.uploader.core.AppEngine;

public class Launcher {

    public static void main(String[] args) {

        var logger = LoggerFactory.getLogger(Launcher.class);
        logger.info("Start MRP Data Uploader");
        if (args.length != 1) {
            String msg = String
                    .format("Enter the range number of materials: %d, %d, %d, %d, %d, %d", 1, 2, 3, 4, 5, 6);
            logger.error(msg);
            System.exit(1);
        }
        var generator = Generator.create();
        generator.generate();
        AppEngine engine = generator.getEngine();
        if (engine != null) {
            var range = Integer.parseInt(args[0]);
            engine.buildTasks(range);
            engine.start();
        } else {
            logger.error("Cannot create the core of the application");
        }


    }

}
