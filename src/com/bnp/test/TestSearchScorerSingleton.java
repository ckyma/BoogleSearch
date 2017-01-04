package com.bnp.test;

import org.junit.Before;
import org.junit.Test;
import com.bnp.searchscorersingleton.SearchScorerSingleton;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * JUnit test class for the SearchScorerSinglton class
 */

public class TestSearchScorerSingleton {

    /**
     * The Singleton Class to test
     */
    private SearchScorerSingleton ss = SearchScorerSingleton.getInstance();

    @Before
    public void rubBefore(){
        ss.clear();
    }

    @Test
    public void testInputSearchTerm(){

        ss.inputSearchTerm(null);
        assertEquals("Must be empty", "{}", ss.toString());

        ss.inputSearchTerm(" ");
        assertEquals("Must be empty", "{}", ss.toString());

        ss.inputSearchTerm("Happy Birthday");
        ss.inputSearchTerm(" Happy ");
        assertEquals("Must be exact values", "{birthday=1, happy=2}", ss.toString());
    }

    @Test
    public void testGetAllKeysSorted(){

        ss.inputSearchTerm(null);
        ss.inputSearchTerm(" ");
        assertEquals("Must be empty", "[]", ss.getAllKeysSorted().toString());

        ss.inputSearchTerm("Happy Birthday Happy ");
        assertEquals("Must be exact values", "[birthday, happy]", ss.getAllKeysSorted().toString());
    }

    @Test
    public void testExists(){

        ss.inputSearchTerm(" ");
        assertEquals("Must be false", false, ss.exists(" "));

        ss.inputSearchTerm("Happy Birthday ");
        assertTrue("Must be true", ss.exists("HAPpy"));
        assertFalse("Must be false", ss.exists("unhappy"));
    }

    @Test
    public void testHighestOccurance(){

        ss.inputSearchTerm("Happy Birthday Happy ");

        assertEquals("Must be exact values", "happy", ss.getHighestOccurance());
    }

    @Test
    public void testCleanLowScores(){

        ss.inputSearchTerm("Happy Birthday Happy ");

        assertEquals("Must be exact values", 1, ss.cleanLowScores(2));
        assertEquals("Must be exact values", "{happy=2}", ss.toString());
    }

    @Test(expected = IOException.class)
    public void testWriteToCSV() throws IOException {

        ss.inputSearchTerm("Happy Birthday Happy ");

        try{
            ss.writeToCSV("Z:/test_19000101.csv");
        }
        catch (IOException ex){
            throw ex;
        }
    }

    public static void main(String[] args) {

        // Run test cases
        Result result = JUnitCore.runClasses(TestSearchScorerSingleton.class);

        // Print test results
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }

        System.out.println(result.wasSuccessful());
    }
}
