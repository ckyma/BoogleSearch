package com.bnp.logging;

import java.io.IOException;
import java.util.logging.*;

/**
 * LogFile class to log
 */
public class LogFile {

    protected static final Logger logger=Logger.getLogger("MYLOG");
    /**
     * log Method
     * enable to log all exceptions to a file and display user message on demand
     * @param ex
     * @param level
     * @param msg
     */
    public static void log(Exception ex, String level, String msg){

        FileHandler fh = null;
        try {
            fh = new FileHandler("logs/log.xml",true);
            logger.addHandler(fh);
            switch (level) {
                case "severe":
                    logger.log(Level.SEVERE, msg, ex);
                    break;
                case "warning":
                    logger.log(Level.WARNING, msg, ex);
                    break;
                case "info":
                    logger.log(Level.INFO, msg, ex);
                    break;
                case "config":
                    logger.log(Level.CONFIG, msg, ex);
                    break;
                case "fine":
                    logger.log(Level.FINE, msg, ex);
                    break;
                case "finer":
                    logger.log(Level.FINER, msg, ex);
                    break;
                case "finest":
                    logger.log(Level.FINEST, msg, ex);
                    break;
                default:
                    logger.log(Level.CONFIG, msg, ex);
                    break;
            }
        } catch (IOException | SecurityException ex1) {
            logger.log(Level.SEVERE, null, ex1);
        } finally{
            if(fh!=null)fh.close();
        }
    }
}
