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
package org.gandji.mymoviedb.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author gandji <gandji@free.fr>
 * 
 */
@Entity
@Table(name="videofile")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class VideoFile {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO,generator = "videofile_seq")
    @SequenceGenerator(name="videofile_seq")
    private Long id;
    
    // unique hashCode for video files
    private String hashCode;

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
    
    private String fileName;
    private String directory;
    private String driveLabel;

    public Long getId() {
        return id;
    }

    public void setDriveLabel(String driveLabel) {
        this.driveLabel = driveLabel;
    }

    public String getDriveLabel() {
        return driveLabel;
    }

    public enum Version {
        VF,VO,VOST, MULTI
    }
    @Enumerated(EnumType.STRING)
    private Version version;
    
    public enum QualiteVideo {
        EXCELLENT,
        GOOD,
        BAD,
        SCREENER;
        
        static public QualiteVideo from(String name) {
            for (QualiteVideo qualiteVideo : values()) {
                if (qualiteVideo.name().equals(name)) {
                    return qualiteVideo;
                }
            }
            return null;
        }
    }
    @Enumerated(EnumType.STRING)
    private QualiteVideo qualiteVideo;

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public QualiteVideo getQualiteVideo() {
        return qualiteVideo;
    }

    public void setQualiteVideo(QualiteVideo qualiteVideo) {
        this.qualiteVideo = qualiteVideo;
    }

    public void setQualiteVideo(String qualiteVideo) {
        this.qualiteVideo = QualiteVideo.from(qualiteVideo);
    }

    public Path toPath() {return Paths.get(directory,fileName);}

    public String computeCurrentDriveLabel() {
        File file = new File(this.getDirectory()+"/"+this.getFileName());
        Iterable<Path> paths = file.toPath().getFileSystem().getRootDirectories();
        for (Path path : paths) {
            if (this.getDirectory().substring(0,3).equals(path.getRoot().toString())) {
                return  FileSystemView.getFileSystemView().getSystemDisplayName(path.toFile());
            }
        }
        return "unknown";

    }
    
    @ManyToOne(cascade={CascadeType.MERGE},fetch=FetchType.LAZY)
    @JoinColumn(name="movie_id", foreignKey = @ForeignKey(name="fkey_constraint_movie"))
    private Movie movie;
}
