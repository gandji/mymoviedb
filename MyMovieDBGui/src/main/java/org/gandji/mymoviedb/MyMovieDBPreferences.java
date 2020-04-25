package org.gandji.mymoviedb;

import org.gandji.mymoviedb.scrapy.MovieInfoSearchService;
import org.springframework.stereotype.Component;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

@Component
public class MyMovieDBPreferences {
    /*private String dataSourceUrl;
    private String dataSourceUser;
    private String dataSourcePassword;
    private String dataSourceDriver;*/

    public enum GuiMode {
        SWING("swing", "swing"),
        JAVAFX("javafx", "javafx");

        String preferencesName;
        String displayName;

        GuiMode(String preferencesName, String displayName) {
            this.preferencesName = preferencesName;
            this.displayName = displayName;
        }

        public static GuiMode fromPreferencesName(String preferencesName) {
            for (GuiMode guiMode : values()) {
                if (guiMode.preferencesName.equals(preferencesName)) {
                    return guiMode;
                }
            }
            throw new IllegalArgumentException("Unknown gui mode (internal name): "+preferencesName);
        }

        public static GuiMode fromDisplayName(String displayName) {
            for (GuiMode guiMode : values()) {
                if (guiMode.displayName.equals(displayName)) {
                    return guiMode;
                }
            }
            throw new IllegalArgumentException("Unknown gui mode (human readable name): "+displayName);
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private GuiMode guiMode = GuiMode.JAVAFX;
    private boolean keepDuplicateFilesOnScan = false;
    private boolean fullFeatured = true;
    private boolean hackersHack= false;
    private String  dateFormat = DEFAULT_DATE_FORMAT;
    private Integer fontSize = DEFAULT_FONT_SIZE;
    private Integer dbPageSize = DEFAULT_DB_PAGE_SIZE;
    private Integer rightColumnWidth = DEFAULT_RIGHT_COLUMN_WIDTH;
    /*private Integer mainHeight = DEFAULT_MAIN_HEIGHT;
    private Integer mainWidth = DEFAULT_MAIN_WIDTH;*/
    private MovieInfoSearchService.UrlType internetTarget = DEFAULT_INTERNET_TARGET;

    // Preference keys for this package
    /*private static final String DATASOURCE_URL = "datasourceurl";
    private static final String DATASOURCE_USERNAME = "datasourceusername";
    private static final String DATASOURCE_PASSWORD = "datasourcepassword";
    private static final String DATASOURCE_DRIVER_NAME = "datasourcedrivername";*/
    private static  final String GUI_MODE = "guimode";
    private static final String POSTER_HEIGHT = "posterheight";
    private static final String POSTER_WIDTH = "posterwidth";
    private static final String KEEP_DUPLICATE_FILES_ON_SCAN = "keepduplicatesonscan";
    private static final String FULL_FEATURED = "fullfeatured";
    private static final String HACKERS_HACK = "hackershack";
    private static final String DATE_FORMAT = "dateformat";
    private static final String FONT_SIZE = "fontsize";
    private static final String DB_PAGE_SIZE = "dbpagesize";
    private static final String RIGHT_COLUMN_WIDTH = "rightcolumnwidth";
    /*private static final String MAIN_HEIGHT = "mainheight";
    private static final String MAIN_WIDTH = "mainwidth";*/
    private static final String INTERNET_TARGET = "internetTarget";

    private final static String DEFAULT_DATE_FORMAT = "dd/mm/yyyy hh:mm::ss";
    private static final Integer DEFAULT_FONT_SIZE = 24;
    private static final Integer DEFAULT_DB_PAGE_SIZE = 300;
    private static final Integer DEFAULT_RIGHT_COLUMN_WIDTH = 550;
    private static final Integer DEFAULT_MAIN_HEIGHT = 800;
    private static final Integer DEFAULT_MAIN_WIDTH = 1700;
    private static final MovieInfoSearchService.UrlType DEFAULT_INTERNET_TARGET = MovieInfoSearchService.UrlType.TMDB;

    private int posterHeight;
    private int posterWidth;

    public MyMovieDBPreferences() {
        resetPrefs();
    }

    public boolean isFullFeatured() {
        return fullFeatured;
    }

    public void setFullFeatured(boolean fullFeatured) {
        this.fullFeatured = fullFeatured;
    }

    public boolean isHackersHack() {
        return hackersHack;
    }

    public void setHackersHack(boolean hackersHack) {
        this.hackersHack = hackersHack;
    }

    public boolean isKeepDuplicateFilesOnScan() { return keepDuplicateFilesOnScan; }

    public void setKeepDuplicateFilesOnScan(boolean keepDuplicateFilesOnScan) { this.keepDuplicateFilesOnScan = keepDuplicateFilesOnScan; }

    public GuiMode getGuiMode() {
        return guiMode;
    }

    public void setGuiMode(GuiMode guiMode) {
        this.guiMode = guiMode;
    }

    public Integer getFontSize() { return this.fontSize; }

    public void setFontSize(Integer fontSize) { this.fontSize = fontSize; }

    public Integer getDbPageSize() {
        return dbPageSize;
    }

    public void setDbPageSize(Integer dbPageSize) {
        this.dbPageSize = dbPageSize;
    }

    public Integer getRightColumnWidth() {
        return rightColumnWidth;
    }

    public void setRightColumnWidth(Integer rightColumnWidth) {
        this.rightColumnWidth = rightColumnWidth;
    }

    /*public Integer getMainHeight() {
        return mainHeight;
    }

    public void setMainHeight(Integer mainHeight) {
        this.mainHeight = mainHeight;
    }

    public Integer getMainWidth() {
        return mainWidth;
    }

    public void setMainWidth(Integer mainWidth) {
        this.mainWidth = mainWidth;
    }*/

    public void flushPrefs() {
       Preferences prefs = Preferences.userNodeForPackage(MyMovieDBConfiguration.class);
       prefs.put(GUI_MODE, guiMode.preferencesName);
       prefs.putInt(POSTER_HEIGHT,posterHeight);
       prefs.putInt(POSTER_WIDTH,posterWidth);
       prefs.putBoolean(KEEP_DUPLICATE_FILES_ON_SCAN,keepDuplicateFilesOnScan);
       prefs.putBoolean(FULL_FEATURED,fullFeatured);
       prefs.putBoolean(HACKERS_HACK,hackersHack);
       prefs.put(DATE_FORMAT, dateFormat);
       prefs.putInt(FONT_SIZE, fontSize);
       prefs.putInt(DB_PAGE_SIZE, dbPageSize);
       prefs.putInt(RIGHT_COLUMN_WIDTH, rightColumnWidth);
       /*prefs.putInt(MAIN_HEIGHT, mainHeight);
       prefs.putInt(MAIN_WIDTH, mainWidth);*/
       prefs.put(INTERNET_TARGET, internetTarget.name());
       try {
           prefs.flush();
       } catch (BackingStoreException e) {
           e.printStackTrace();
       }
   }

   public void resetPrefs() {
       Preferences prefs = Preferences.userNodeForPackage(MyMovieDBConfiguration.class);
       setGuiMode(GuiMode.fromPreferencesName(prefs.get(GUI_MODE, GuiMode.SWING.preferencesName)));
       setPosterHeight(prefs.getInt(POSTER_HEIGHT,268));
       setPosterWidth(prefs.getInt(POSTER_WIDTH,182));
       setKeepDuplicateFilesOnScan(prefs.getBoolean(KEEP_DUPLICATE_FILES_ON_SCAN,false));
       setFullFeatured(prefs.getBoolean(FULL_FEATURED,true));
       setHackersHack(prefs.getBoolean(HACKERS_HACK,false));
       setDateFormat(prefs.get(DATE_FORMAT, DEFAULT_DATE_FORMAT));
       setFontSize(prefs.getInt(FONT_SIZE, DEFAULT_FONT_SIZE));
       setDbPageSize(prefs.getInt(DB_PAGE_SIZE, DEFAULT_DB_PAGE_SIZE));
       setRightColumnWidth(prefs.getInt(RIGHT_COLUMN_WIDTH, DEFAULT_RIGHT_COLUMN_WIDTH));
       /*setMainHeight(prefs.getInt(MAIN_HEIGHT, DEFAULT_MAIN_HEIGHT));
       setMainWidth(prefs.getInt(MAIN_WIDTH, DEFAULT_MAIN_WIDTH));*/
       setInternetTarget(MovieInfoSearchService.UrlType.fromName(prefs.get(INTERNET_TARGET, DEFAULT_INTERNET_TARGET.name())));
       try {
           prefs.flush();
       } catch (BackingStoreException e) {
           e.printStackTrace();
       }
   }

    public void setPosterHeight(int posterHeight) {
        this.posterHeight = posterHeight;
        Preferences prefs = Preferences.userNodeForPackage(MyMovieDBConfiguration.class);
        prefs.putInt(POSTER_HEIGHT,posterHeight);
    }

    public int getPosterHeight() {
        return posterHeight;
    }

    public void setPosterWidth(int posterWidth) {
        this.posterWidth = posterWidth;
        Preferences prefs = Preferences.userNodeForPackage(MyMovieDBConfiguration.class);
        prefs.putInt(POSTER_WIDTH,posterWidth);
    }

    public int getPosterWidth() {
        return posterWidth;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setInternetTarget(MovieInfoSearchService.UrlType internetTarget) {
        this.internetTarget = internetTarget;
    }

    public MovieInfoSearchService.UrlType getInternetTarget() {
        return internetTarget;
    }
}