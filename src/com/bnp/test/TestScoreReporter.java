package com.bnp.test;

import com.bnp.scorereporter.ScoreReporter;
import com.bnp.searchscorersingleton.SearchScorerSingleton;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.Timer;

/**
 * JUnit test class for the ScoreReporter class
 *
 * @author Yu Chen
 */
public class TestScoreReporter {

    /**
     * Parameters
     */
    private SearchScorerSingleton ss = SearchScorerSingleton.getInstance();
    private String configFile = "cfg/SystemDefinition.csv";

    @Before
    public void runBefore(){
        ss.clear();
        ss.inputSearchTerm("Happy Birthday Happy");
    }

    @Test
    public void testRunTask(){

        ScoreReporter sr = new ScoreReporter(configFile, ss);
        Timer timer = sr.runTask();

        try {
            Thread.sleep(500);
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }

        timer.cancel();
        timer.purge();
    }

    @Test
    public void testTriggerOutputFromConsole(){

        ScoreReporter sr = new ScoreReporter(configFile, ss);

        System.out.println("(Type Q and Enter to quit)");
        sr.triggerOutputFromConsole();
    }

    @Test
    public void testReportToCSV(){

        ScoreReporter sr = new ScoreReporter(configFile, ss);

        sr.reportToCSV();
    }

    public static void main(String[] args) {

        // Run test cases
        Result result = JUnitCore.runClasses(TestScoreReporter.class);

        // Print test results
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }

        System.out.println(result.wasSuccessful());
    }
}
