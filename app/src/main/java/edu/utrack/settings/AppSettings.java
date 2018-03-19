package edu.utrack.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.utrack.data.calendar.CalendarData;

/**
 * Created by Tobi on 13/03/2018.
 */

public class AppSettings {

    private File saveFile;

    public CalendarData currentCalendar;

    public AppSettings(File saveFile) {
        this.saveFile = saveFile;
    }

    public void save() throws FileNotFoundException {
        ConfigSection section = new ConfigSection();
        if(currentCalendar != null) section.set("calendar", currentCalendar);
        section.write(saveFile);
    }

    public void load() throws IOException {
        if(!saveFile.exists()) save();

        ConfigSection section = ConfigSection.read(saveFile);
        currentCalendar = section.contains("calendar") ? section.getType("calendar", CalendarData.class) : null;
    }

    private File getFile() {
        return saveFile;
    }
}
