package com.bnp.scorereporter;

import com.bnp.logging.LogFile;
import com.bnp.searchscorersingleton.SearchScorerSingleton;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * ScoreReporter class to define and schedule the reporting of scores
 *
 * @author      Yu Chen
 * @version     %I%, %G%
 * @since       1.0
 */

public class ScoreReporter extends TimerTask {

    /**
     * The configuration loaded
     */
    private LinkedHashMap<String, String> config;

    /**
     * The data storage of Boogle Search
     */
    private SearchScorerSingleton ss;

    /**
     * Default C'stor
     *
     * Default at 09:00:00 HKT, period is set to 24 hours, if there is no configuration file loaded
     */
    private ScoreReporter(){
        config = new LinkedHashMap<>();
        config.put("Time", "09:00");
        config.put("Timezone", "Asia/Hong_Kong");
        config.put("Frquency", "24");
        config.put("DIR", "D:");
    }

    public ScoreReporter(SearchScorerSingleton ss){
        this();
        this.ss = ss;
    }

    public ScoreReporter(String configFile, SearchScorerSingleton ss){
        this();
        this.ss = ss;
        loadConfigFile(configFile);
    }

    /**
     * Copy C'stor
     *
     * @param sr the original object to copy
     */
    public ScoreReporter(ScoreReporter sr){
        this.ss = sr.ss;
        this.config = (LinkedHashMap<String, String>)sr.config.clone();
    }

    /**
     * Load configurations form the config file in CSV format
     *
     * @param csvFile the configuration file
     */
    public void loadConfigFile(String csvFile) {

        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] param = line.split(cvsSplitBy);
                config.put(param[0], param[1]);
            }
        } catch (IOException e) {
            LogFile.log(e, "warning", "Cannot open config file " + csvFile + ", using default settings.");
        }
    }

    /**
     * Write the internal storage to the CSV file, named as "keyscore_yyyyMMdd.csv"
     */
    public void reportToCSV(){
        String path = config.get("DIR");
        SimpleDateFormat timeParser = new SimpleDateFormat("yyyyMMdd");
        //SimpleDateFormat timeParser = new SimpleDateFormat("yyyyMMddHHmmss");
        Date time = new Date();
        try {
            ss.writeToCSV(path + "\\keyscore_" + timeParser.format(time) + ".csv");
        }
        catch(IOException ex){
            // do nothing
        }
    }

    /**
     * On-demand trigger to report searching counts to the CSV file
     */
    public void triggerOutputFromConsole(){
        while(true){

            // Wait before trigger the on-demand reporting for better display
            try{
                Thread.sleep(500);
            }
            catch(Exception ex){
                // do nothing
                LogFile.log(ex, "warning", "Failed to delay for 500ms.");
            }

            System.out.println("Type Y and Enter to report immediately:");
            Scanner sc = new Scanner(System.in);
            String input = sc.next();
            if(input.equals("Y")){
                reportToCSV();
            } else if (input.equals("Q")){
                break;
            }else{
                System.out.println("Not printing");
            }

        }
    }

    /**
     * The Timer Task process to run at specified time interval
     */
    @Override
    public void run() {
        try {

            String timeZone = config.get("Timezone");
            TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);

            // Get the current day of the week
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            // Report ONLY on weekdays
            if( dayOfWeek != Calendar.SUNDAY && dayOfWeek != Calendar.SATURDAY ){
                reportToCSV();
            }
        } catch (Exception ex) {
            LogFile.log(ex, "severe", "error running thread " + ex.getMessage());
        }
    }

    /**
     * Run the task using a timer with predefined time intervals from the configuration file
     *
     * @return the Timer object to control the scheduled task
     */
    public Timer runTask(){

        // Load parameters from the configuration
        String timeZone = config.get("Timezone"),
            timeStr = config.get("Time"),
            freqStr = config.get("Frequency");
        Double freqHours = new Double(freqStr);
        long milliSec = Math.round(1000.0 * 60.0 * 60.0 * freqHours);

        // Stop if the time interval is not more than 1 millisecond
        if(milliSec < 1){
            LogFile.log(null, "severe", "error scheduled time frequency.");
            return null;
        }

        // Set the timezone
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));

        // Parse the first starting time
        SimpleDateFormat timeParser = new SimpleDateFormat("HH:mm");
        Date time = new Date();

        try {
            time = timeParser.parse(timeStr);
        }
        catch (ParseException e){
            LogFile.log(e, "warning", "Can't parse the time from config file: " + timeStr + ", using now instead");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
        calendar.set(Calendar.MINUTE, time.getMinutes());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Increment the starting time by one day if the time has passed on the current running day, i.e., scheduled to start from the next day
        Date now = new Date();
        if(calendar.getTime().before(now)){
            calendar.add(Calendar.DATE, 1);
        }

        // Instantiate Timer Object
        Timer timer = new Timer();

        // Start running the timer task
        // Default at 09:00:00 HKT, period is set to 24 hours, if there is no configuration file loaded
        timer.schedule(this, calendar.getTime(), milliSec);
        //timer.schedule(this, calendar.getTime(), TimeUnit.SECONDS.toMillis(freqHours.longValue()));

        return timer;
    }
}
