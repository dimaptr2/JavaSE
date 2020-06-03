package ru.velkomfood.mm.mrp.uploader.config;

import ru.velkomfood.mm.mrp.uploader.MrpUploaderComponent;
import ru.velkomfood.mm.mrp.uploader.core.AppEngine;
import ru.velkomfood.mm.mrp.uploader.data.EventBus;
import ru.velkomfood.mm.mrp.uploader.input.DataReader;
import ru.velkomfood.mm.mrp.uploader.input.DataReaderImpl;
import ru.velkomfood.mm.mrp.uploader.output.DataWriter;
import ru.velkomfood.mm.mrp.uploader.output.DataWriterImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Generator implements MrpUploaderComponent {

    private static final Generator instance = new Generator();
    private final Properties parameters;
    private AppEngine engine;

    private Generator() {
        parameters = readParameters();
    }

    public static Generator create() {
        return instance;
    }

    public void generate() {
        ExecutorService service = Executors.newFixedThreadPool(4);
        engine = new AppEngine(service);
        EventBus bus = new EventBus();
        DataReader reader = new DataReaderImpl(bus, parameters, service);
        DataWriter writer = new DataWriterImpl(bus, parameters, service);
        engine.addComponent(reader);
        engine.addComponent(writer);
    }

    public AppEngine getEngine() {
        return engine;
    }

    // private section

    private Properties readParameters() {
        var props = new Properties();
        try (InputStream is = getClass().getResourceAsStream("/app.properties")) {
            props.load(is);
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
        }
        return props;
    }

}
