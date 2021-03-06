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

import javax.persistence.*;

/**
 *
 * @author gandji <gandji@free.fr>
 */
@Entity
@Table(name="KER",uniqueConstraints = {
       @UniqueConstraint(columnNames = {"regexpstring"}) })
public class KeywordExcludeRegexp {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO,generator = "ker_seq")
    @SequenceGenerator(name="ker_seq")
    private Long id;
    
    private String regexpString;

    public KeywordExcludeRegexp() {
    }

      public KeywordExcludeRegexp(String regex) {
          regexpString=regex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegexpString() {
        return regexpString;
    }

    public void setRegexpString(String regexp) {
        this.regexpString = regexp;
    }

}
