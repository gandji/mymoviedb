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
package org.gandji.mymoviedb.scrapy;

import java.io.IOException;
import java.lang.reflect.MalformedParametersException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.enumeration.MediaType;
import com.omertron.themoviedbapi.model.config.Configuration;
import com.omertron.themoviedbapi.model.credits.MediaCreditCast;
import com.omertron.themoviedbapi.model.credits.MediaCreditCrew;
import com.omertron.themoviedbapi.model.media.MediaBasic;
import com.omertron.themoviedbapi.model.media.MediaCreditList;
import com.omertron.themoviedbapi.model.movie.MovieBasic;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import com.omertron.themoviedbapi.model.tv.TVBasic;
import com.omertron.themoviedbapi.model.tv.TVInfo;
import com.omertron.themoviedbapi.results.ResultList;
import org.gandji.mymoviedb.data.Movie;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author gandji <gandji@free.fr>
 *
 */
@Component
public class MovieInfoSearchService {

    private static final Logger LOG = LoggerFactory.getLogger(MovieInfoSearchService.class);

    @Value("${mymoviedb.tmdb.apikey}")
    private String tmdbApiKey;

    // TODO configuration option for max movies returned?
    private final Integer maxMoviesReturned = 5;

    public enum UrlType {
        IMDB("IMDB"),
        TMDB("TMDB");

        UrlType(String name) {
            this.name = name;
        }

        String name;

        public static UrlType fromUrlString(String urlString) {
            Pattern imdbPattern = Pattern.compile("https?://www.imdb.com/");
            Pattern tmdbPattern = Pattern.compile("https?://www.themoviedb.org/");

            Matcher imdbMatcher = imdbPattern.matcher(urlString);
            Matcher tmdbMatcher = tmdbPattern.matcher(urlString);

            if (imdbMatcher.lookingAt()) {
                return IMDB;
            }
            else if (tmdbMatcher.lookingAt()) {
                return TMDB;
            }
            return null;
        }

        public static UrlType fromName(String name) {
            for (UrlType urlType : UrlType.values()) {
                if (urlType.name.equals(name)) {
                    return urlType;
                }
            }
            return null;
        }
    }

    public MovieInfoSearchService() {}

    public List<Movie> searchInternetInfoForMovies(List<String> kwds, MovieFoundCallback callback) throws InterruptedException {
        List<Movie> moviesImdb = searchImdbForMovies(kwds,callback);
        List<Movie> movieTmdb = searchTmdbForMovies(kwds, callback);
        moviesImdb.addAll(movieTmdb);
        return moviesImdb;
    }

    private List<Movie> searchTmdbForMovies(List<String> kwds, MovieFoundCallback callback) throws InterruptedException {
        String queryString = assembleQueryString(kwds);
        Integer nMoviesToReturn = maxMoviesReturned;
        List<Movie> movies = new ArrayList<>();

        try {
            TheMovieDbApi tmdb = new TheMovieDbApi(tmdbApiKey);

            LOG.info("Searching TMDB for kwds "+queryString);
            ResultList<MediaBasic> results = tmdb.searchMulti(queryString,1,"fr", false);
            LOG.info("Searching TMDB "+results.getTotalResults()+" results");

            for (MediaBasic mediaBasic : results.getResults()) {
                Movie movie = null;
                if (mediaBasic.getMediaType().equals(MediaType.MOVIE)) {
                    MovieBasic movieBasic = (MovieBasic) mediaBasic;
                    LOG.info("TMDB has movie: "+movieBasic.getTitle());
                    movie = buildMovieFromTmdbMovieBasic(movieBasic, tmdb);
                }
                else if (mediaBasic.getMediaType().equals(MediaType.TV)) {
                    TVBasic tvBasic = (TVBasic) mediaBasic;
                    LOG.info("TMDB has tv show: "+tvBasic.getName());
                    movie = buildMovieFromTmdbTVBasic(tvBasic,tmdb);
                }

                if (null != movie) {
                    movies.add(movie);
                    MovieFoundCallback.MovieFoundResult result = callback.found(movie);
                    if (0 >= nMoviesToReturn-- || result == MovieFoundCallback.MovieFoundResult.STOP) {
                        nMoviesToReturn = 0;// do really stop if requested before end
                        break;
                    }
                }

            }

        } catch (MovieDbException e) {
            e.printStackTrace();
            return null;
        }

        return movies;
    }

