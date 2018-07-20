package ru.velkomfood.stax.stream.processor.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.velkomfood.stax.stream.processor.StaxStreamProcessor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


public class StaxStreamProcessorTest {

    private static final String FILE_NAME = "Here is the path to the XML file";
    private StringBuilder fileContent;
    private List<String> tagNames;

    @Before
    public void prepareStaxStreamProcessor() {

        fileContent = new StringBuilder(0);

        try (Stream<String> stream = Files.lines(Paths.get(FILE_NAME))) {
            stream.forEach(line -> {
                line = line.replaceAll("\n", "");
                line = line.replaceAll("\r\n", "");
                fileContent.append(line);
            });
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    @Test
    public void runStaxStreamProcess1() {

        ByteArrayInputStream inputStream1 =
                new ByteArrayInputStream(fileContent.toString().getBytes());

        // Test first collection
        try (StaxStreamProcessor processor1 = new StaxStreamProcessor(inputStream1)) {

            tagNames = processor1.readAllTagNames();
            Assert.assertNotEquals(null, tagNames);

        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            try {
                inputStream1.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }

        // Test second collection
        ByteArrayInputStream inputStream2 =
                new ByteArrayInputStream(fileContent.toString().getBytes());

        try (StaxStreamProcessor processor2 = new StaxStreamProcessor(inputStream2)) {

            Map<String, String> tagsWithText = processor2.readAllTagValues();
            Assert.assertTrue(tagsWithText.size() > 0);

        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            try {
                inputStream2.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }

    }


}
