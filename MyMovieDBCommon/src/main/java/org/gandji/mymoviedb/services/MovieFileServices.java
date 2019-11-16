package org.gandji.mymoviedb.services;

import org.gandji.mymoviedb.data.VideoFile;
import org.gandji.mymoviedb.filefinder.FileUtils;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * Created by gandji on 21/09/2019.
 */
@Component
public class MovieFileServices {
    public void updateVideoFile(VideoFile file, Path path) {
        file.setFileName(path.getFileName().toString());
        file.setDirectory(path.getParent().toString());
        file.setDriveLabel(file.computeCurrentDriveLabel());
        String hashCode = FileUtils.computeHash(path);
        file.setHashCode(hashCode);
    }

}
