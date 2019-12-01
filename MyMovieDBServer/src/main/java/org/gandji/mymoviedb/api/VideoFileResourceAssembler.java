package org.gandji.mymoviedb.api;

import org.gandji.mymoviedb.data.VideoFile;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

/**
 * Created by gandji on 22/11/2019.
 */
@Component
public class VideoFileResourceAssembler implements ResourceAssembler<VideoFile,VideoFileResource> {
    @Override
    public VideoFileResource toResource(VideoFile videoFile) {
        VideoFileResource videoFileResource = new VideoFileResource(videoFile);
        return videoFileResource;
    }
}
