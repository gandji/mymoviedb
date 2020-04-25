package org.gandji.mymoviedb.gui.widgets;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.MyMovieDBPreferences;
import org.gandji.mymoviedb.gui.FileDataModel;
import org.gandji.mymoviedb.gui.MovieGuiService;
import org.gandji.mymoviedb.data.*;
import org.gandji.mymoviedb.services.LaunchServices;
import org.gandji.mymoviedb.services.MovieDaoServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableColumn;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by gandji on 05/04/2017.
 */
@Component
@Scope("prototype")
@Slf4j
public class MovieDescriptionPanel extends JPanel implements MovieHolder {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private HibernateMovieDao hibernateMovieDao;

    @Autowired
    private MovieDaoServices movieDaoServices;

    @Autowired
    private HibernateVideoFileDao hibernateVideoFileDao;

    @Autowired
    private MovieGuiService movieGuiService;

    @Autowired
    private LaunchServices launchServices;

    @Autowired
    private UserInputMovie userInputMovie;

    private Movie movie;

    @Autowired
    private FileDataModel fileDataModel;

    @Autowired
    private MyMovieDBPreferences myMovieDBPreferences;

    private JTextField titleTextField;
    private JTextField alternateTitleTextField;
    private JTextField directorTextField;
    private JList<String> actorsList;
    private JList<String> genresList;
    private JTextPane summaryTextArea;
    private JTextField yearTextField;
    private JTextField durationTextField;
    private JSpinner ratingSpinner;
    private JFormattedTextField lastSeenFormattedTextField;
    private JButton playMovieButton;
    private JButton openInfoButton;
    private JButton saveButton;
    private JButton internetCriticsButton;
    private JTable filesTable;

    public JPanel getPanel() {
        return panel;
    }

    private JPanel panel;
    private JScrollPane filesTableScrollPane;
    private JLabel posterDisplay;
    private JButton enterInfoUrlButton;
    private JButton deleteButton;
    private JLabel commentLabel;
    private JTextArea commentTextArea;
    private JButton addActorButton;
    private JButton addGenreButton;
    private JPanel actorsGenrePanel;
    private JPanel commentsPanel;
    private JPanel ratingDurationPanel;
    private JPanel buttonsPanel;
    private JPanel posterPanel;
    private JPanel titleFilesPanel;
    private JScrollPane summaryScrollPane;
    private JLabel createdLabel;
    private JTextField createdTextField;
    private JPanel summaryFilePanel;
    private JButton seenNowButton;

    public static void main(String[] args) {
        JFrame frame = new JFrame("JPanel");
        //frame.setContentPane(new MovieDescriptionPanel(frame).panel);
        frame.setContentPane(new MovieDescriptionPanel().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public MovieDescriptionPanel() {
        super();
        //createUIComponents();
    }

    @PostConstruct
    public void init() {
        createUIComponents();
    }

    @PostConstruct
    public void postConstruct() {
        JFormattedTextField.AbstractFormatter dateFormatDisplay = new DateFormatter(new SimpleDateFormat(myMovieDBPreferences.getDateFormat()));
        JFormattedTextField.AbstractFormatter dateFormatEdit = dateFormatDisplay;
        lastSeenFormattedTextField.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
        lastSeenFormattedTextField.setFormatterFactory(new DefaultFormatterFactory(dateFormatEdit, dateFormatDisplay));

        seenNowButton.addActionListener(e -> {
            lastSeenFormattedTextField.setValue(Date.from(Instant.now()));
        });

        fakePoster();
        // construct the popup menu for film rows
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItemAddAFileToMovie = new JMenuItem("Add a file for this movie");
        JMenuItem menuItemChoosePoster = new JMenuItem("Choose a poster");

        menuItemAddAFileToMovie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchServices.addFileInBackground(movie, false);
            }
        });

        popupMenu.add(menuItemAddAFileToMovie);
        popupMenu.add(menuItemChoosePoster);

