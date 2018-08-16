package com.gamesbykevin.linkedinbot.agent;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Random;

import static com.gamesbykevin.linkedinbot.util.LogFile.displayMessage;

public class AgentHelper {

    //the shortest amount of time we pause for
    public static long PAUSE_DELAY_MIN;

    //when we pause let's do it at a random time with this range added onto the minimum
    public static long PAUSE_DELAY_RANGE = 5000L;

    //object used to pick random time
    private static Random random;

    private static Random getRandom() {
        if (random == null)
            random = new Random(System.currentTimeMillis());

        return random;
    }

    protected static WebElement getWebElement(WebDriver driver, By by) {

        WebElement element = null;

        //how many attempts
        int count = 1;

        //maximum number of attempts
        final int limit = 15;

        while (count <= limit) {

            try {

                //look for the element
                element = driver.findElement(by);

                //if we found the element and it is displayed
                if (element != null && element.isDisplayed())
                    break;

            } catch (Exception e) {
                //don't print because it will be too much
            }

            //add to count
            count++;
        }

        //return our object
        return element;
    }

    protected static void pause() {

        try {

            //pick random sleep time
            final long sleep = PAUSE_DELAY_MIN + (getRandom().nextInt((int)PAUSE_DELAY_RANGE));
            System.out.println("Sleeping for (milliseconds): " + sleep);
            Thread.sleep(sleep);

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Sleep completed");
    }

    protected static void clickLink(WebDriver driver, By by, String message) {

        //print message to command prompt
        displayMessage(message);

        //locate our element that we want to simulate a click
        WebElement element = getWebElement(driver, by);

        //click it as long as it is there
        if (element != null)
            element.click();

        //wait a moment afterwards for the content to load
        pause();
    }

    public static void recycle(List<WebElement> elements) {

        //before recycling make sure not null
        if (elements != null) {

            for (int index = 0; index < elements.size(); index++) {
                recycle(elements.get(index));
            }

            elements.clear();
            elements = null;
        }
    }

    public static void recycle(WebElement element) {

        if (element != null) {
            element.clear();
            element = null;
        }
    }
}