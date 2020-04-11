package org.gandji.mymoviedb.gui.widgets;

import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.MyMovieDBPreferences;
import org.gandji.mymoviedb.filefinder.VideoFileWorker;
import org.gandji.mymoviedb.services.LaunchServices;
import org.gandji.mymoviedb.tools.RepairDatabase;
import org.gandji.mymoviedb.MyMovieDBConfiguration;
import org.gandji.mymoviedb.gui.MovieDataModelPoster;
import org.gandji.mymoviedb.gui.ScanADirectoryWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.util.prefs.Preferences;

/**
 * Created by gandji on 17/11/2017.
 */
@Component
@Slf4j
public class NewLayout extends JFrame {

    @Autowired
    private MyMovieDBPreferences preferences;

    @Autowired
    private SearchWindow searchWindow;

    @Autowired
    private LogDisplay logDisplay;

    @Autowired
    private PreferencesWindow preferencesWindow;

    @Autowired
    private StatisticsPanel statisticsPanel;

    @Autowired
    private RepairDatabase repairDatabase;

    @Autowired
    private ApplicationContext applicationContext;

    // the db display
    @Autowired
    private MovieDataModelPoster movieDataModelPoster;

    @Autowired
    private AboutWindow aboutWindow;

    @Autowired
    private DbDisplayTable dbDisplayTable;

    @Autowired
    LaunchServices launchServices;

    private JScrollPane jScrollPane1;

    // the main menu bar
    private javax.swing.JMenuItem AddDirectoryMenuItem;
    private javax.swing.JMenuItem AddFileMenuItem;
    private javax.swing.JMenuItem ExitMenuItem;
    private javax.swing.JMenu FileMenu;
    private javax.swing.JMenuBar MenuBar;
    private javax.swing.JMenuItem repairDatabaseMenuItem;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenu toolsMenu;
    private JToggleButton jToggleButton1;
    private JTabbedPane rightColumn;

    public NewLayout() {
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setTitle("MyMovieDB");
    }


