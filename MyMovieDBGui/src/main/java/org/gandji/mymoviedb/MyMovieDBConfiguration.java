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
package org.gandji.mymoviedb;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *
 * @author gandji <gandji@free.fr>
 * @todo for publication: readme, configuration, what is demonstrated here
 * @todo                  About window
 * @todo                  Configuration procedure at install: configure mysql connection
 * @todo check necessity of hibernate.cfg.xml
 * @todo when clicking prefs "keep file on update", it is not saved unless save button pushed is this really what we want?
 * @todo movie file device for usb: when loading/searching for a file to play or to display information:
 *             get the diskLabel
 *             from all the root fs's, get the drive letter (from getSystemDisplayName())
 *             check it is plugged in, present, active, etc...
 *             replace the drive letter in VideoFile path by the letter found
 * @todo enter actor and genres in movieDescriptionPanel
 * @todo update movie description window when ever a movie changes!!!
 * @todo in preferences: give browser to use? command line?
 * @todo enter movie by hand:genres, actors, poster
 * @todo see iso problem in simplemagic
 * @todo user configurable regexps: implement friendly add/remove regexp
 * @todo search criteria: file version, file name
 */
@Configuration
public class MyMovieDBConfiguration {
}
