/*
 * Copyright (C) 2017 gandji <gandji@free.fr>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gandji.mymoviedb.gui;

import org.gandji.mymoviedb.data.HibernateVideoFileDao;
import org.gandji.mymoviedb.data.VideoFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author gandji <gandji@free.fr>
 */
@Component
@Scope("prototype")
public class FileDataModel extends AbstractTableModel {

    private static final Logger LOG = Logger.getLogger(FileDataModel.class.getName());

    private List<VideoFile> files = null;

    @Autowired
    private HibernateVideoFileDao hibernateVideoFileDao;

    public FileDataModel() {
        this.files = null;
    }

    public FileDataModel(List<VideoFile> files) {
        this.files = files;
    }
   
    public void setFiles(List<VideoFile> files) {
        this.files = new ArrayList<>();
        this.files.addAll(files);
    }

    public List<VideoFile> getFiles() {
        return files;
    }

    public VideoFile getFile(int index) {
        if (null==files || files.isEmpty()) { return null; }
        return files.get(0);
    }

    public void addVideoFile(VideoFile file) {
        if (null==this.files) {
            this.files = new ArrayList<>();
        }
        this.files.add(file);
    }

    public void populateEditors(JTable table) {
        // version
        JComboBox versionCombo = new JComboBox();
        for (VideoFile.Version version : VideoFile.Version.values()) {
            versionCombo.addItem(version);
        }
        table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(versionCombo));
        // qualite video
        JComboBox qualityCombo = new JComboBox();
        for (VideoFile.QualiteVideo qualiteVideo : VideoFile.QualiteVideo.values()) {
            qualityCombo.addItem(qualiteVideo);
        }
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(qualityCombo));


    }

    public enum Role {
        FILENAME, VERSION, QUALITE_VIDEO, DRIVE_LABEL,
    };

    private class ColumnDescription {

        Role role;
        String name;
        Integer preferredWidth;
        boolean editable;
        String accessorName;
        Class valueType;

        private ColumnDescription(Role role, String name, Integer preferredWidth) {
            this.role = role;
            this.name = name;
            this.preferredWidth = preferredWidth;
            this.editable = false;
        }

        private ColumnDescription(Role role, String name, Integer preferredWidth, boolean editable, String accessorName, Class valueType) {
            this.role = role;
            this.name = name;
            this.preferredWidth = preferredWidth;
            this.editable = editable;
            this.accessorName = accessorName;
            this.valueType = valueType;
        }
        
        public Object getValueFor(VideoFile file){
            switch (this.role){
                case FILENAME: return file.getFileName();
                case VERSION: return file.getVersion();
                case QUALITE_VIDEO: return file.getQualiteVideo();
                case DRIVE_LABEL: return file.getDriveLabel();
                default: return null;
            }
        }

        public boolean isEditable() {
            return editable;
        }
    }

    final private ColumnDescription[] columns = {
        new ColumnDescription(Role.FILENAME, "File name",120, false, null, null),
        new ColumnDescription(Role.VERSION, "Version",35, true, "setVersion", VideoFile.Version.class),
        new ColumnDescription(Role.QUALITE_VIDEO, "Qualite",80, true, "setQualiteVideo", VideoFile.QualiteVideo.class),
        new ColumnDescription(Role.DRIVE_LABEL, "Disque",80, false, "setDriveLabel", String.class),
    };

    public Integer getPreferredWidth(Integer colIndex) {
        return columns[colIndex].preferredWidth;
    }
    @Override
    public int getRowCount() {
        if (null!= files){
            return files.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    // trick: i use -1 to access the movie object!
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex<0) { return null;}// means no selection
        if (null != files) {
            if (columnIndex == -1) {
                return files.get(rowIndex);
            } else {
                return columns[columnIndex].getValueFor(files.get(rowIndex));
            }
        } else {
            return null;
        }
    }
    
    @Override
    public String getColumnName(int column) {
        return columns[column].name;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if (row < 0) { return false;} // useless?
        if (col == -1) { return false;} // useless?
        return columns[col].isEditable();
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        VideoFile vf = files.get(row);

        if (col==1) {
            vf.setVersion((VideoFile.Version) value);
        }
        if (col==2) {
            vf.setQualiteVideo((VideoFile.QualiteVideo)value);
        }
        if (col==3) {

        }
        if (col==4) {

        }
        files.set(row, hibernateVideoFileDao.save(vf));

    }

}
    
