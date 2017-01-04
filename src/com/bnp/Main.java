package com.bnp;

import com.bnp.scorereporter.ReportScore;
import com.bnp.scorereporter.ScoreReporter;
import com.bnp.searchscorersingleton.SearchScorerSingleton;

/**
 * Main class for a demo of the Simple first-time  run
 *
 * @author Yu Chen
 */
public class Main {

    public static void main(String[] args) {
	    // Simple first-time test run

        // Load the configuration
        String configFile = "cfg/SystemDefinition.csv";

        // Initiate the storage singleton og the Boogle search counting
        SearchScorerSingleton ss = SearchScorerSingleton.getInstance();

        // Define the searching count reporter
        ScoreReporter sr = new ScoreReporter(configFile, ss);

        // Simulate a search input
        ss.inputSearchTerm("Happy Birthday Happy");
        ss.inputSearchTerm("Happy Birthday Happy");

        // Start both scheduled and on-demand reporting
        ReportScore rs = new ReportScore(sr);
        rs.report();
    }
}
