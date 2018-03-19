package edu.utrack.monitor.app;

import android.content.Context;

import java.util.List;
import java.util.Objects;

import edu.utrack.calendar.CalendarTracker;
import edu.utrack.data.app.ForegroundAppInfo;
import edu.utrack.data.calendar.CalendarEvent;
import edu.utrack.monitor.MonitorService;
import edu.utrack.util.BiCallback;
import edu.utrack.util.Callback;

/**
 * Created by Tobi on 04/03/2018.
 */

public class ActivityMonitor {

    private MonitorService context;
    private Thread thread;
    private int delayTime;
    private boolean active = false;

    private AppDetector detector;
    private AppChangeEvent callback;

    private ForegroundAppInfo currentApp;
    private long appStartTime;

    public ActivityMonitor(MonitorService context, int delayTime, AppChangeEvent callback) {
        this.context = context;
        this.delayTime = delayTime;
        this.callback = callback;
        this.thread = new Thread(this::startSchedule);
        detector = new AppDetector(context);
    }

    public void start() {
        if(!active) thread.start();
    }

    public void stop() {
        if(active) thread.interrupt();
    }

    private void startSchedule() {
        while(true) {
            try {
                Thread.sleep(delayTime);
                tick();
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    //TODO Right now, it constantly checks what app is in the foreground, and saves its own data.
    //With lollipop, we can get a log of all these events. It may be worth adapting the implementation
    //to use this if its supported in lollipop instead of polling every second.

    private void tick() {
        List<CalendarEvent> currentEvents = context.getCalendarTracker().getCurrentEvents();
        if(currentEvents.isEmpty()) return;

        long time = System.currentTimeMillis();
        ForegroundAppInfo info = detector.getForegroundApp();

        for(CalendarEvent event : currentEvents) {
            //The event will have ended by the next time the method is called, so pretend that the app was closed
            if(System.currentTimeMillis() + delayTime >= event.getEndTime()) {
                if(currentApp == null) {
                    long estimatedTime = event.getEndTime() - System.currentTimeMillis();
                    if(estimatedTime != 0) callback.appChange(info, null, System.currentTimeMillis(), estimatedTime, event);
                }
            }
        }

        if(info == currentApp || Objects.equals(info, currentApp)) return;

        if(currentApp == null) {
            currentApp = info;
            appStartTime = System.currentTimeMillis();
        } else {
            //Different apps
            long endTime = System.currentTimeMillis();
            long timeSpent = endTime - appStartTime;

            for(CalendarEvent event : currentEvents) {
                if(System.currentTimeMillis() + delayTime < event.getEndTime()) {
                    callback.appChange(currentApp, info, appStartTime, timeSpent, event);
                }
            }

            currentApp = info;
            appStartTime = endTime;
        }
    }

    public int getDelayTime() {
        return delayTime;
    }

    public boolean isActive() {
        return active;
    }

    public AppChangeEvent getCallback() {
        return callback;
    }

    @FunctionalInterface
    public interface AppChangeEvent {
        void appChange(ForegroundAppInfo from, ForegroundAppInfo to, long startTime, long timeSpent, CalendarEvent event);
    }
}

