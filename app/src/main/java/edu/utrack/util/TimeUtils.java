package edu.utrack.util;

public class TimeUtils {

    public static String formatTimeShort(int totalSeconds) {

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = (totalSeconds / (60 * 60)) % 60;
        int days = (totalSeconds / (60 * 60 * 24)) % 24;

        String timeText = "";
        if(days > 0) timeText += days + "d, ";
        if(hours > 0) timeText += hours + "h, ";
        if(minutes > 0) timeText += minutes + "m, ";
        timeText += seconds + "s";

        return timeText;
    }

    public static String formatTimeLong(int totalSeconds) {

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = (totalSeconds / (60 * 60)) % 60;
        int days = (totalSeconds / (60 * 60 * 24)) % 24;

        String timeText = "";
        if(days > 0) timeText += days + getPlural(days, " day") + ", ";
        if(hours > 0) timeText += hours + getPlural(hours, " hour") + ", ";
        if(minutes > 0) timeText += minutes + getPlural(minutes, " minute") + ", ";
        timeText += seconds + getPlural(seconds, " second");

        return timeText;
    }

    private static String getPlural(int time, String name) {
        if(time == 1) return name;
        return name + "s";
    }
}
