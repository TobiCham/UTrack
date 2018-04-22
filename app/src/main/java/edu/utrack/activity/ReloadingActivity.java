package edu.utrack.activity;

public interface ReloadingActivity {

    void handleReload();

    void setMessage(String message);

    String getReloadingMessage();

    void setContentVisible(boolean visible);
}
