package edu.tracker.monitor.screen;

/**
 * Created by Tobi on 26/02/2018.
 */

public class PhoneScreenData {

    private int unlocks, screenOn, screenOff;

    public void reset() {
        unlocks = 0;
        screenOn = 0;
        screenOff = 0;
    }

    public int getUnlocks() {
        return unlocks;
    }

    public void setUnlocks(int unlocks) {
        this.unlocks = unlocks;
    }

    public void addUnlock() {
        unlocks++;
    }

    public int getScreenOn() {
        return screenOn;
    }

    public void addScreenOn() {
        screenOn++;
    }

    public void setScreenOn(int screenOn) {
        this.screenOn = screenOn;
    }

    public int getScreenOff() {
        return screenOff;
    }

    public void setScreenOff(int screenOff) {
        this.screenOff = screenOff;
    }

    public void addScreenOff() {
        screenOff++;
    }
}
