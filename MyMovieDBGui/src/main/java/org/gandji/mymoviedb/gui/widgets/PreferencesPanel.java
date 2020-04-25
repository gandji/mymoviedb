package org.gandji.mymoviedb.gui.widgets;

import org.gandji.mymoviedb.MyMovieDBPreferences;
import org.gandji.mymoviedb.scrapy.MovieInfoSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by gandji on 27/10/2017.
 */
@Component
public class PreferencesPanel extends JPanel {

    private JTextField databaseUrlField;
    private JLabel databaseUrlLabel;
    private JLabel databaseUserLabel;
    private JTextField databaseUserField;
    private JLabel databasePasswordLabel;
    private JPasswordField databasePasswordField;
    private JButton OKButton;
    private JButton cancelButton;
    private JToolBar toolbar;
    private JPanel internalPanel;
    private JCheckBox keepFileOnUpdateCheckBox;
    private JCheckBox fullFeaturedCheckBox;
    private JCheckBox hackersHackCheckBox;

    private JLabel fontSizeLabel;
    private JSpinner fontSizeWidget;

    private JLabel dbPageSizeLabel;
    private JSpinner dbPageSizeWidget;

    private JLabel rightColumnWidthLabel;
    private JSpinner rightColumnWidthWidget;

    /*private JLabel mainHeightLabel;
    private JSpinner mainHeightWidget;

    private JLabel mainWidthLabel;
    private JSpinner mainWidthWidget;*/

    private JLabel dateFormatLabel;
    private JTextField dateFormatField;

    private JComboBox<MovieInfoSearchService.UrlType> internetTargetCombo;

    private JLabel guiModeLabel;
    private JComboBox<MyMovieDBPreferences.GuiMode> guiModeCombo;

    @Autowired
    MyMovieDBPreferences myMovieDBPreferences;

    private JFrame controllingWidget;

    /*public PreferencesPanel() {
    }*/

