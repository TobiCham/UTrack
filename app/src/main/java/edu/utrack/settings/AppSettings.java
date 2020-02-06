package edu.utrack.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Tobi on 13/03/2018.
 */
public class AppSettings {

    private File saveFile;

    public int currentCalendarID = -1;
    public boolean tracks = true;
    public HistorySettingType historySetting = HistorySettingType.FOUR_WEEKS;
    public long lastDeletionTime = -1;
    public boolean developer = false;

    public AppSettings(File saveFile) {
        this.saveFile = saveFile;
    }

    public void save() {
        ConfigSection section = new ConfigSection();

        if(historySetting == null) historySetting = HistorySettingType.FOUR_WEEKS;

        section.set("calendarID", currentCalendarID);
        section.set("track", tracks);
        section.set("history-type", historySetting.toString());
        section.set("last-deletion-time", lastDeletionTime);
        section.set("developer", developer);
        try {
            section.write(saveFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if(!saveFile.exists()) save();

        ConfigSection section = null;
        try {
            section = ConfigSection.read(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
            section = new ConfigSection();
        }
        currentCalendarID = section.contains("calendarID") ? section.getInteger("calendarID") : -1;
        tracks = section.getBoolean("track");
        historySetting = HistorySettingType.fromName(section.getString("history-type"));
        if(historySetting == null) historySetting = HistorySettingType.FOUR_WEEKS;
        lastDeletionTime = section.contains("last-deletion-time") ? section.getLong("last-deletion-time") : -1;
        developer = section.contains("developer") && section.getBoolean("developer");
    }

    private File getFile() {
        return saveFile;
    }
}
