package ru.velkomfood.fin.tests;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Created by dpetrov on 30.03.2017.
 */
public class TestRunner {

    public static void main(String[] args) {

        Result result = JUnitCore.runClasses(TestPrintForm.class);

        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }

        System.out.println(result.wasSuccessful());
    }

}
