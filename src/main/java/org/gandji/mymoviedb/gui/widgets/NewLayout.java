package org.gandji.mymoviedb.gui.widgets;

import org.gandji.mymoviedb.MyMovieDBConfiguration;
import org.gandji.mymoviedb.MyMovieDBPreferences;
import org.gandji.mymoviedb.data.HibernateMovieDao;
import org.gandji.mymoviedb.data.repositories.MovieRepository;
import org.gandji.mymoviedb.filefinder.VideoFileWorker;
import org.gandji.mymoviedb.gui.MovieDataModelPoster;
import org.gandji.mymoviedb.gui.ScanADirectoryWorker;
import org.gandji.mymoviedb.tools.RepairDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
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
public class NewLayout extends JFrame {

    Logger LOG = LoggerFactory.getLogger(NewLayout.class);

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

    private DbDisplayTable dbDisplayTable;
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

        /* REMOVE dbDisplayTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{

                }
        ));
        */

        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane1.setPreferredSize(new Dimension(movieDataModelPoster.getPreferredWidth(0),5000));

        // the right hand column
        rightColumn = new JTabbedPane();

        int preferredRightColumnWidth = preferences.getRightColumnWidth();
        int preferredHeight = preferences.getMainHeight();
        JPanel searchPanelContainer = new JPanel();
        searchPanelContainer.setLayout(new BoxLayout(searchPanelContainer, BoxLayout.PAGE_AXIS));
        searchPanelContainer.setPreferredSize(new Dimension(preferredRightColumnWidth,preferredHeight));

        searchWindow.getContentPane().setPreferredSize(new Dimension(preferredRightColumnWidth, preferredHeight));
        searchPanelContainer.add(searchWindow.getContentPane());
        searchPanelContainer.add(new Box.Filler(new Dimension(preferredRightColumnWidth, 0),
                new Dimension(preferredRightColumnWidth, preferredHeight),
                new Dimension(preferredRightColumnWidth, preferredHeight)));

        rightColumn.addTab("Search", searchPanelContainer);

        JPanel preferencesPanel = new JPanel();
        preferencesPanel.setLayout(new BoxLayout(preferencesPanel, BoxLayout.PAGE_AXIS));
        preferencesPanel.add(preferencesWindow.getContentPane());
        preferencesPanel.add(new Box.Filler(new Dimension(preferredRightColumnWidth, 0),
                new Dimension(preferredRightColumnWidth, preferredHeight),
                new Dimension(preferredRightColumnWidth, preferredHeight)));
        rightColumn.addTab("Preferences", preferencesPanel);

        JPanel statisticsTab = new JPanel();
        statisticsTab.setLayout(new BoxLayout(statisticsTab,BoxLayout.PAGE_AXIS));
        statisticsTab.add(statisticsPanel);
        statisticsTab.add(new Box.Filler(new Dimension(preferredRightColumnWidth, 0),
                new Dimension(preferredRightColumnWidth, preferredHeight),
                new Dimension(preferredRightColumnWidth, preferredHeight)));
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
        MovieDescriptionPanel movieDescriptionPanel = (MovieDescriptionPanel) applicationContext.getBean("movieDescriptionPanel");
        dbDisplayTable = (DbDisplayTable) applicationContext.getBean("dbDisplayTable", movieDescriptionPanel);
        jScrollPane1.setViewportView(dbDisplayTable);

        // put things in the main layout
        // default layout is BorderLayout
        BorderLayout borderLayout = new BorderLayout();
        getContentPane().setLayout(borderLayout);
        /*borderLayout.addLayoutComponent(rightColumn, BorderLayout.LINE_END);
        borderLayout.addLayoutComponent(jScrollPane1, BorderLayout.LINE_START);
        borderLayout.addLayoutComponent(movieDescriptionPanel.getPanel(),BorderLayout.CENTER);*/
        getContentPane().add(jScrollPane1, BorderLayout.LINE_START);
        getContentPane().add(movieDescriptionPanel.getPanel(), BorderLayout.CENTER);
        getContentPane().add(rightColumn,BorderLayout.LINE_END);

        getContentPane().setPreferredSize(new Dimension(preferences.getMainWidth(),preferences.getMainHeight()));

        pack();

    }

    private void addFile(java.awt.event.ActionEvent evt) {
        Preferences prefs = Preferences.userNodeForPackage(MyMovieDBConfiguration.class);
        String lastDir = prefs.get("lastdirused", System.getProperty("user.home") + "/Downloads/Video");

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(lastDir));
        int rep = chooser.showOpenDialog(null);
        if (rep == JFileChooser.APPROVE_OPTION) {
            try {

                Path fileToProcess = chooser.getSelectedFile().toPath();

                prefs.put("lastdirused", chooser.getSelectedFile().getParent());

                VideoFileWorker videoFileWorker = (VideoFileWorker) applicationContext.getBean("videoFileWorker");
                videoFileWorker.setFile(fileToProcess);
                videoFileWorker.setLimitPopups(false);
                videoFileWorker.setMovie(null);

                videoFileWorker.execute();

            } catch (Exception ex) {
                LOG.error("Cannot access file " + chooser.getSelectedFile().toString(), ex);
            }
        }
    }

    private void scanADirectory(java.awt.event.ActionEvent evt) {

        Preferences prefs = Preferences.userNodeForPackage(MyMovieDBConfiguration.class);
        String lastDir = prefs.get("lastdirused", System.getProperty("user.home") + "/Downloads/Video");

        /*
        In this case, you have two choices.
        You could provide SwingWorker with a callback interface,
        which it would be able to call from done
        once the SwingWorker has completed.

        Or you could attach a PropertyChangeListener to the SwingWorker
        and monitor the state value, waiting until it equals StateValue.Done
        */
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(lastDir));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Choose directory");
        chooser.setAcceptAllFileFilterUsed(false);
        int rep = chooser.showOpenDialog(null);
        if (rep == JFileChooser.APPROVE_OPTION) {
            prefs.put("lastdirused", chooser.getSelectedFile().getParent());
            Path dirToProcess = chooser.getSelectedFile().toPath();
            ScanADirectoryWorker sdw = (ScanADirectoryWorker) applicationContext.getBean("scanADirectoryWorker");
            sdw.setDirToProcess(dirToProcess);
            sdw.execute();

        } else /* CANCEL_OPTION or ERROR_OPTION*/ {
            //System.out.println("No Selection ");
        }

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
