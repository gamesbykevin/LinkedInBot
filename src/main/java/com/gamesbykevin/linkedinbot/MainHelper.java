package com.gamesbykevin.linkedinbot;

import com.gamesbykevin.linkedinbot.agent.Agent;

import java.util.concurrent.TimeUnit;

import static com.gamesbykevin.linkedinbot.util.LogFile.displayMessage;

public class MainHelper {

    public static String getDurationDesc(final long duration) {

        return String.format(
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(duration),
            TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
            TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
    }
}