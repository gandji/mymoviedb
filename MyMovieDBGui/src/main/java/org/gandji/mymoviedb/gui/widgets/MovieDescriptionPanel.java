package org.gandji.mymoviedb.gui.widgets;

import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.MyMovieDBPreferences;
import org.gandji.mymoviedb.gui.FileDataModel;
import org.gandji.mymoviedb.gui.MovieGuiService;
import org.gandji.mymoviedb.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableColumn;
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
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by gandji on 05/04/2017.
 */
@Component
@Scope("prototype")
@Slf4j
public class MovieDescriptionPanel extends JPanel {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private HibernateMovieDao hibernateMovieDao;

    @Autowired
    private HibernateVideoFileDao hibernateVideoFileDao;

    @Autowired
    private MovieGuiService movieGuiService;

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

    private MovieHolder movieHolder;

    public void setMovieHolder(MovieHolder movieHolder) {
        this.movieHolder = movieHolder;
    }

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
        createUIComponents();
    }

    @PostConstruct
    public void init() {
        //createUIComponents();
    }

    @PostConstruct
    public void postConstruct() {

        fakePoster();
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options = {"Remove movie and files from database (do not delete files)",
                        "Remove movie and files from database and delete the files!",
                        "Cancel"};
                int reply = JOptionPane.showOptionDialog(MovieDescriptionPanel.this,
                        "Are ou sure to delete movie "+movie.getTitle()+"?",
                        "Delete a movie",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[2]
                );
                boolean deleteInDB = false;
                boolean deleteRealFiles = false;
                if (reply == JOptionPane.YES_OPTION) {
                    deleteInDB = true;
                    deleteRealFiles = false;
                    log.info("Deleting movie from database: "+movie.getTitle());
                } else if (reply == JOptionPane.NO_OPTION){
                    log.info("Deleting movie and files for "+movie.getTitle());
                    deleteInDB = true;
                    deleteRealFiles = true;
                }
                if (deleteInDB) {
                    List<VideoFile> vfs = hibernateMovieDao.findVideoFilesForMovie(movie);
                    for (VideoFile vf : vfs) {
                        log.debug("   removing file from DB: " + vf.getFileName());
                        hibernateVideoFileDao.deleteFile(vf);
                        if (deleteRealFiles) {
                            try {
                                log.debug("   deleting file " + vf.getFileName());
                                Files.deleteIfExists(Paths.get(vf.getDirectory(),vf.getFileName()));
                            } catch (IOException e1) {
                                JOptionPane.showMessageDialog(MovieDescriptionPanel.this,
                                        "Could not delete file "+vf.getFileName()+"\nSee log for details",
                                        "Warning",
                                        JOptionPane.WARNING_MESSAGE);
                                e1.printStackTrace();
                            }
                        }
                    }
                    hibernateMovieDao.deleteMovie(movie);
                    setData(null);
                }
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
        this.movie.setTitle(titleTextField.getText());
        this.movie.setAlternateTitle(alternateTitleTextField.getText());
        this.movie.setDirector(directorTextField.getText());
        this.movie.setComments(commentTextArea.getText());
        this.movie.setYear(yearTextField.getText());
        this.movie.setDuree(durationTextField.getText());
        this.movie.setRating((Integer) ratingSpinner.getValue());
        // @todo manage date of last seen: editor? formatter?
        // hack for unset last seen date!
        try {
            this.movie.setLastSeen(lastSeenFormattedTextField.getText().equals("unset") ? null : Date.valueOf(lastSeenFormattedTextField.getText()));
        } catch (IllegalArgumentException e) {
            log.info("Unknown date : "+lastSeenFormattedTextField.getText());
            log.info("Accepted date format is yyy-mm-dd");
            e.printStackTrace();
        }
        this.movie = hibernateMovieDao.save(this.movie);
        log.info("Updated movie in DB: " + movie.getTitle());
    }

    private void imbdUrlButtonActionPerformed(ActionEvent evt) {
        //UserInputMovie userInputMovie = (UserInputMovie) applicationContext.getBean("userInputMovie",this.movieHolder);

        int selectedRow = filesTable.convertRowIndexToModel(filesTable.getSelectedRow());
        // get the selected file with the -1 trick
        VideoFile file = (VideoFile) filesTable.getModel().getValueAt(selectedRow,-1);
        Path filePath = null;

        if (null==file) {
            // no selected files, take first
            //filePath = this.movie.getFiles().iterator().next().toPath();
            if (this.fileDataModel.getRowCount() >0) {
                VideoFile vf = fileDataModel.getFile(0);
                if (null!=vf) {
                    filePath = vf.toPath();
                    userInputMovie.setFile(filePath);
                }
            }
        } else {
            userInputMovie.setFile(file.toPath());
        }

        if (null != filePath) {
            userInputMovie.setText("Enter IMDB url found for file: "+filePath.getFileName().toString());
        } else {
            userInputMovie.setText("Enter IMDB url for new movie: ");
        }
        userInputMovie.setMovieHolder(movieHolder);
        userInputMovie.setVisible(true);
    }

    public void setData(Movie data) {
        this.movie = data;
        if (null==data){
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
        lastSeenFormattedTextField.setText(data.getLastSeen() == null ? "unset" : data.getLastSeen().toString());


        // actors
        updateActors();

        // genres
        updateGenres();

        // files
        updateFiles();
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
                VideoFile file = (VideoFile) filesTable.getModel().getValueAt(selectedRow,-1);

                Object[] options = {"Remove file from database (do not delete file)",
                        "Remove file from database and delete the file!",
                        "Cancel"};
                int reply = JOptionPane.showOptionDialog(MovieDescriptionPanel.this,
                        "Are ou sure to delete file "+file.getFileName()+"?",
                        "Delete a file",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[2]
                );
                if (reply == JOptionPane.YES_OPTION) {
                    log.info("Deleting file "+file.getFileName()+" from DB only");
                    hibernateVideoFileDao.deleteFile(file);
                    Movie reloaded = hibernateMovieDao.findOne(MovieDescriptionPanel.this.movie.getId());
                    setData(movie);
                } else if (reply == JOptionPane.NO_OPTION){
                    hibernateVideoFileDao.deleteFile(file);
                    log.info("Really deleting "+file.getFileName()+" from directory "+file.getDirectory());
                    try {
                        Files.deleteIfExists(Paths.get(file.getDirectory(),file.getFileName()));
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(MovieDescriptionPanel.this,
                                "Could not delete file "+file.getFileName()+"\nSee log for details",
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
            VideoFile file = (VideoFile) filesTable.getModel().getValueAt(selectedRow,-1);
            if (!Desktop.isDesktopSupported()){
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
                        "Cannot open directory " + file.getDirectory() ,
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
                myMovieDBPreferences.getPosterHeight(),BufferedImage.TYPE_3BYTE_BGR);
        posterDisplay.setIcon(new ImageIcon(image));
    }

    private void playFileAtRow(int row) {
        // get the file with the -1 trick
        VideoFile file = (VideoFile) filesTable.getModel().getValueAt(row,-1);
       movieGuiService.playTheFile(Paths.get(file.getDirectory(),file.getFileName()));
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
        movie.setLastSeen(lastSeenFormattedTextField.getText().equals("unset") ? null : Date.valueOf(lastSeenFormattedTextField.getText()));

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
        enterInfoUrlButton.addActionListener(e -> imbdUrlButtonActionPerformed(e));
        addActorButton.addActionListener(e -> addActorButtonActionPerformed(e));
        addGenreButton.addActionListener(e -> addGenreButtonActionPerformed(e));

        filesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                JTable table =(JTable) evt.getSource();
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

}
