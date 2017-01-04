package com.bnp.scorereporter;

import com.bnp.logging.LogFile;

import java.util.Timer;

/**
 * ReportScore class to start the reporting of scores
 *
 * @author      Yu Chen
 * @version     %I%, %G%
 * @since       1.0
 */

public class ReportScore extends Thread {

    /**
     * The ScoreReporter used to define and schedule the reporting
     */
    ScoreReporter sr;

    /**
     * the Timer object to control the scheduled task
     */
    volatile Timer timer = new Timer();

    /**
     * C'stor
     *
     * @param sr the ScoreReporter used to define and schedule the reporting
     */
    public ReportScore(ScoreReporter sr){
        //Constructor
        this.sr = sr;
    }

    /**
     * The Task process to run at background thread
     */
    @Override
    public void run() {
        try {
            // Your task process
            timer = sr.runTask();
        } catch (Exception ex) {
            LogFile.log(ex, "severe", "error running thread " + ex.getMessage());
        }
    }

    /**
     * End the timer
     */
    public void end(){
        synchronized (timer){
            if(timer != null) {
                timer.cancel();
                timer.purge();
            }
        }
    }

    /**
     * The process to initiate and run the scheduled reporting process and the on-demand trigger on two threads
     */
    public void report(){

        // Create a background thread to run the scheduled reporting
        Thread thread1 = new ReportScore(sr);
        thread1.start();

        LogFile.log(null, "info", "Scheduled reporting is running now.");

        // Using the main thread to run the on-demand reporting
        ScoreReporter sr_byTrigger = new ScoreReporter(sr);
        sr_byTrigger.triggerOutputFromConsole();
    }
}