    @PostConstruct
    private void postInit() {

        createUIComponents();

        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTo(myMovieDBPreferences);
                myMovieDBPreferences.flushPrefs();
                if (null!=controllingWidget) {controllingWidget.setVisible(false);}
            }
        });
        /*saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTo(myMovieDBPreferences);
                myMovieDBPreferences.flushPrefs();
            }
        });*/
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myMovieDBPreferences.resetPrefs();
                loadFrom(myMovieDBPreferences);
                if (null!=controllingWidget) {controllingWidget.setVisible(false);}
            }
        });
        loadFrom(myMovieDBPreferences);

    }

    public JPanel getInternalPanel() {
        return internalPanel;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Preferences");
        frame.setContentPane(new PreferencesPanel().getInternalPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void setControllingWidget(JFrame controllingWidget) {
        this.controllingWidget = controllingWidget;
    }

    public JFrame getControllingWidget() {
        return controllingWidget;
    }

    public void loadFrom(MyMovieDBPreferences data) {
        /*
        databaseUrlField.setText(data.getDataSourceUrl());
        databaseUserField.setText(data.getDataSourceUser());
        databasePasswordField.setText(data.getDataSourcePassword());
        */
        guiModeCombo.setSelectedItem(data.getGuiMode());
        keepFileOnUpdateCheckBox.setSelected(data.isKeepDuplicateFilesOnScan());
        fullFeaturedCheckBox.setSelected(data.isFullFeatured());
        hackersHackCheckBox.setSelected(data.isHackersHack());
        dateFormatField.setText(data.getDateFormat());
        fontSizeWidget.setValue(data.getFontSize());
        dbPageSizeWidget.setValue(data.getDbPageSize());
        rightColumnWidthWidget.setValue(data.getRightColumnWidth());
        /*mainHeightWidget.setValue(data.getMainHeight());
        mainWidthWidget.setValue(data.getMainWidth());*/
        internetTargetCombo.setSelectedItem(data.getInternetTarget());
    }

    public void saveTo(MyMovieDBPreferences data) {
        /*data.setDataSourceUrl(databaseUrlField.getText());
        data.setDataSourceUser(databaseUserField.getText());
        data.setDataSourcePassword(databasePasswordField.getText());
        */
        data.setGuiMode((MyMovieDBPreferences.GuiMode)guiModeCombo.getSelectedItem());
        data.setKeepDuplicateFilesOnScan(keepFileOnUpdateCheckBox.isSelected());
        data.setFullFeatured(fullFeaturedCheckBox.isSelected());
        data.setHackersHack(hackersHackCheckBox.isSelected());
        data.setDateFormat(dateFormatField.getText());
        data.setFontSize((int)fontSizeWidget.getValue());
        data.setDbPageSize((int)dbPageSizeWidget.getValue());
        data.setRightColumnWidth((int)rightColumnWidthWidget.getValue());
        /*data.setMainHeight((int)mainHeightWidget.getValue());
        data.setMainWidth((int)mainWidthWidget.getValue());*/
        data.setInternetTarget((MovieInfoSearchService.UrlType)internetTargetCombo.getSelectedItem());
    }

    public boolean isModified(MyMovieDBPreferences data) {
        /*if (databaseUrlField.getText() != null ? !databaseUrlField.getText().equals(data.getDataSourceUrl()) : data.getDataSourceUrl() != null)
            return true;
        if (databaseUserField.getText() != null ? !databaseUserField.getText().equals(data.getDataSourceUser()) : data.getDataSourceUser() != null)
            return true;
        if (databasePasswordField.getText() != null ? !databasePasswordField.getText().equals(data.getDataSourcePassword()) : data.getDataSourcePassword() != null)
            return true;
            */
        if (guiModeCombo.getSelectedItem() != data.getGuiMode()) return true;
        if (keepFileOnUpdateCheckBox.isSelected() != data.isKeepDuplicateFilesOnScan()) return true;
        if (fullFeaturedCheckBox.isSelected() != data.isFullFeatured()) return true;
        if (hackersHackCheckBox.isSelected() != data.isHackersHack()) return true;
        if (!dateFormatField.getText().equals(data.getDateFormat())) return true;
        if (!fontSizeWidget.getValue().equals(data.getFontSize())) return true;
        if (!dbPageSizeWidget.getValue().equals(data.getDbPageSize())) return true;
        if (!rightColumnWidthWidget.getValue().equals(data.getRightColumnWidth())) return true;
        /*if (!mainHeightWidget.getValue().equals(data.getMainHeight())) return true;
        if (!mainWidthWidget.getValue().equals(data.getMainWidth())) return true;*/
        return false;
    }

    public void createUIComponents() {
        GridLayout layout = new GridLayout(0,2);
        layout.setHgap(100);

        databaseUrlLabel = new JLabel("Database URL");
        databaseUrlField = new JTextField();

        databaseUserLabel = new JLabel("User");
        databaseUserField = new JTextField();

        databasePasswordLabel = new JLabel("password");
        databasePasswordField = new JPasswordField();

        keepFileOnUpdateCheckBox = new JCheckBox("Keep files on update");

        fullFeaturedCheckBox = new JCheckBox("Experimantal features");

        hackersHackCheckBox = new JCheckBox("Use special queries");

        dateFormatLabel = new JLabel("Date format");
        dateFormatField = new JTextField();

        fontSizeLabel = new JLabel("Font size");
        fontSizeWidget = new JSpinner(new SpinnerNumberModel());

        dbPageSizeLabel = new JLabel("Number of movies displayed");
        dbPageSizeWidget = new JSpinner(new SpinnerNumberModel());

        rightColumnWidthLabel = new JLabel("Right column width");
        rightColumnWidthWidget = new JSpinner(new SpinnerNumberModel());

        /*mainHeightLabel = new JLabel("Initial window height");
        mainHeightWidget = new JSpinner(new SpinnerNumberModel());

        mainWidthLabel = new JLabel("Initial window width");
        mainWidthWidget = new JSpinner(new SpinnerNumberModel());*/

        guiModeLabel = new JLabel("Gui Mode (restart needed)");
        guiModeCombo = new JComboBox<>(MyMovieDBPreferences.GuiMode.values());
        guiModeCombo.setRenderer(new GuiModeRenderer());

        OKButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        FlowLayout buttonsLayout = new FlowLayout(FlowLayout.TRAILING);
        JPanel buttons = new JPanel(buttonsLayout);
        buttons.add(OKButton);
        buttons.add(cancelButton);

        internalPanel = new JPanel(layout);
        /*internalPanel.add(databaseUrlLabel);
        internalPanel.add(databaseUrlField);
        internalPanel.add(databaseUserLabel);
        internalPanel.add(databaseUserField);
        internalPanel.add(databasePasswordLabel);
        internalPanel.add(databasePasswordField);
        */

        internalPanel.add(fontSizeLabel);
        internalPanel.add(fontSizeWidget);

        internalPanel.add(dbPageSizeLabel);
        internalPanel.add(dbPageSizeWidget);

        /*internalPanel.add(mainHeightLabel);
        internalPanel.add(mainHeightWidget);

        internalPanel.add(mainWidthLabel);
        internalPanel.add(mainWidthWidget);*/

        internalPanel.add(rightColumnWidthLabel);
        internalPanel.add(rightColumnWidthWidget);

        internalPanel.add(dateFormatLabel);
        internalPanel.add(dateFormatField);

        internalPanel.add(new JLabel(""));
        internalPanel.add(keepFileOnUpdateCheckBox);

        internalPanel.add(new JLabel(""));
        internalPanel.add(fullFeaturedCheckBox);

        internalPanel.add(new JLabel(""));
        internalPanel.add(hackersHackCheckBox);

        internalPanel.add(new JLabel("DB for internet info"));
        internetTargetCombo = new JComboBox<>(MovieInfoSearchService.UrlType.values());
        internalPanel.add(internetTargetCombo);

        internalPanel.add(guiModeLabel);
        internalPanel.add(guiModeCombo);

        internalPanel.add(buttons);

        setLayout(layout);
    }

    public class GuiModeRenderer extends DefaultListCellRenderer {
        @Override
        public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setText(((MyMovieDBPreferences.GuiMode) value).getDisplayName());
            return this;
        }
    }
}
