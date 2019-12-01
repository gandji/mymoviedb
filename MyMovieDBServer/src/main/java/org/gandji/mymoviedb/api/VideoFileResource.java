package org.gandji.mymoviedb.api;

import org.gandji.mymoviedb.data.VideoFile;
import org.springframework.hateoas.ResourceSupport;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by gandji on 22/11/2019.
 */
public class VideoFileResource extends ResourceSupport {
    VideoFile videoFile;
    public VideoFileResource() {
        super();
    }
    public VideoFileResource(VideoFile videoFile) {
        super();
        this.videoFile = videoFile;
    }

    public String getVideoFilePath() {
        return Paths.get(videoFile.getDirectory(),videoFile.getFileName()).toAbsolutePath().toString();
    }
}
