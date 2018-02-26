package edu.tracker.monitor.screen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import edu.tracker.MainActivity;

import static android.content.Intent.*;

public class PhoneScreenListener extends BroadcastReceiver {

    private PhoneScreenData data;

    public PhoneScreenListener() {
        data = new PhoneScreenData();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action.equals(ACTION_USER_PRESENT)) data.addUnlock();
        if(action.equals(ACTION_SCREEN_ON)) data.addScreenOn();
        if(action.equals(ACTION_SCREEN_OFF)) data.addScreenOff();

        if(context instanceof MainActivity) ((MainActivity) context).updateUnlocks();
    }

    public PhoneScreenData getData() {
        return data;
    }
}
