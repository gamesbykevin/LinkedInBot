package com.gamesbykevin.linkedinbot.util;

import com.gamesbykevin.linkedinbot.Main;
import com.gamesbykevin.linkedinbot.agent.Agent;
import com.gamesbykevin.linkedinbot.agent.AgentHelper;

import java.io.FileInputStream;

public class Properties {

    public static final String PROPERTY_FILE = "./application.properties";

    private static java.util.Properties PROPERTIES;

    public static final boolean DEBUG = true;

    public static java.util.Properties getProperties() {

        if (PROPERTIES == null) {

            PROPERTIES = new java.util.Properties();

            try {

                if (DEBUG) {

                    //call this when running the project in intellij
                    PROPERTIES.load(Main.class.getClassLoader().getResourceAsStream(PROPERTY_FILE));

                } else {

                    //call this when you create an executable .jar and place the application.properties file in the same directory as the .jar
                    PROPERTIES.load(new FileInputStream(PROPERTY_FILE));

                }

            } catch(Exception ex) {
                ex.printStackTrace();
                System.exit(10);
            }
        }

        return PROPERTIES;
    }

    public static void load() {

        //grab the email address from our config
        Email.EMAIL_NOTIFICATION_ADDRESS = getProperties().getProperty("emailNotification");

        //our gmail login we need so we have an smtp server to send emails
        Email.GMAIL_SMTP_USERNAME = getProperties().getProperty("gmailUsername");
        Email.GMAIL_SMTP_PASSWORD = getProperties().getProperty("gmailPassword");

        //our bing login credentials so our account is credited
        Agent.LOGIN_USERNAME = getProperties().getProperty("linkedInLoginUsername");
        Agent.LOGIN_PASSWORD = getProperties().getProperty("linkedInLoginPassword");

        //where do we start at
        Agent.HOME_PAGE = getProperties().getProperty("linkedInHomepage");

        //where do we login
        Agent.LOGIN_PAGE = getProperties().getProperty("linkedInLoginPage");

        //who authored a chat message
        Agent.PROFILE_NAME = getProperties().getProperty("linkedInProfileName");

        //where do we find our messages
        Agent.MESSAGE_PAGE = getProperties().getProperty("linkedInMessages");

        //what user agent do we want to use
        Agent.USER_AGENT_MOBILE = getProperties().getProperty("chromUserAgentMobile");

        //minimum time we wait to perform our next action
        AgentHelper.PAUSE_DELAY_MIN = Long.parseLong(getProperties().getProperty("pauseDelayMin"));

        //additional time on the minimum delay
        AgentHelper.PAUSE_DELAY_RANGE = Long.parseLong(getProperties().getProperty("pauseDelayRange"));

        //where is the driver so we can interact with the web pages
        Agent.CHROME_DRIVER_LOCATION = getProperties().getProperty("chromeDriverLocation");

        //we won't allow any time less than 1 second
        if (AgentHelper.PAUSE_DELAY_MIN < 1000)
            AgentHelper.PAUSE_DELAY_MIN = 1000;

        //we won't allow any time less than 1 second
        if (AgentHelper.PAUSE_DELAY_RANGE < 1000)
            AgentHelper.PAUSE_DELAY_RANGE = 1000;
    }
}