/*
 * Copyright (C) 2016 gandji <gandji@free.fr>
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
package org.gandji.mymoviedb.filefinder;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.gandji.mymoviedb.data.KeywordExcludeRegexp;
import org.gandji.mymoviedb.data.repositories.KeywordExcludeRegexpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author gandji
 */
@Component
public class FileNameUtils {

    @Autowired
    private KeywordExcludeRegexpRepository keywordExcludeRegexpRepository;
    
    static private List<KeywordExcludeRegexp> dropRegexs = null;
    
    static private String[] splitName(File fil){
        // remove file extension
        String name = fil.getName().replaceFirst("[.][^.]+$","");
        // then split
        String[] kwords = name.split("[ ._-]");
        return kwords;
    }
    
    static private ArrayList<String> filterOut(String[] kwords){
        ArrayList<String> result = new ArrayList<>();
        for (String kw : kwords){
            if (dropKeyword(kw)){
                continue;
            }
            result.add(kw);
        }
        return result;
    }

    static private ArrayList<String> removeAccents(ArrayList<String> kwds) {
        // on enleve les accents
        for (ListIterator<String> it = kwds.listIterator(); it.hasNext();) {
            String kw = it.next();
            kw = Normalizer.normalize(kw, Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            // we can modify the element because we use a ListIterator
            it.set(kw);
        }
        return kwds;
    }
  
    // make non static, for dependency injection, grrrr
    public ArrayList<String> extractKeywords(File file) {
        if (null == dropRegexs){
           dropRegexs = keywordExcludeRegexpRepository.findAll();
        }
        return removeAccents(filterOut(splitName(file)));
    }
    
    private static boolean dropKeyword(String kw) {
        for (KeywordExcludeRegexp dropk : dropRegexs){
            if (null==dropk) { continue;}
            if (kw.matches(dropk.getRegexpString())){
                return true;
            }
        }
        return false;
    }

}
