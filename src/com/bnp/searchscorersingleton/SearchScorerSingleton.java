package com.bnp.searchscorersingleton;

import com.bnp.logging.LogFile;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.concurrent.*;

/**
 * SearchScorerSingleton class to store the keywords and their counts
 * It's modelled as the Singleton Pattern, acting as one single storage for all search results
 *
 * @author      Yu Chen
 * @version     %I%, %G%
 * @since       1.0
 */
public class SearchScorerSingleton {

    /**
     * The singleton instance
     */
    private static SearchScorerSingleton instance;

    /**
     * Internal storage of the key-value pairs for <key, count>
     * Use ConcurrentHashMap to store the key-value pairs to utilize its thread-safe features
     */
    private ConcurrentHashMap<String, Integer> map;

    /**
     * C'stor
     */
    private SearchScorerSingleton() {
        map = new ConcurrentHashMap<>();
    }

    /**
     * Intermediary object used as a lock and allow one thread to write to csv file at a time,
     *  by not locking the map to improve efficiency
     */
    private Object objWriter = new Object();

    /**
     * The main method to initialize and retrieve the singleton
     *
     * @return the reference to the singleton object
     */
    public static SearchScorerSingleton getInstance(){
        if(instance == null){
            // Ensure thread safety
            synchronized (SearchScorerSingleton.class) {
                if(instance == null){
                    instance = new SearchScorerSingleton();
                }
            }
        }
        return instance;
    }

    /**
     * Input the and parse keywords from the raw search term
     *
     * @param term the input term to be parsed as keywords
     */
    public void inputSearchTerm(String term){
        if(term != null) {

            // Trim and split the term into keywords
            String[] wordArray = term.trim().split(" ");

            // Store and count each of the keywords
            for (String word : wordArray) {

                // Filter out the key with value ""
                if(!word.equals("")) {

                    // Store and count the keyword
                    incrementCount(word.toLowerCase());

                }

            }

        }

    }

    /**
     * Store and count the keyword in the storage
     *
     * @param word the keyword to be stored and counted in the map
     * @return arbitrary as null, as requested by the requirement
     */
    private Object incrementCount(String word){

        // If the key exists, atomically compute the new count of the keywords, i.e., increment by 1
        map.computeIfPresent(word.toLowerCase(), (key, val) -> ++val);

        // If the key does not exist, atomically put the keywords with count = 1
        map.putIfAbsent(word.toLowerCase(), 1);

        // Arbitrary return null, as requested by the requirement
        return null;
    }

    /**
     * Returns all keys sorted in a List
     *
     * @return a List of all keys sorted
     */
    public List getAllKeysSorted(){

        // Sort the keys by storing them in a ordered TreeSet, using natural order of the String
        TreeSet<String> set = new TreeSet(map.keySet());

        // Construct the return List
        List<String> list = new ArrayList<>(set);

        return list;
    }

    /**
     * Returns the key of the highest occurrence
     *
     * @return the key of the highest count
     */
    public String getHighestOccurance(){

        // Stop if the storage is empty
        if(map.size() == 0){
            return null;
        }

        // Use the collection utility function max() to get the max value
        String key = Collections.max(map.entrySet(), Map.Entry.comparingByValue()).getKey();

        return key;
    }

    /**
     * Remove all the elements less than the specified occurrence number
     *
     * @param x the threshold number used to remove
     * @return number of elements removed
     */
    public int cleanLowScores(int x){

        // Counter of removal
        int total = 0;

        // Ensure thread safety
        synchronized (map){

            // Loop through all the entries for thread safety, instead of using iterator
            for (Map.Entry<String, Integer> entry:map.entrySet()) {

                // Check the value threshold
                if(entry.getValue() < x){

                    // Remove from the storage
                    map.remove(entry.getKey());

                    // Count the removal
                    ++total;
                }
            }

        }
        return total;
    }

    /**
     * Check if a keyword exists and has count > 0
     *
     * @param _keyword the keyword to check
     * @return true if exists, false if not
     */
    public Boolean exists(String _keyword){

        Integer value = map.get(_keyword.toLowerCase());

        // Check existing and occurrence count
        if(value != null && value >= 1){
            return true;
        }

        return false;
    }

    /**
     * Write the internal map to the csv file
     *
     * @param fileName the file name of the file to write
     */
    public void writeToCSV(String fileName) throws IOException {

        // Define the end of the line
        String eol = System.getProperty("line.separator");

        // Ensure thread safety
        synchronized (objWriter){

            try (Writer writer = new FileWriter(fileName)) {

                // Write the Header
                writer.append("Keyword")
                        .append(',')
                        .append("Score")
                        .append(eol);

                // Write the content
                for (Map.Entry<String, Integer> entry : map.entrySet()) {
                    writer.append(entry.getKey())
                            .append(',')
                            .append(entry.getValue().toString())
                            .append(eol);
                }

                writer.flush();
                writer.close();

                LogFile.log(null, "info", "Written CSV " + fileName);
            } catch (IOException ex) {
                LogFile.log(ex, "severe", "Can't write to CSV " + fileName);
                //ex.printStackTrace(System.err);
                throw ex;
            }

        }
    }

    /**
     * Remove all the elements from the storage
     */
    public void clear(){

        // Ensure thread safety
        synchronized (map) {
            map.clear();
        }

    }

    /**
     *
     *
     * @return the string output of the storage map
     */
    @Override
    public String toString(){
        return map.toString();
    }
}
