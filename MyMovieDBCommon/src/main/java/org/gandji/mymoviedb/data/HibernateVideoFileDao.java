/*
 * Copyright (C) 2017 gandji <gandji@free.fr>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.gandji.mymoviedb.data;

import org.gandji.mymoviedb.data.repositories.VideoFileRepository;
import org.gandji.mymoviedb.filefinder.FileUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.List;

@Component
public class HibernateVideoFileDao {

    @Autowired
    private VideoFileRepository videoFileRepository;

    @Transactional
    public VideoFile save(VideoFile videoFile) {
        return videoFileRepository.save(videoFile);
    }

    public long count() {
        return videoFileRepository.count();
    }

    @Transactional
    public Movie findMovieForFile(String hashCode) {
        VideoFile fileAndMovie = videoFileRepository.findOneByHashCode(hashCode);
        
        Movie movie = fileAndMovie.getMovie();
        Hibernate.initialize(movie);

        return movie;
    }

    public List<VideoFile> findByHashCode(String fileHash){
        return videoFileRepository.findByHashCode(fileHash);
    }

    @Transactional
    public void deleteFile(VideoFile videoFile) {
        videoFileRepository.deleteById(videoFile.getId());
    }

    public List<VideoFile> findAll() {
        return videoFileRepository.findAll();
    }

    public List<VideoFile> findFileByFileName(String fileName) {
        return videoFileRepository.findByFileName(fileName);
    }

    public List<VideoFile> findByFileName(String fileName) {
        return videoFileRepository.findByFileName(fileName);
    }

    public void populateVideoFile(VideoFile file, Path path) {
        file.setFileName(path.getFileName().toString());
        file.setDirectory(path.getParent().toString());
        file.setDriveLabel(file.computeCurrentDriveLabel());
        String hashCode = FileUtils.computeHash(path);
        file.setHashCode(hashCode);
    }

}
