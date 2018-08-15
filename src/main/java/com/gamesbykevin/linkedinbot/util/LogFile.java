package com.gamesbykevin.linkedinbot.util;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LogFile {

    //file writer object
    private static PrintWriter FILE_WRITER;

    //the directory were our log files are stored
    public static final String LOG_DIRECTORY = "logs";

    //we separate the directories and files with this value /
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private static PrintWriter getFileWriter() {

        if (FILE_WRITER == null) {

            try {

                //create a new directory
                File file = new File(LOG_DIRECTORY);

                //if the directory does not exist, create it
                if (!file.exists())
                    file.mkdirs();

                //add timestamp to file name
                String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

                //create our file writer
                FILE_WRITER = new PrintWriter(LOG_DIRECTORY + FILE_SEPARATOR + "log_" + time + ".log", "UTF-8");

            } catch (Exception e) {

                e.printStackTrace();

            }
        }

        //return our file writer
        return FILE_WRITER;
    }

    private static void write(String line) {

        try {

            //write our line
            getFileWriter().println(line);

            //write any remaining bytes and close the file
            getFileWriter().flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void recycle() {

        try {

            if (FILE_WRITER != null) {
                FILE_WRITER.flush();
                FILE_WRITER.close();
            }

            FILE_WRITER = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getErrorMessage(Exception e) {

        String message = "";

        try {

            message += e.getMessage() + "\n\t\t";;

            StackTraceElement[] stack = e.getStackTrace();

            for (int i = 0; i <  stack.length; i++) {
                message = message + stack[i].toString() + "\n\t\t";
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return message;
    }

    public static void displayMessage(Exception e) {
        displayMessage(getErrorMessage(e));
    }

    public static void displayMessage(String message) {
        displayMessage(message, true);
    }

    public static void displayMessage(String message, boolean write) {

        //print message to the command line
        System.out.println(message);

        //write to file if true
        if (write)
            write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + ": " + message);
    }
}