    private String assembleQueryString(List<String> kwds) {
        // assemble query string
        String queryString = kwds.get(0);
        // take out the last keyword, often ripper name
        for (Integer i = 1; i < kwds.size()-1; i++) {
            queryString = queryString + "+" + kwds.get(i);
        }
        return queryString;
    }
    private List<Movie> searchImdbForMovies(List<String> kwds, MovieFoundCallback callback) throws InterruptedException {
        List<Movie> movies = new ArrayList<>();
        String queryString = assembleQueryString(kwds);
        Document doc=null;
        try {
            // ask for the page
            LOG.debug("Reaching out to IMDB for search results");
            Connection connection = Jsoup.connect("http://imdb.com/find?q="+queryString)
                    .userAgent("MyPersonalMovieCritics 1.0 (+http://gandji.org)")
                    //.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:51.0) Gecko/20100101 Firefox/51.0")
                    .followRedirects(true)
                    .referrer("http://www.google.com")
                    .timeout(3000);
            Connection.Response reponse = connection.execute();
            doc = reponse.parse();
            // if doc is still null, see http://stackoverflow.com/questions/6581655/jsoup-useragent-how-to-set-it-right
            // however you may find it simpler to use HttpUrlConnection or Apache's HttpClient 
            // and then passing the result to JSoup. An excellent writeup on everything you need to know:
            // http://stackoverflow.com/questions/2793150/using-java-net-urlconnection-to-fire-and-handle-http-requests
            
        } catch (IOException ex) {
            LOG.error("Connection to IMDB failed for query "+queryString+ " : ", ex);
        }

        if (null != doc) {
            // get info from the page
            Integer nMoviesToReturn = maxMoviesReturned;
            Elements elts = doc.select("div[class='findSection']");
            for (Element elt : elts) {
                Elements subElts = elt.select("td[class='result_text']");
                for (Element subElt : subElts) {
                    Elements anchors = subElt.select("a[href]");
                    for (Element anchor : anchors) {
                        // now for each search result: get and parse the href!
                        Movie movie = getOneFilmFromImdb(anchor.attr("abs:href"));
                        if (null != movie) {
                            movies.add(movie);
                            MovieFoundCallback.MovieFoundResult result = callback.found(movie);
                            if (0 >= nMoviesToReturn-- || result == MovieFoundCallback.MovieFoundResult.STOP) {
                                nMoviesToReturn = 0;// do really stop if requested before end
                                break;
                            }
                        }
                    }
                    if (0 >= nMoviesToReturn) {
                        break;
                    }

                }
                if (0 >= nMoviesToReturn) {
                    break;
                }
            }
        }
        return movies;
    }

