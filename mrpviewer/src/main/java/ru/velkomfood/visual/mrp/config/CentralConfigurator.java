package ru.velkomfood.visual.mrp.config;

import ru.velkomfood.visual.mrp.controller.MySqlConnector;
import ru.velkomfood.visual.mrp.controller.PageGenerator;
import ru.velkomfood.visual.mrp.view.Frontend;
import ru.velkomfood.visual.mrp.view.SearchHelper;

import java.io.IOException;

/**
 * Created by dpetrov on 28.07.16.
 */

public class CentralConfigurator {

    private static CentralConfigurator instance;

    private CentralConfigurator() { }

    public static CentralConfigurator getInstance() {

        if (instance == null) instance = new CentralConfigurator();

        return instance;
    }

    public MySqlConnector mySqlConnector() {
        return MySqlConnector.getInstance();
    }

    public Frontend frontend() {
        return new Frontend();
    }

    public PageGenerator pageGenerator() throws IOException {
        return PageGenerator.getInstance();
    }

    public SearchHelper searchHelper() {
        return new SearchHelper();
    }

}
