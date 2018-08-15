package com.gamesbykevin.linkedinbot.agent;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;

import static com.gamesbykevin.linkedinbot.agent.AgentHelper.*;
import static com.gamesbykevin.linkedinbot.util.LogFile.displayMessage;
import static com.gamesbykevin.linkedinbot.util.Properties.DEBUG;

public class Agent {

    private static final String DRIVER_PROPERTY = "webdriver.chrome.driver";

    //our login credentials
    public static String LOGIN_USERNAME;
    public static String LOGIN_PASSWORD;

    //where we start
    public static String HOME_PAGE;

    //where are out messages location
    public static String MESSAGE_PAGE;

    //where do we login at
    public static String LOGIN_PAGE;

    //what is our profile name (ex. kclauson)
    public static String PROFILE_NAME;

    //where is the google chrome selenium driver
    public static String CHROME_DRIVER_LOCATION;

    //our user agent for mobile browsing
    public static String USER_AGENT_MOBILE;

    //our object used to interact with the web pages
    private WebDriver driver;

    /**
     * Create our agent
     */
    public Agent() {

        displayMessage("*******************");
        displayMessage("Launching browser with driver: " + CHROME_DRIVER_LOCATION);
        System.setProperty(DRIVER_PROPERTY, CHROME_DRIVER_LOCATION);

        //create our new chrome options
        ChromeOptions options = new ChromeOptions();

        //customize the browser we are logging into
        options.addArguments("--user-agent=" + USER_AGENT_MOBILE);

        //if we aren't debugging do not display chrome
        if (DEBUG == false) {

            //add headless so the browser can run in the background without a gui
            options.addArguments("--headless");
        }

        //needed to start chrome without errors
        options.addArguments("--no-sandbox");

        //create our driver with the specified options
        this.driver = new ChromeDriver(options);

        //wait a moment
        pause();
    }

    private void openWebPage(String url, String message) {

        displayMessage(message);

        try {

            //load the bing homepage
            getDriver().get(url);

        } catch (Exception e) {
            displayMessage(e);
        }

        //wait a moment
        pause();
    }

    private void openHomePage() {
        openWebPage(HOME_PAGE, "Open home page");
    }

    private void openLoginPage() {
        openWebPage(LOGIN_PAGE, "Open login page");
    }

    public boolean login() {

        if (!loggedIn()) {

            //open the login page
            openLoginPage();

            //enter login
            enterLogin();

            //submit login
            submitLogin();

            //confirm that we have logged in
            return (loggedIn());

        } else {

            //we are
            return true;
        }
    }

    /**
     * Check if we are logged in
     * @return true if logged in, false otherwise
     */
    private boolean loggedIn() {

        //open the website homepage
        openHomePage();

        //our web element
        WebElement element = null;

        boolean success = false;

        try {

            //if the element does not exist, we aren't logged in
            element = getDriver().findElement(By.id("messaging-tab-icon"));

            //if the element exists, we are logged in
            success = (element != null);

        } catch (Exception e) {

            //in case any exception, we are not logged in
            success = false;
        }

        //if successful we are logged in
        return success;
    }

    public void viewMessages() {

        //open the message box
        openWebPage(MESSAGE_PAGE, "Open message box");

        //locate elements by anchor tag
        //By className = By.cssSelector(".msg-conversation-listitem.msg-conversations-container__convo-item.msg-conversations-container__pillar.ember-view");
        By className = By.xpath("//h3[@class='msg-conversation-listitem__participant-names msg-conversation-card__participant-names truncate pr1 Sans-17px-black-100%-semibold']");

        //get all the elements found
        List<WebElement> elements = getDriver().findElements(className);

        //check each of the unread messages
        for (WebElement element : elements) {

            try {

                //lets just make sure this message has text
                if (element.getText() != null && element.getText().length() > 1) {

                    //click the element to open the conversation
                    element.click();

                    //pause for a short while
                    pause();

                    //obtain all chat messages in the conversation
                    List<WebElement> messages = getDriver().findElements(By.cssSelector(".msg-s-message-list__event.clearfix"));

                    //check the last message and determine who sent it
                    WebElement message = messages.get(messages.size() - 1);

                    //obtain all children within this web element
                    List<WebElement> children = message.findElements(By.xpath(".//*"));

                    //text of message
                    String text = "";

                    //did we author the message
                    boolean authored = true;

                    //identify the info for this message
                    for (WebElement child : children) {

                        //this is who wrote the message
                        if (child.getAttribute("class").equalsIgnoreCase("msg-s-message-group__profile-link ember-view")) {

                            //who authored this message
                            if (child.getAttribute("href").toLowerCase().contains(PROFILE_NAME.toLowerCase())) {
                                authored = true;
                            } else {
                                authored = false;
                            }

                        } else if (child.getAttribute("class").equalsIgnoreCase("msg-s-event-listitem__message-bubble")) {

                            //get the message text
                            text = child.getText();
                        }
                    }

                    if (authored) {
                        displayMessage("We wrote");
                    } else {
                        displayMessage("They wrote");
                    }

                    displayMessage("Message: " + text);

                    sendMessage("Test message");
                    break;
                }

            } catch (Exception e) {

                //not all elements will be found
                e.printStackTrace();
                break;
            }
        }
    }

    private void sendMessage(String message) {

        //This is where we enter our message
        WebElement element = getDriver().findElement(By.xpath("//div[@class='msg-form__contenteditable Sans-15px-black-70% flex-grow-1']"));

        //enter our message
        element.sendKeys(message);

        //wait a moment
        pause();

        //button that will send message
        //element = getDriver().findElement(By.xpath("//div[@class='msg-form__send-button button-primary-small']"));

        //send the message
        //element.click();
        //AgentHelper.clickLink(getDriver(), By.id("signin-submit"), );

        //wait a moment
        pause();
    }

    private void enterLogin() {

        //obtain our text fields and enter our credentials
        getWebElement(getDriver(), By.id("session_key-login")).sendKeys(LOGIN_USERNAME);
        getWebElement(getDriver(), By.id("session_password-login")).sendKeys(LOGIN_PASSWORD);
    }

    private void submitLogin() {

        AgentHelper.clickLink(getDriver(), By.id("signin-submit"), "Submit login");
    }

    protected WebDriver getDriver() {
        return this.driver;
    }

    public void recycle() {

        if (this.driver != null) {

            try {
                displayMessage("Closing browser");
                this.driver.quit();
            } catch (Exception e) {
                displayMessage(e);
            }

            this.driver = null;
        }
    }
}