    public Movie getOneFilmFromUrl(String href) {
        UrlType db = UrlType.fromUrlString(href);
        if (db==UrlType.IMDB) {
            return getOneFilmFromImdb(href);
        }
        else if (db==UrlType.TMDB) {
            return getOnefilmFromTmdb(href);
        }
        else {
            throw new MalformedParametersException("Unknown movie database url: "+href);
        }
    }
    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private Movie buildMovieFromTmdbMovieBasic(MovieBasic movieBasic, TheMovieDbApi tmdb) throws MovieDbException {
        Movie movie = new Movie();
        movie.setTitle(movieBasic.getTitle());

        movie.setInfoUrlAsString("https://www.themoviedb.org/movie/"+movieBasic.getId()+"-x");

        MediaCreditList credits = null;
        credits = tmdb.getMovieCredits(movieBasic.getId());

        List<MediaCreditCrew> crew = credits.getCrew();
        for (MediaCreditCrew crewMember : crew) {
            LOG.debug("Crew job : "+crewMember.getJob()+"    name : "+crewMember.getName());
            if (crewMember.getJob().equals("Director")) {
                movie.setDirector(crewMember.getName());
                break;
            }
        }

        List<MediaCreditCast> cast = credits.getCast();
        for (MediaCreditCast castMember : cast) {
            if (castMember.getOrder()>=0 && castMember.getOrder()<=3) {
                LOG.debug("Actor : " + castMember.getName() + " --- " + castMember.getOrder());
                movie.addActorByName(castMember.getName());
            }
        }

        LOG.debug("OVERVIEW " + movieBasic.getOverview());
        movie.setSummary(movieBasic.getOverview());

        movie.setYear(extractYear(movieBasic.getReleaseDate()));
        LOG.debug("YEAR = "+movieBasic.getReleaseDate()+"  --->  "+movie.getYear());

        if (movieBasic instanceof MovieInfo) {
            // on evite de redemander à tmdb
            movie.setDuree(convertirEnDuree(((MovieInfo)movieBasic).getRuntime()));
        } else {
            MovieInfo movieInfo = tmdb.getMovieInfo(movieBasic.getId(), "fr");
            movie.setDuree(convertirEnDuree(movieInfo.getRuntime()));
        }

        // I want genres in english
        MovieInfo englishMovieInfo = tmdb.getMovieInfo(movieBasic.getId(),"en");
        List<com.omertron.themoviedbapi.model.Genre> genres = englishMovieInfo.getGenres();
        for (com.omertron.themoviedbapi.model.Genre genre : genres) {
            LOG.debug("GENRE : "+genre.getName()+"  "+genre);
            movie.addGenreByName(genre.getName());
        }

        LOG.debug("POSTER PATH = "+movieBasic.getPosterPath());

        Configuration configuration = tmdb.getConfiguration();
        String baseUrl = configuration.getBaseUrl();
        String posterSize = "w185"; // TODO check available
        // TODO scale poster
        String posterUrl = baseUrl+posterSize+movieBasic.getPosterPath();

        LOG.debug("POSTER IS AT "+posterUrl);
        try {
            byte[] bytes = Jsoup.connect(posterUrl).ignoreContentType(true)
                    .userAgent("MyPersonalMovieCritics 1.0 (+http://gandji.org)")
                    //.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:51.0) Gecko/20100101 Firefox/51.0")
                    .followRedirects(true)
                    .referrer("http://www.google.com")
                    .timeout(3000)
                    .execute().bodyAsBytes();
            if (bytes==null) {
                LOG.warn("Could not retrieve poster at <"+posterUrl+">");
            }
            movie.setPosterBytes(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movie;
    }

    private Movie buildMovieFromTmdbTVBasic(TVBasic tvBasic, TheMovieDbApi tmdb) throws MovieDbException {
        Movie movie = new Movie();
        movie.setTitle(tvBasic.getName());

        movie.setInfoUrlAsString("https://www.themoviedb.org/tv/"+tvBasic.getId()+"-x");

        TVInfo tvInfo = null;
        if (tvBasic instanceof TVInfo) {
            tvInfo = (TVInfo)tvBasic;
        } else {
            tvInfo = tmdb.getTVInfo(tvBasic.getId(),"fr");
        }
        MediaCreditList credits = tvInfo.getCredits();
        List<MediaCreditCrew> crew = credits.getCrew();
        for (MediaCreditCrew crewMember : crew) {
            LOG.debug("Crew job : "+crewMember.getJob()+"    name : "+crewMember.getName());
            if (crewMember.getJob().equals("Director")) {
                movie.setDirector(crewMember.getName());
                break;
            }
        }

        List<MediaCreditCast> cast = credits.getCast();
        for (MediaCreditCast castMember : cast) {
            if (castMember.getOrder()>=0 && castMember.getOrder()<=3) {
                LOG.debug("Actor : " + castMember.getName() + " --- " + castMember.getOrder());
                movie.addActorByName(castMember.getName());
            }
        }

        movie.setSummary(tvBasic.getOverview());

        movie.setYear(extractYear(tvBasic.getFirstAirDate()));
        LOG.debug("YEAR = "+tvBasic.getFirstAirDate()+"  --->  "+movie.getYear());

        Integer meanRuntime = 0;
        for (Integer runtime : tvInfo.getEpisodeRunTime()) {
            meanRuntime+=runtime;
        }
        if (tvInfo.getEpisodeRunTime().size()>0) {
            meanRuntime = Math.floorDiv(meanRuntime, tvInfo.getEpisodeRunTime().size());
        }
        LOG.debug("RUNTIME MOYENNE = "+meanRuntime);
        LOG.debug("DUREE   MOYENNE = "+convertirEnDuree(meanRuntime));
        movie.setDuree(convertirEnDuree(meanRuntime));

        // I want genres in english
        TVInfo englishTVInfo = tmdb.getTVInfo(tvBasic.getId(),"en");
        List<com.omertron.themoviedbapi.model.Genre> genres = englishTVInfo.getGenres();
        for (com.omertron.themoviedbapi.model.Genre genre : genres) {
            LOG.debug("GENRE : "+genre.getName()+"  "+genre);
            movie.addGenreByName(genre.getName());
        }

        LOG.debug("POSTER PATH = "+tvBasic.getPosterPath());

        Configuration configuration = tmdb.getConfiguration();
        String baseUrl = configuration.getBaseUrl();
        String posterSize = "w185"; // TODO check available
        // TODO scale poster
        String posterUrl = baseUrl+posterSize+tvBasic.getPosterPath();

        LOG.debug("POSTER IS AT "+posterUrl);
        try {
            byte[] bytes = Jsoup.connect(posterUrl).ignoreContentType(true)
                    .userAgent("MyPersonalMovieCritics 1.0 (+http://gandji.org)")
                    //.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:51.0) Gecko/20100101 Firefox/51.0")
                    .followRedirects(true)
                    .referrer("http://www.google.com")
                    .timeout(3000)
                    .execute().bodyAsBytes();
            if (bytes==null) {
                LOG.warn("Could not retrieve poster at <"+posterUrl+">");
            }
            movie.setPosterBytes(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movie;
    }

    public class TmdbDescriptor {
        public final String type;
        public final Integer id;

        public TmdbDescriptor(String type, Integer id) {
            this.type = type;
            this.id = id;
        }
    }

    public TmdbDescriptor getTmbdIdFromUrl(String url) {
        Pattern extractId = Pattern.compile("https?://www.themoviedb.org/(tv|movie|season|episode)/([0-9]+?)-.*");
        Matcher movieMatcher = extractId.matcher(url);

        if (!movieMatcher.find()) {
            LOG.info("Could not match id in <" + url + ">");
            return null;
        }
        String type = movieMatcher.group(1);
        Integer movieId = Integer.parseInt(movieMatcher.group(2));
        return new TmdbDescriptor(type,movieId);
    }

    private Movie getOnefilmFromTmdb(String href) {

        TmdbDescriptor tmdbDescriptor = getTmbdIdFromUrl(href);
        Integer movieId = tmdbDescriptor.id;
        String type = tmdbDescriptor.type;

        Movie movie = null;

        if (!type.equals("movie")) {
            LOG.warn("Not implemented tmdb data type <" + type+">");
            return null;
        }

        // TODO cache configuration
        try {
            TheMovieDbApi tmdb = new TheMovieDbApi(tmdbApiKey);
            MovieInfo movieInfo = tmdb.getMovieInfo(movieId,"fr");

            movie = buildMovieFromTmdbMovieBasic(movieInfo, tmdb);
            movie.setInfoUrlAsString(href);

        } catch (MovieDbException e) {
            e.printStackTrace();
            LOG.info("Error while asking TMDB: "+e.getMessage());
        }

        return movie;
    }

    private String extractYear(String releaseDate) {
        Pattern yearPattern = Pattern.compile("(\\d\\d\\d\\d)-\\d\\d-\\d+?");
        Matcher yearMatcher = yearPattern.matcher(releaseDate);
        if (yearMatcher.find()) {
            return "("+yearMatcher.group(1)+")";
        } else {
            return "unknown";
        }
    }

    private String convertirEnDuree(int minutes) {
        if (minutes<60) {
            return ""+minutes+"min";
        } else {
            int hours = Math.floorDiv(minutes,60);
            int min = Math.floorMod(minutes,60);
            return ""+hours+"h "+min+"min";
        }
    }

    private Movie getOneFilmFromImdb(String href) {
        Movie movie = new Movie();
        Document doc = null;
        try {
            Connection connection = Jsoup.connect(href) 
                    .userAgent("Mozilla");
            doc = connection.get();
        } catch (IOException ex) {
            LOG.error("cannot find url "+href, ex);
        }
        href=href.replaceAll("/\\?ref_=[a-z_0-9]*","");
        movie.setInfoUrlAsString(href);
        if (null != doc) {
            //System.out.println("BODY: "+doc.body());
            Elements elts = doc.select("div[class='title_wrapper']");
            for (Element elt : elts) {
                Elements subElts = elt.select("h1[itemprop='name']");
                for (Element subElt : subElts) {
                    LOG.debug("Retrieving movie info for "+subElt.text());
                    movie.setTitle(subElt.text());
                }
            }
            if ("unknown".equals(movie.getTitle())) {
                return null;
            }
            elts = doc.select("span[itemprop='director']");
            for (Element elt : elts) {
                movie.setDirector(elt.text());
            }
            elts = doc.select("span[itemprop='actors']");
            for (Element elt : elts) {
                movie.addActorByName(elt.text());
            }

            elts = doc.select("div[class=\"summary_text\"]");
            for (Element elt : elts) {
                if (!elt.text().startsWith("Add a Plot")) {
                    movie.setSummary(elt.text());
                } else {
                    movie.setSummary("pas de resume");
                }
            }
            elts = doc.select("span[id='titleYear']");
            for (Element elt : elts ) {
                movie.setYear(elt.text());
            }
            elts = doc.select("div[class='subtext']");
            for (Element elt : elts) {
                Elements subElts = elt.select("time[itemprop='duration']");
                for (Element subElt : subElts) {
                    movie.setDuree(subElt.text());
                }
                subElts = elt.select("span[itemprop='genre']");
                for (Element subElt : subElts) {
                    // de celui la il peut reellement y an avoir plusieurs
                    movie.addGenreByName(subElt.text());
                }
            }
            // l'image
            elts = doc.select("div[class='poster']");
            for (Element elt : elts) {
                Elements subElts = elt.select("img[itemprop='image']");
                for (Element subElt : subElts ) {
                    String posterUrl = subElt.attr("src");
                    LOG.debug("POSTER IS AT "+posterUrl);
                    try {
                        byte[] bytes = Jsoup.connect(posterUrl).ignoreContentType(true)
                                .userAgent("MyPersonalMovieCritics 1.0 (+http://gandji.org)")
                                //.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:51.0) Gecko/20100101 Firefox/51.0")
                                .followRedirects(true)
                                .referrer("http://www.google.com")
                                .timeout(3000)
                                .execute().bodyAsBytes();
                        movie.setPosterBytes(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return movie;
    }

}
