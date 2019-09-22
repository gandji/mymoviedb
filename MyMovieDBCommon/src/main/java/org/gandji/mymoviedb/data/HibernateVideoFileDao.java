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

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.gandji.mymoviedb.data.repositories.VideoFileRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class HibernateVideoFileDao {
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private VideoFileRepository videoFileRepository;

    @Transactional
    public VideoFile save(VideoFile videoFile) {
        return entityManager.merge(videoFile);
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
        videoFileRepository.delete(videoFile);
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
}
