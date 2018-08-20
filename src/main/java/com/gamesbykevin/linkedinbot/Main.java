package com.gamesbykevin.linkedinbot;

import com.gamesbykevin.linkedinbot.agent.Agent;
import com.gamesbykevin.linkedinbot.util.LogFile;
import com.google.cloud.dialogflow.v2.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.gamesbykevin.linkedinbot.MainHelper.*;
import static com.gamesbykevin.linkedinbot.util.LogFile.displayMessage;

public class Main extends Thread {

    //how long do we sleep (15 minutes)
    public static final long THREAD_DELAY = 900000L;

    public static void main(String[] args) {

        try {

            System.out.println("Hello World! " + new Date());

            ArrayList<String> texts = new ArrayList<>();
            String projectId = "psyched-garage-213416";

            //keep same for the same conversation
            final String sessionId = UUID.randomUUID().toString();

            //all conversations are spoken in English
            final String languageCode = "en-US";

            while (true) {

                System.out.println("Type (\"exit\" to quit): ");

                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String sentence = br.readLine();

                if (sentence != null) {

                    if (sentence.equalsIgnoreCase("exit"))
                        break;

                    texts.clear();
                    texts.add(sentence);
                    detectIntentTexts(projectId, texts, sessionId, languageCode);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the result of detect intent with texts as inputs.
     *
     * Using the same `session_id` between requests allows continuation of the conversation.
     * @param projectId Project/Agent Id.
     * @param texts The text intents to be detected based on what a user says.
     * @param sessionId Identifier of the DetectIntent session.
     * @param languageCode Language code of the query.
     */
    public static void detectIntentTexts(
            String projectId,
            List<String> texts,
            String sessionId,
            String languageCode) throws Exception {

        // Instantiates a client
        try (SessionsClient sessionsClient = SessionsClient.create()) {

            // Set the session name using the sessionId (UUID) and projectID (my-project-id)
            SessionName session = SessionName.of(projectId, sessionId);
            //System.out.println("Session Path: " + session.toString());

            // Detect intents for each text input
            for (String text : texts) {

                // Set the text (hello) and language code (en-US) for the query
                TextInput.Builder textInput = TextInput.newBuilder().setText(text).setLanguageCode(languageCode);

                // Build the query with the TextInput
                QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

                // Performs the detect intent request
                DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);

                // Display the query result
                QueryResult queryResult = response.getQueryResult();

                //System.out.println("====================");
                //System.out.format("Query Text: '%s'\n", queryResult.getQueryText());
                //System.out.format("Detected Intent: %s (confidence: %f)\n", queryResult.getIntent().getDisplayName(), queryResult.getIntentDetectionConfidence());
                System.out.format("Fulfillment Text: '%s'\n", queryResult.getFulfillmentText());
            }
        }
    }











    /*
    public static void main(String[] args) {

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
    */

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