        // sets the popup menu for the table
        posterDisplay.setComponentPopupMenu(popupMenu);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchServices.deleteMovie(movie, MovieDescriptionPanel.this);
                setData(null);
            }
        });


    }

    private void playMovieButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_playTheFileButtonActionPerformed
        movieGuiService.playTheMovie(movie);
    }

    private void openInfoUrlButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_openImdbButtonActionPerformed
        movieGuiService.openInfoUrl(movie.getInfoUrl());
    }

    private void internetCriticsButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_internetCriticsButtonActionPerformed
        movieGuiService.internetCritics(movie);
    }

    private void saveButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        log.info("Saving movie in DB: " + movie.getTitle());
        this.movie.setTitle(titleTextField.getText());
        this.movie.setAlternateTitle(alternateTitleTextField.getText());
        this.movie.setDirector(directorTextField.getText());
        this.movie.setComments(commentTextArea.getText());
        this.movie.setYear(yearTextField.getText());
        this.movie.setDuree(durationTextField.getText());
        this.movie.setRating((Integer) ratingSpinner.getValue());
        // @todo manage date of last seen: editor? formatter?
        //  hack for unset last seen date!
        try {
            Date lastSeen = (Date) lastSeenFormattedTextField.getValue();
            this.movie.setLastSeen(lastSeen);
        } catch (IllegalArgumentException e) {
            log.info("Unknown date : " + lastSeenFormattedTextField.getText());
            log.info("Accepted date format  is yyyy-MM-dd or dd/MM/yyyy");
            e.printStackTrace();
        }
        this.movie = movieDaoServices.checkActorsAndSaveMovie(this.movie);
        /*for (VideoFile file : this.movie.getFiles()) {
            movieDaoServices.addFileToMovie(this.movie, file);
        }*/
        log.info("Updated movie in DB: " + movie.getTitle());
    }

    private void enterInfoUrlButtonActionPerformed(ActionEvent evt) {
        //UserInputMovie userInputMovie = (UserInputMovie) applicationContext.getBean("userInputMovie",this.movieHolder);

        int selectedRow = filesTable.convertRowIndexToModel(filesTable.getSelectedRow());
        // get the selected file with the -1 trick
        VideoFile file = (VideoFile) filesTable.getModel().getValueAt(selectedRow, -1);
        Path filePath = null;

        if (null == file) {
            // no selected files, take first
            //filePath = this.movie.getFiles().iterator().next().toPath();
            if (this.fileDataModel.getRowCount() > 0) {
                VideoFile vf = fileDataModel.getFile(0);
                if (null != vf) {
                    filePath = vf.toPath();
                    userInputMovie.setFile(filePath);
                }
            }
        } else {
            userInputMovie.setFile(file.toPath());
        }

        if (null != filePath) {
            userInputMovie.setText("Enter TMDB url found for file: " + filePath.getFileName().toString());
        } else {
            userInputMovie.setText("Enter TMDB url for new movie: ");
        }
        userInputMovie.setMovieHolder(this); // REMOVE MovieHolder
        userInputMovie.setVisible(true);
    }

    @Override
    public void setData(Movie data) {
        this.movie = data;
        if (null == data) {
            titleTextField.setText("");
            alternateTitleTextField.setText("");
            directorTextField.setText("");
            summaryTextArea.setText("");
            commentTextArea.setText("");
            yearTextField.setText("");
            durationTextField.setText("");
            ratingSpinner.setValue(0);
            lastSeenFormattedTextField.setText("");
            actorsList.setListData(new String[]{});
            genresList.setListData(new String[]{});
            if (null != fileDataModel.getFiles()) {
                fileDataModel.getFiles().clear();
            }
            fakePoster();

            return;
        }
        titleTextField.setText(data.getTitle());
        alternateTitleTextField.setText(data.getAlternateTitle());
        directorTextField.setText(data.getDirector());
        if (data.getCreated() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(myMovieDBPreferences.getDateFormat());
            createdTextField.setText(sdf.format(data.getCreated()));
        } else {
            createdTextField.setText("n/a");
        }
        commentTextArea.setText(data.getComments());
        summaryTextArea.setText(data.getSummary());
        yearTextField.setText(data.getYear());
        durationTextField.setText(data.getDuree());

        //poster
        try {
            //ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(data.getPosterBytes()));
            //Iterator<ImageReader> readers= ImageIO.getImageReaders(iis);
            if (null != data.getPosterBytes()) {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(data.getPosterBytes()));
                posterDisplay.setIcon(new ImageIcon(image));
                //int newW = myMovieDBPreferences.getPosterWidth() / 2;
                //int newH = myMovieDBPreferences.getPosterHeight() / 2;
                /* either this */
                //posterDisplay.setIcon(new ImageIcon(image.getScaledInstance(newW, newH, Image.SCALE_FAST)));

                /* or this:
                Image resized = image.getScaledInstance(newW,newH, Image.SCALE_SMOOTH);
                BufferedImage resizedImage = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = resizedImage.createGraphics();
                g2d.drawImage(resized, 0, 0, null);
                g2d.dispose();
                posterDisplay.setIcon(new ImageIcon(resizedImage));*/
            } else {
                BufferedImage image = new BufferedImage(myMovieDBPreferences.getPosterWidth(),
                        myMovieDBPreferences.getPosterHeight(), BufferedImage.TYPE_3BYTE_BGR);
                Graphics graphics = image.getGraphics();
                graphics.drawString(movie.getTitle(), 10, 40);
                if (movie.getAlternateTitle() != null) {
                    graphics.drawString(" (" + movie.getAlternateTitle() + ")",
                            10, 60);
                }
                graphics.drawString(" by " + movie.getDirector(),
                        10, 80);
                posterDisplay.setIcon(new ImageIcon(image));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }

        // hack: put zero as default value
        if (null == data.getRating()) {
            data.setRating(0);
        }
        SpinnerNumberModel ratingModel = new SpinnerNumberModel((int) data.getRating(), 0, 5, 1);
        ratingSpinner.setModel(ratingModel);
        // hack for null last seen dates
        lastSeenFormattedTextField.setValue(movie.getLastSeen());


        // actors
        updateActors();

        // genres
        updateGenres();

        // files
        updateFiles();
    }

    @Override
    public Movie getMovie() {
        return movie;
    }

    private void updateFiles() {
        List<VideoFile> videoFiles = null;
        try {
            videoFiles = hibernateMovieDao.findVideoFilesForMovie(this.movie);
        } catch (Exception e) {
            // transform Set<> to List<>
            videoFiles = new ArrayList<>();
            videoFiles.addAll(this.movie.getFiles());
        }
        if (null != fileDataModel.getFiles()) {
            fileDataModel.getFiles().clear();
        }
        fileDataModel.setFiles(videoFiles);
        filesTable.setModel(fileDataModel);
        fileDataModel.populateEditors(filesTable);
        // @todo delete a selection of files
        for (Integer colIndex = 0; colIndex < fileDataModel.getColumnCount(); colIndex++) {
            TableColumn column = filesTable.getColumnModel().getColumn(colIndex);
            column.setPreferredWidth(fileDataModel.getPreferredWidth(colIndex));
        }
        filesTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        final JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Remove file");
        deleteItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = filesTable.convertRowIndexToModel(filesTable.getSelectedRow());
                // get the file with the -1 trick
                VideoFile file = (VideoFile) filesTable.getModel().getValueAt(selectedRow, -1);

                Object[] options = {"Remove file from database (do not delete file)",
                        "Remove file from database and delete the file!",
                        "Cancel"};
                int reply = JOptionPane.showOptionDialog(MovieDescriptionPanel.this,
                        "Are ou sure to delete file " + file.getFileName() + "?",
                        "Delete a file",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[2]
                );
                if (reply == JOptionPane.YES_OPTION) {
                    log.info("Deleting file " + file.getFileName() + " from DB only");
                    hibernateVideoFileDao.deleteFile(file);
                    Movie reloaded = hibernateMovieDao.findOne(MovieDescriptionPanel.this.movie.getId());
                    setData(movie);
                } else if (reply == JOptionPane.NO_OPTION) {
                    hibernateVideoFileDao.deleteFile(file);
                    log.info("Really deleting " + file.getFileName() + " from directory " + file.getDirectory());
                    try {
                        Files.deleteIfExists(Paths.get(file.getDirectory(), file.getFileName()));
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(MovieDescriptionPanel.this,
                                "Could not delete file " + file.getFileName() + "\nSee log for details",
                                "Warning",
                                JOptionPane.WARNING_MESSAGE);
                        e1.printStackTrace();
                    }
                    Movie reloaded = hibernateMovieDao.findOne(MovieDescriptionPanel.this.movie.getId());
                    setData(movie);
                }
            }
        });
        popupMenu.add(deleteItem);
        // open file location
        JMenuItem openFileLocation = new JMenuItem("Open file location");
        openFileLocation.addActionListener(e -> {
            int selectedRow = filesTable.convertRowIndexToModel(filesTable.getSelectedRow());
            // get the file with the -1 trick
            VideoFile file = (VideoFile) filesTable.getModel().getValueAt(selectedRow, -1);
            if (!Desktop.isDesktopSupported()) {
                JOptionPane.showMessageDialog(this,
                        "Cannot access desktop!",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            Desktop dt = Desktop.getDesktop();
            File fd = null;
            try {
                fd = new File(file.getDirectory());
                dt.open(fd);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Cannot open directory " + file.getDirectory(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        popupMenu.add(openFileLocation);
        // automatically select row when right click
        popupMenu.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int rowAtPoint = filesTable.rowAtPoint(SwingUtilities.convertPoint(popupMenu, new Point(0, 0), filesTable));
                        if (rowAtPoint > -1) {
                            filesTable.setRowSelectionInterval(rowAtPoint, rowAtPoint);
                        }
                    }
                });
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        filesTable.setComponentPopupMenu(popupMenu);
    }

    private void updateGenres() {
        try {
            List<Genre> genres = hibernateMovieDao.findGenresForMovie(this.movie);
            // seems we have to convert to String[] ourselves:
            String[] genresStringArray = new String[genres.size()];
            Integer idx = 0;
            for (Genre genre : genres) {
                genresStringArray[idx] = genre.getName();
                idx++;
            }
            genresList.setListData(genresStringArray);
        } catch (Exception e) {
            Set<Genre> genres = this.movie.getGenres();
            // seems we have to convert to String[] ourselves:
            String[] genresStringArray = new String[genres.size()];
            Integer idx = 0;
            for (Genre genre : genres) {
                genresStringArray[idx] = genre.getName();
                idx++;
            }
            genresList.setListData(genresStringArray);
        }
    }

    private void updateActors() {
        try {
            List<Actor> actors = hibernateMovieDao.findActorsForMovie(this.movie);
            // seems we have to convert to String[] ourselves:
            String[] actorsStringArray = new String[actors.size()];
            Integer idx = 0;
            for (Actor actor : actors) {
                actorsStringArray[idx] = actor.getName();
                idx++;
            }
            actorsList.setListData(actorsStringArray);
        } catch (Exception e) {
            Set<Actor> actors = this.movie.getActors();
            String[] actorsStringArray = new String[actors.size()];
            Integer idx = 0;
            for (Actor actor : actors) {
                actorsStringArray[idx] = actor.getName();
                idx++;
            }
            actorsList.setListData(actorsStringArray);
        }

    }

    private void fakePoster() {
        // fake poster
        BufferedImage image = new BufferedImage(myMovieDBPreferences.getPosterWidth(),
                myMovieDBPreferences.getPosterHeight(), BufferedImage.TYPE_3BYTE_BGR);
        posterDisplay.setIcon(new ImageIcon(image));
    }

    private void playFileAtRow(int row) {
        // get the file with the -1 trick
        VideoFile file = (VideoFile) filesTable.getModel().getValueAt(row, -1);
        movieGuiService.playTheFile(Paths.get(file.getDirectory(), file.getFileName()));
    }

    private void addGenreButtonActionPerformed(ActionEvent e) {
        applicationContext.getBean(GenreEditorPanel.class);
    }

    private void addActorButtonActionPerformed(ActionEvent e) {

    }

    public void getData(Movie movie) {
        movie.setTitle(titleTextField.getText());
        movie.setAlternateTitle(alternateTitleTextField.getText());
        movie.setDirector(directorTextField.getText());
        movie.setComments(commentTextArea.getText());
        movie.setSummary(summaryTextArea.getText());
        movie.setYear(yearTextField.getText());
        movie.setDuree(durationTextField.getText());

        movie.setRating((Integer) ratingSpinner.getValue());
        movie.setLastSeen((Date) lastSeenFormattedTextField.getValue());

    }

    public boolean isModified(Movie movie) {
        if (titleTextField.getText() != null ? !titleTextField.getText().equals(movie.getTitle()) : movie.getTitle() != null)
            return true;
        if (alternateTitleTextField.getText() != null ? !alternateTitleTextField.getText().equals(movie.getAlternateTitle()) : movie.getAlternateTitle() != null)
            return true;
        if (directorTextField.getText() != null ? !directorTextField.getText().equals(movie.getDirector()) : movie.getDirector() != null)
            return true;
        if (commentTextArea.getText() != null ? !commentTextArea.getText().equals(movie.getComments()) : movie.getComments() != null)
            return true;
        if (summaryTextArea.getText() != null ? !summaryTextArea.getText().equals(movie.getSummary()) : movie.getSummary() != null)
            return true;
        if (yearTextField.getText() != null ? !yearTextField.getText().equals(movie.getYear()) : movie.getYear() != null)
            return true;
        if (durationTextField.getText() != null ? !durationTextField.getText().equals(movie.getDuree()) : movie.getDuree() != null)
            return true;
        return false;
    }

    private void createUIComponents() {

        /*playMovieButton = new JButton("Play movie");
        openInfoButton = new JButton("Open internet info page");
        internetCriticsButton = new JButton("Internet critics");
        saveButton = new JButton("Save");
        enterInfoUrlButton = new JButton("Enter URL");
        addActorButton = new JButton("Add");
        addGenreButton = new JButton("Add");*/

        playMovieButton.addActionListener(e -> playMovieButtonActionPerformed(e));
        openInfoButton.addActionListener(e -> openInfoUrlButtonActionPerformed(e));
        internetCriticsButton.addActionListener(e -> internetCriticsButtonActionPerformed(e));
        saveButton.addActionListener(e -> saveButtonActionPerformed(e));
        enterInfoUrlButton.addActionListener(e -> enterInfoUrlButtonActionPerformed(e));
        addActorButton.addActionListener(e -> addActorButtonActionPerformed(e));
        addGenreButton.addActionListener(e -> addGenreButtonActionPerformed(e));

        filesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                JTable table = (JTable) evt.getSource();
                Point p = evt.getPoint();
                int row = table.rowAtPoint(p);
                if (evt.getClickCount() == 2) {
                    playFileAtRow(row);
                } else {
                    super.mouseClicked(evt);
                }
            }
        });

    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(6, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel.setMaximumSize(new Dimension(1920, 1200));
        panel.setMinimumSize(new Dimension(300, 500));
        panel.setOpaque(true);
        panel.setPreferredSize(new Dimension(800, 800));
        panel.setRequestFocusEnabled(true);
        panel.setVerifyInputWhenFocusTarget(false);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, -1, -1, panel.getFont())));
        actorsGenrePanel = new JPanel();
        actorsGenrePanel.setLayout(new GridLayoutManager(1, 6, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(actorsGenrePanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 100), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Actors");
        actorsGenrePanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Genres");
        actorsGenrePanel.add(label2, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        actorsGenrePanel.add(scrollPane1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(140, -1), new Dimension(140, -1), new Dimension(300, -1), 2, false));
        actorsList = new JList();
        scrollPane1.setViewportView(actorsList);
        final JScrollPane scrollPane2 = new JScrollPane();
        actorsGenrePanel.add(scrollPane2, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(140, -1), new Dimension(140, -1), new Dimension(300, -1), 0, false));
        genresList = new JList();
        scrollPane2.setViewportView(genresList);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        actorsGenrePanel.add(panel1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(-1, 40), null, 0, false));
        addActorButton = new JButton();
        addActorButton.setText("Add");
        panel1.add(addActorButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        actorsGenrePanel.add(panel2, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(-1, 40), null, 0, false));
        addGenreButton = new JButton();
        addGenreButton.setText("Add");
        panel2.add(addGenreButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        commentsPanel = new JPanel();
        commentsPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(commentsPanel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 80), null, 0, false));
        commentTextArea = new JTextArea();
        commentsPanel.add(commentTextArea, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 90), new Dimension(-1, 90), 0, false));
        commentLabel = new JLabel();
        commentLabel.setText("Comment");
        commentsPanel.add(commentLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panel.add(separator1, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayoutManager(1, 7, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(buttonsPanel, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        playMovieButton = new JButton();
        playMovieButton.setText("PlayMovie");
        buttonsPanel.add(playMovieButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        openInfoButton = new JButton();
        openInfoButton.setText("Internet Info Page");
        buttonsPanel.add(openInfoButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        buttonsPanel.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Save");
        buttonsPanel.add(saveButton, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        internetCriticsButton = new JButton();
        internetCriticsButton.setText("Internet critics");
        buttonsPanel.add(internetCriticsButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        enterInfoUrlButton = new JButton();
        enterInfoUrlButton.setText("Enter url");
        buttonsPanel.add(enterInfoUrlButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteButton = new JButton();
        deleteButton.setText("Delete");
        buttonsPanel.add(deleteButton, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(20, 0));
        panel.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        posterPanel = new JPanel();
        posterPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        posterPanel.setMaximumSize(new Dimension(-1, -1));
        panel3.add(posterPanel, BorderLayout.WEST);
        posterDisplay = new JLabel();
        posterDisplay.setText("");
        posterPanel.add(posterDisplay, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, BorderLayout.CENTER);
        titleFilesPanel = new JPanel();
        titleFilesPanel.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(titleFilesPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Title");
        titleFilesPanel.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Alternate Title");
        titleFilesPanel.add(label4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Director");
        titleFilesPanel.add(label5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        titleTextField = new JTextField();
        titleFilesPanel.add(titleTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        alternateTitleTextField = new JTextField();
        titleFilesPanel.add(alternateTitleTextField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        directorTextField = new JTextField();
        titleFilesPanel.add(directorTextField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ratingDurationPanel = new JPanel();
        ratingDurationPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(ratingDurationPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        ratingDurationPanel.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Year");
        panel5.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Rating");
        panel5.add(label7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ratingSpinner = new JSpinner();
        panel5.add(ratingSpinner, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        yearTextField = new JTextField();
        panel5.add(yearTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        ratingDurationPanel.add(panel6, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Duration");
        panel6.add(label8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Last seen");
        panel6.add(label9, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        durationTextField = new JTextField();
        panel6.add(durationTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lastSeenFormattedTextField = new JFormattedTextField();
        panel6.add(lastSeenFormattedTextField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        createdLabel = new JLabel();
        createdLabel.setText("Created");
        panel6.add(createdLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        createdTextField = new JTextField();
        panel6.add(createdTextField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        seenNowButton = new JButton();
        seenNowButton.setText("Now");
        panel6.add(seenNowButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        summaryFilePanel = new JPanel();
        summaryFilePanel.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(summaryFilePanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Summary");
        summaryFilePanel.add(label10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 80), null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Files");
        summaryFilePanel.add(label11, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 50), null, 0, false));
        filesTableScrollPane = new JScrollPane();
        summaryFilePanel.add(filesTableScrollPane, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        filesTable = new JTable();
        filesTableScrollPane.setViewportView(filesTable);
        summaryScrollPane = new JScrollPane();
        summaryFilePanel.add(summaryScrollPane, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        summaryTextArea = new JTextPane();
        summaryScrollPane.setViewportView(summaryTextArea);
        label3.setLabelFor(titleTextField);
        label4.setLabelFor(alternateTitleTextField);
        label5.setLabelFor(directorTextField);
        label6.setLabelFor(yearTextField);
        label7.setLabelFor(ratingSpinner);
        label8.setLabelFor(durationTextField);
        label9.setLabelFor(lastSeenFormattedTextField);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