    private void createComponents() {

        int minHeight = 300;

        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane1.setMinimumSize(new Dimension(movieDataModelPoster.getPreferredWidth(0),minHeight));
        jScrollPane1.setPreferredSize(new Dimension(movieDataModelPoster.getPreferredWidth(0),5000));
        jScrollPane1.setPreferredSize(new Dimension(movieDataModelPoster.getPreferredWidth(0),5000));

        // the right hand column
        rightColumn = new JTabbedPane();

        int ppi = Toolkit.getDefaultToolkit().getScreenResolution();
        int preferredRightColumnWidth = preferences.getRightColumnWidth()*ppi/96;
        int preferredHeight = preferences.getMainHeight();


        rightColumn.setMinimumSize(new Dimension(preferredRightColumnWidth/2,minHeight));
        rightColumn.setPreferredSize(new Dimension(preferredRightColumnWidth,preferredHeight));
        rightColumn.setMaximumSize(new Dimension(preferredRightColumnWidth*2,Short.MAX_VALUE));


        JPanel searchPanelContainer = new JPanel();
        searchPanelContainer.setLayout(new BoxLayout(searchPanelContainer, BoxLayout.PAGE_AXIS));

        searchPanelContainer.add(searchWindow.getContentPane());
        Box.Filler vFiller1 = (Box.Filler)Box.createVerticalGlue();
        vFiller1.changeShape(vFiller1.getMinimumSize(),new Dimension(0,Short.MAX_VALUE),
                vFiller1.getMaximumSize());
        searchPanelContainer.add(vFiller1);

        rightColumn.addTab("Search", searchPanelContainer);

        JPanel preferencesPanel = new JPanel();
        preferencesPanel.setLayout(new BoxLayout(preferencesPanel, BoxLayout.PAGE_AXIS));
        preferencesPanel.add(preferencesWindow.getContentPane());
        Box.Filler vFiller2 = (Box.Filler)Box.createVerticalGlue();
        vFiller2.changeShape(vFiller2.getMinimumSize(),new Dimension(0,Short.MAX_VALUE),
                vFiller2.getMaximumSize());
        preferencesPanel.add(vFiller2);
        rightColumn.addTab("Preferences", preferencesPanel);

        JPanel statisticsTab = new JPanel();
        statisticsTab.setLayout(new BoxLayout(statisticsTab,BoxLayout.PAGE_AXIS));
        statisticsTab.add(statisticsPanel);
        rightColumn.addTab("Statistics", statisticsTab);

        rightColumn.addChangeListener(e -> {
            if (rightColumn.getSelectedComponent()==statisticsTab) {
                statisticsPanel.refresh();
            }
        });

        // the menus
        FileMenu = new JMenu();
        FileMenu.setText("File");

        AddFileMenuItem = new javax.swing.JMenuItem();
        AddFileMenuItem.setText("Add a file");
        AddFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFile(evt);
            }
        });
        FileMenu.add(AddFileMenuItem);

        AddDirectoryMenuItem = new javax.swing.JMenuItem();
        AddDirectoryMenuItem.setText("Scan a directory");
        AddDirectoryMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scanADirectory(evt);
            }
        });
        FileMenu.add(AddDirectoryMenuItem);

        JPopupMenu.Separator jSeparator1 = new javax.swing.JPopupMenu.Separator();
        FileMenu.add(jSeparator1);

        ExitMenuItem = new javax.swing.JMenuItem();
        ExitMenuItem.setText("Exit");
        ExitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitAction(evt);
            }
        });
        FileMenu.add(ExitMenuItem);

        MenuBar = new JMenuBar();
        MenuBar.add(FileMenu);

        toolsMenu = new JMenu();
        toolsMenu.setText("Tools");

        repairDatabaseMenuItem = new javax.swing.JMenuItem();
        repairDatabaseMenuItem.setText("Repair database");
        repairDatabaseMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                repairDatabaseAction(evt);
            }
        });
        toolsMenu.add(repairDatabaseMenuItem);

        aboutMenuItem = new JMenuItem();
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                aboutAction(evt);
            }
        });
        toolsMenu.add(aboutMenuItem);

        MenuBar.add(toolsMenu);

        Box.Filler filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        MenuBar.add(filler1);

        jToggleButton1 = new JToggleButton();
        jToggleButton1.setText("log");
        jToggleButton1.setFocusable(false);
        jToggleButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logDisplay.setVisible(!logDisplay.isVisible());
            }
        });
        MenuBar.add(jToggleButton1);

        setJMenuBar(MenuBar);

    }

    @PostConstruct
    public void postConstruct() {

        createComponents();

        // we want the toggle button to get back to "false" if user closes
        // the window directly
        logDisplay.addCloseCallback(new LogDisplay.CloseCallback() {
            @Override
            public void closing() {
                jToggleButton1.setSelected(false);
            }
        });
        dbDisplayTable = (DbDisplayTable) applicationContext.getBean("dbDisplayTable");
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.PAGE_AXIS));
        leftPanel.add(dbDisplayTable.getTitleLabel());
        leftPanel.add(jScrollPane1);
        jScrollPane1.setViewportView(dbDisplayTable);

        // put things in the main layout
        // default layout is BorderLayout
        BorderLayout borderLayout = new BorderLayout();
        getContentPane().setLayout(borderLayout);
        getContentPane().add(leftPanel, BorderLayout.LINE_START);
        getContentPane().add(dbDisplayTable.getTabbedPane(), BorderLayout.CENTER);
        getContentPane().add(rightColumn,BorderLayout.LINE_END);

        getContentPane().setPreferredSize(new Dimension(preferences.getMainWidth(),preferences.getMainHeight()));

        pack();

    }

    private void addFile(java.awt.event.ActionEvent evt) {
        launchServices.addFileInBackground(null, false);
    }

    private void scanADirectory(java.awt.event.ActionEvent evt) {
        launchServices.scanADirectoryInBackground();
    }

    private void exitAction(java.awt.event.ActionEvent evt) {
        Object[] options = {"Yes",
                "No",
                "Cancel"};
        int n = JOptionPane.showOptionDialog(this,
                "Exit MyMovieDB?",
                "Confirm",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[2]);
        //JDialog dialog = new JDialog();
        if (0 == n) {
            System.exit(0);
        }

    }

    private void repairDatabaseAction(java.awt.event.ActionEvent evt) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                repairDatabase.doRepair();
                return null;
            }
        }.execute();
    }

    private void aboutAction(ActionEvent evt) {
        aboutWindow.setVisible(true);
    }


}
