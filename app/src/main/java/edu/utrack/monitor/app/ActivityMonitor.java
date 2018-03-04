package edu.utrack.monitor.app;

import android.content.Context;

import java.util.function.Consumer;

/**
 * Created by Tobi on 04/03/2018.
 */

public class ActivityMonitor {

    private Context context;
    private Thread thread;
    private int delayTime;
    private boolean active = false;

    private AppDetector detector;
    private Consumer<ForegroundAppInfo> callback;

    public ActivityMonitor(Context context, int delayTime, Consumer<ForegroundAppInfo> callback) {
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
                callback.accept(detector.getForegroundApp());
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public int getDelayTime() {
        return delayTime;
    }

    public boolean isActive() {
        return active;
    }

    public Consumer<ForegroundAppInfo> getCallback() {
        return callback;
    }
}

