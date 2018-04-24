package edu.utrack.monitor.app;

import edu.utrack.data.app.ForegroundAppInfo;
import edu.utrack.monitor.MonitorService;

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
        active = true;
        while(true) {
            try {
                Thread.sleep(delayTime);

                if(!context.doesTrack()) handleCurrentApp(null);
                else handleCurrentApp(detector.getForegroundApp());
            } catch (InterruptedException e) {
                break;
            }
        }
        active = false;
    }

    //TODO Right now, it constantly checks what app is in the foreground, and saves its own data.
    //With lollipop, we can get a log of all these events. It may be worth adapting the implementation
    //to use this if its supported in lollipop instead of polling every second.
    private void handleCurrentApp(ForegroundAppInfo app) {
        //We ignore all apps which start with com.android - these are likely system apps
        if(ForegroundAppInfo.areSameApp(app, currentApp)) return;

        if(currentApp == null) {
            currentApp = app;
            appStartTime = System.currentTimeMillis();
        } else {
            //Different apps
            long endTime = System.currentTimeMillis();
            long timeSpent = endTime - appStartTime;

            callback.appChange(currentApp, app, appStartTime, timeSpent);

            currentApp = app;
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
        void appChange(ForegroundAppInfo from, ForegroundAppInfo to, long startTime, long timeSpent);
    }
}

