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
package org.gandji.mymoviedb.data.repositories;

import org.gandji.mymoviedb.data.VideoFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 *
 * @author gandji <gandji@free.fr>
 */
public interface VideoFileRepository extends JpaRepository<VideoFile, Long> {
    List<VideoFile> findByFileName(String fileName);

    List<VideoFile> findByHashCode(String fileHash);

    VideoFile findOneByHashCode(String fileHash);

    void delete(VideoFile videoFile);

    @Query(value = "select vf from VideoFile vf where vf.hashCode is null ")
    List<VideoFile> findNullHashCodes();
}
