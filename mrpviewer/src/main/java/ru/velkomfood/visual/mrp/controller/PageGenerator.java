package ru.velkomfood.visual.mrp.controller;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * Created by dpetrov on 28.07.16.
 */

public class PageGenerator {

    private static final String HTML_DIR = "public_html";

    private static PageGenerator pageGenerator;
    private final Configuration cfg;

    private PageGenerator() {

        cfg = new Configuration();

    }

    public static PageGenerator getInstance() throws IOException {

        if (pageGenerator == null) {
            pageGenerator = new PageGenerator();
        }

        return pageGenerator;
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
