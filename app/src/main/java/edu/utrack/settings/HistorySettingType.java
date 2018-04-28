package edu.utrack.settings;

public enum HistorySettingType {

    ONE_WEEK(7, "1 Week"),
    TWO_WEEKS(14, "2 Weeks"),
    FOUR_WEEKS(28, "4 Weeks"),
    EIGHT_WEEKS(56, "8 Weeks"),
    TWELVE_WEEKS(84, "12 Weeks"),
    SIX_MONTHS(182, "6 Months"),

    //This is apparently over 5.8 million years, just to clarify if this number is big enough
    FOREVER(Integer.MAX_VALUE, "Forever");

    private int days;
    private String description;

    HistorySettingType(int days, String description) {
        this.days = days;
        this.description = description;
    }

    public int getDays() {
        return days;
    }

    public String getDescription() {
        return description;
    }

    public int getIndex() {
        for(int i = 0; i < values().length; i++) {
            if(values()[i] == this) return i;
        }
        return -1; //If this happens?????
    }

    /**
     * @param name name of the enum constant
     * @return the enum value, or null if none found
     */
    public static HistorySettingType fromName(String name) {
        for(HistorySettingType type : values()) {
            if(type.toString().equalsIgnoreCase(name) || type.getDescription().equalsIgnoreCase(name)) return type;
        }
        return null;
    }

    public static CharSequence[] getOptions() {
        CharSequence[] options = new CharSequence[HistorySettingType.values().length];
        for(int i = 0; i < values().length; i++) {
            options[i] = values()[i].getDescription();
        }
        return options;
    }
}
