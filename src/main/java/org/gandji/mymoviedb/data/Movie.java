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

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author gandji <gandji@free.fr>
 */
@Entity
@Table(name="MOVIE")
@EntityListeners(AuditingEntityListener.class)
public class Movie {

    private static final Logger LOG = Logger.getLogger(Movie.class.getName());
    

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO,generator="movie_seq")
    @SequenceGenerator(name="movie_seq")
    private Long id;

    private String   title="unknown";
    
    private String   year="unknown";
    private URL infoUrl =null;

    private String   director="unknown";

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @ManyToMany(cascade={})
    @JoinColumns({
        @JoinColumn(name="ACTORS_ID",referencedColumnName = "id"),
        @JoinColumn(name="ACTORS_NAME",referencedColumnName = "name")})
    private Set<Actor> actors = null;

    @Column(columnDefinition="VARCHAR(1025)")
    private String   summary="unknown";
    private String   duree="unknown";

    private String   comments="";

    @Lob
    @Column(name="poster")
    private byte[] posterBytes;

    @ManyToMany(cascade=CascadeType.MERGE)
    @JoinColumns({
        @JoinColumn(name="GENRES_NAME",referencedColumnName = "name")})
    private Set<Genre> genres=null;
    
    @OneToMany(mappedBy="movie",cascade=CascadeType.MERGE)
    private Set<VideoFile> files;

    @Column(name = "alternateTitle")
    private String alternateTitle;
    
    private Integer rating = 0;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date lastSeen;

    public Movie() {
        genres = new HashSet<>();
        actors = new HashSet<>();
        try {
            infoUrl = new URL("http://unknown.com");
        } catch (MalformedURLException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public Movie(Movie movie) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.director = movie.getDirector();
        this.year = movie.getYear();
        this.genres = null;
        this.actors = null;
        this.infoUrl = movie.getInfoUrl();
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public URL getInfoUrl() {
        return infoUrl;
    }
    public void setInfoUrl(URL infoUrl) { this.infoUrl = infoUrl; }

    public void setInfoUrlAsString(String infoUrl) {
        try {
            this.infoUrl = new URL(infoUrl);
            return;
        } catch (MalformedURLException ex) {
            LOG.log(Level.SEVERE, "Malformed URL for internet info: ", ex);
        }
        try {
            // I prefer infoUrl not to be null
            this.infoUrl = new URL("http://unknown.com");
        } catch (MalformedURLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            this.infoUrl = null;
        }
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String Director) {
        this.director = Director;
    }

    public Date getCreated() {
        if (null==created) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date d1 = sdf.parse("2017-05-31");
                Date d2 = sdf.parse("2017-08-22");
                created = new Date(ThreadLocalRandom.current().nextLong(d1.getTime(), d2.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return created;
    }

    public void setCreated(Date added) {
        this.created = added;
    }

    public Set<Actor> getActors() {
        return actors;
    }

    private boolean alreadyHaveActor(Actor actor) {
        boolean alreadyHaveActor = false;
        for (Actor actorOfMovie : actors) {
            if (actorOfMovie.getName().equals(actor.getName())) {
                alreadyHaveActor = true;
                break;
            }
        }
        return alreadyHaveActor;
    }

    public void addActorByName(String actorName) {
            Actor actor = new Actor(actorName);
            addActor(actor);
    }

    public void addActor(Actor actor) {
        if (!alreadyHaveActor(actor)) {
            actor.addMovie(this);
            this.actors.add(actor);
        }
    }
   
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDuree() {
        return duree;
    }

    public void setDuree(String duree) {
        this.duree = duree;
    }

    public String getComments() { return comments; }

    public void setComments(String comments) { this.comments = comments; }

    public byte[] getPosterBytes() {
        return posterBytes;
    }

    public void setPosterBytes(byte[] posterBytes) {
        this.posterBytes = posterBytes;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void addGenreByName(String genreName) {
          Genre genre = new Genre(genreName);
        genre.addMovie(this);
        this.genres.add(genre);
    }

    @Override
    public String toString() {
        return "Movie{" + "title=" + title + ", year=" + year + ", infoUrl=" + infoUrl + ", director=" + director + ", summary=" + summary + ", duree=" + duree + "}";
    }

    public void addFile(VideoFile videoFile) {
        if (null==files){
            files = new HashSet<>();
        }
        videoFile.setMovie(this);
        files.add(videoFile);
    }

    public Set<VideoFile> getFiles() {
        return files;
    }

    void clearGenres() {
        this.genres.clear();
    }

    public Long getId() {
        return this.id;
    }

    public String getAlternateTitle() {
        return alternateTitle;
    }

    public void setAlternateTitle(String alternateTitle) {
        this.alternateTitle = alternateTitle;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;

        Movie movie = (Movie) o;

        if (id != null ? !id.equals(movie.id) : movie.id != null) return false;
        if (!title.equals(movie.title)) return false;
        if (year != null ? !year.equals(movie.year) : movie.year != null) return false;
        if (infoUrl != null ? !infoUrl.equals(movie.infoUrl) : movie.infoUrl != null) return false;
        if (director != null ? !director.equals(movie.director) : movie.director != null) return false;
        if (summary != null ? !summary.equals(movie.summary) : movie.summary != null) return false;
        if (duree != null ? !duree.equals(movie.duree) : movie.duree != null) return false;
        if (comments != null ? !comments.equals(movie.comments) : movie.comments != null) return false;
        if (alternateTitle != null ? !alternateTitle.equals(movie.alternateTitle) : movie.alternateTitle != null)
            return false;
        if (rating != null ? !rating.equals(movie.rating) : movie.rating != null) return false;
        return lastSeen != null ? lastSeen.equals(movie.lastSeen) : movie.lastSeen == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + title.hashCode();
        result = 31 * result + (year != null ? year.hashCode() : 0);
        result = 31 * result + (infoUrl != null ? infoUrl.hashCode() : 0);
        result = 31 * result + (director != null ? director.hashCode() : 0);
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        result = 31 * result + (duree != null ? duree.hashCode() : 0);
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        result = 31 * result + (alternateTitle != null ? alternateTitle.hashCode() : 0);
        result = 31 * result + (rating != null ? rating.hashCode() : 0);
        result = 31 * result + (lastSeen != null ? lastSeen.hashCode() : 0);
        return result;
    }
}
