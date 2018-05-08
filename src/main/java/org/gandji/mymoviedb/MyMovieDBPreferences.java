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

    private boolean keepDuplicateFilesOnScan = false;
    private String  dateFormat = DEFAULT_DATE_FORMAT;
    private Integer fontSize = DEFAULT_FONT_SIZE;
    private Integer rightColumnWidth = DEFAULT_RIGHT_COLUMN_WIDTH;
    private Integer mainHeight = DEFAULT_MAIN_HEIGHT;
    private Integer mainWidth = DEFAULT_MAIN_WIDTH;
    private MovieInfoSearchService.UrlType internetTarget = DEFAULT_INTERNET_TARGET;

    // Preference keys for this package
    /*private static final String DATASOURCE_URL = "datasourceurl";
    private static final String DATASOURCE_USERNAME = "datasourceusername";
    private static final String DATASOURCE_PASSWORD = "datasourcepassword";
    private static final String DATASOURCE_DRIVER_NAME = "datasourcedrivername";*/
    private static final String POSTER_HEIGHT = "posterheight";
    private static final String POSTER_WIDTH = "posterwidth";
    private static final String KEEP_DUPLICATE_FILES_ON_SCAN = "keepduplicatesonscan";
    private static final String DATE_FORMAT = "dateformat";
    private static final String FONT_SIZE = "fontsize";
    private static final String RIGHT_COLUMN_WIDTH = "rightcolumnwidth";
    private static final String MAIN_HEIGHT = "mainheight";
    private static final String MAIN_WIDTH = "mainwidth";
    private static final String INTERNET_TARGET = "internetTarget";

    //@Value("${spring.datasource.url}")
    //String defaultDataSourceUrl;

    // TODO REMOVE conection data from prefs @Value("${spring.datasource.username}")
    //String defaultDataSourceUsername;

    //@Value("${spring.datasource.password}")
    //String defaultDataSourcePassword;

    //@Value("${spring.datasource.driver-class-name}")
    Class defautDataSourceDriver;

   // @Value("${spring.datasource.driver-class-name}")
    String defaultDataSourceDriverName;
    private final static String DEFAULT_DATE_FORMAT = "dd/mm/yyyy hh:mm::ss";
    private static final Integer DEFAULT_FONT_SIZE = 24;
    private static final Integer DEFAULT_RIGHT_COLUMN_WIDTH = 550;
    private static final Integer DEFAULT_MAIN_HEIGHT = 800;
    private static final Integer DEFAULT_MAIN_WIDTH = 1700;
    private static final MovieInfoSearchService.UrlType DEFAULT_INTERNET_TARGET = MovieInfoSearchService.UrlType.IMDB;

    private int posterHeight;
    private int posterWidth;

    public MyMovieDBPreferences() {
        resetPrefs();
    }

    /*public String getDataSourceUrl() {
        return dataSourceUrl;
    }*/

    /*public void setDataSourceUrl(final String dataSourceUrl) {
        this.dataSourceUrl = dataSourceUrl;
    }*/

    /*public String getDataSourceUser() {
        return dataSourceUser;
    }*/

    /*
    public void setDataSourceUser(final String dataSourceUser) {
        this.dataSourceUser = dataSourceUser;
    }

    public String getDataSourcePassword() {
        return dataSourcePassword;
    }

    public void setDataSourcePassword(final String dataSourcePassword) {
        this.dataSourcePassword = dataSourcePassword;
    }

    public String getDataSourceDriver() {
        return dataSourceDriver;
    }

    public void setDataSourceDriver(String dataSourceDriver) {
        this.dataSourceDriver = dataSourceDriver;
    }
    */
    public boolean isKeepDuplicateFilesOnScan() { return keepDuplicateFilesOnScan; }

    public void setKeepDuplicateFilesOnScan(boolean keepDuplicateFilesOnScan) { this.keepDuplicateFilesOnScan = keepDuplicateFilesOnScan; }

    public Integer getFontSize() { return this.fontSize; }

    public void setFontSize(Integer fontSize) { this.fontSize = fontSize; }

    public Integer getRightColumnWidth() {
        return rightColumnWidth;
    }

    public void setRightColumnWidth(Integer rightColumnWidth) {
        this.rightColumnWidth = rightColumnWidth;
    }

    public Integer getMainHeight() {
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
    }

    public void flushPrefs() {
       Preferences prefs = Preferences.userNodeForPackage(MyMovieDBConfiguration.class);
       /*prefs.put(DATASOURCE_DRIVER_NAME,dataSourceDriver);
       prefs.put(DATASOURCE_USERNAME,dataSourceUser);
       prefs.put(DATASOURCE_URL,dataSourceUrl);
       prefs.put(DATASOURCE_PASSWORD,dataSourcePassword);*/
       prefs.putInt(POSTER_HEIGHT,posterHeight);
       prefs.putInt(POSTER_WIDTH,posterWidth);
       prefs.putBoolean(KEEP_DUPLICATE_FILES_ON_SCAN,keepDuplicateFilesOnScan);
       prefs.put(DATE_FORMAT, dateFormat);
       prefs.putInt(FONT_SIZE, fontSize);
       prefs.putInt(RIGHT_COLUMN_WIDTH, rightColumnWidth);
       prefs.putInt(MAIN_HEIGHT, mainHeight);
       prefs.putInt(MAIN_WIDTH, mainWidth);
       prefs.put(INTERNET_TARGET, internetTarget.name());
       try {
           prefs.flush();
       } catch (BackingStoreException e) {
           e.printStackTrace();
       }
   }

   public void resetPrefs() {
       Preferences prefs = Preferences.userNodeForPackage(MyMovieDBConfiguration.class);
       /*setDataSourcePassword(prefs.get(DATASOURCE_PASSWORD,defaultDataSourcePassword));
       setDataSourceUrl(prefs.get(DATASOURCE_URL,defaultDataSourceUrl));
       setDataSourceUser(prefs.get(DATASOURCE_USERNAME,defaultDataSourceUsername));
       setDataSourceDriver(prefs.get(DATASOURCE_DRIVER_NAME,defaultDataSourceDriverName));*/
       setPosterHeight(prefs.getInt(POSTER_HEIGHT,268));
       setPosterWidth(prefs.getInt(POSTER_WIDTH,182));
       setKeepDuplicateFilesOnScan(prefs.getBoolean(KEEP_DUPLICATE_FILES_ON_SCAN,false));
       setDateFormat(prefs.get(DATE_FORMAT, DEFAULT_DATE_FORMAT));
       setFontSize(prefs.getInt(FONT_SIZE, DEFAULT_FONT_SIZE));
       setRightColumnWidth(prefs.getInt(RIGHT_COLUMN_WIDTH, DEFAULT_RIGHT_COLUMN_WIDTH));
       setMainHeight(prefs.getInt(MAIN_HEIGHT, DEFAULT_MAIN_HEIGHT));
       setMainWidth(prefs.getInt(MAIN_WIDTH, DEFAULT_MAIN_WIDTH));
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