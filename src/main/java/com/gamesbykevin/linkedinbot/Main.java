package com.gamesbykevin.linkedinbot;

import com.gamesbykevin.linkedinbot.agent.Agent;
import com.gamesbykevin.linkedinbot.util.LogFile;
import com.gamesbykevin.linkedinbot.util.Properties;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;

import static com.gamesbykevin.linkedinbot.MainHelper.*;
import static com.gamesbykevin.linkedinbot.util.LogFile.displayMessage;
import static com.gamesbykevin.linkedinbot.util.LogFile.recycle;

public class Main extends Thread {

    //how long do we sleep (15 minutes)
    public static final long THREAD_DELAY = 900000L;

    public static void main(String[] args) {

        try {

            // Instantiates a client
            LanguageServiceClient language = LanguageServiceClient.create();

            // The text to analyze sentiment and magnitude
            String[] texts = {
                "You are worthless",
                "You are worth something",
                "You are worth nothing",
                "You are nice",
                "You are nice!",
                "You are nice!!!!!",
                "You are not nice",
                "You are an asshole",
                "You are a nice asshole",
                "I love you jerk.",
                "I hate thanos!!! I hope he dies a horrible death!!!!! What an awful useless human being!"
            };

            for (String text : texts) {
                Document doc = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();
                // Detects the sentiment of the text
                Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();

                System.out.printf("Text: \"%s\"%n", text);
                System.out.printf(
                        "Sentiment: score = %s, magnitude = %s%n",
                        sentiment.getScore(), sentiment.getMagnitude());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void mains(String[] args) {

        try {

            //load our properties
            displayMessage("Loading properties: " + Properties.PROPERTY_FILE);
            Properties.load();

            //start our thread
            Main main = new Main();
            main.start();

            //process has begun
            displayMessage("Bot started...");

        } catch (Exception e) {
            displayMessage(e);
            recycle();
        }
    }

    public Main() {
        //default constructor
    }

    @Override
    public void run() {

        while (true) {

            Agent agent = null;

            try {

                //create our agent to navigate the website
                agent = new Agent();

                //make sure we are logged in
                boolean result = agent.login();

                if (result) {
                    displayMessage("We are logged in");
                } else {
                    displayMessage("We are not logged in");
                }

                //navigate to message box to check for any unread messages to obtain text
                agent.viewMessages();

                //analyze text

                //send response

                //logout
                //agent.logout();

                //clean up log file resources
                LogFile.recycle();

            } catch (Exception e) {
                displayMessage(e);
            }

            try {

                //if not null recycle
                if (agent != null)
                    agent.recycle();

            } catch (Exception e) {

                //print any exception
                displayMessage(e);

            } finally {

                //assign null
                agent = null;
            }

            try {

                //sleep for a short time
                displayMessage("Bot sleeping for (HH:MM:SS) " + getDurationDesc(THREAD_DELAY));
                Thread.sleep(THREAD_DELAY);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
