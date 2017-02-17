package ru.velkomfood.fin.cash.view;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * Created by dpetrov on 10.02.2017.
 */
public class PageGenerator {

    private static final String HTML_DIR = "public_html";
    private static PageGenerator instance;
    private final Configuration cfg;

    private PageGenerator() {
        cfg = new Configuration(Configuration.VERSION_2_3_20);
        cfg.setDefaultEncoding("UTF-8");
    }

    public static PageGenerator getInstance() {
        if (instance == null) {
            instance = new PageGenerator();
        }
        return instance;
    }

    public String getPage(String filename, Map<String, Object> data) {

        Writer stream = new StringWriter();

        try {
            Template template = cfg.getTemplate(HTML_DIR + File.separator + filename);
            template.process(data, stream);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }

        return stream.toString();
    }

}
