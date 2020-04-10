MyMovieDB
=========

MyMovieDB is a database tool dedicated
to personal video file management.

Once you compressed all your DVDs into video 
divx, mkv, iso, avi whatever, and have them 
stored on some disk, you may want to sit down and
be able to choose in a friendly way 
the movie you are going to watch. 

Since you
have nearly a thousand video files on the disk,
you do not want to skim through all files, all 
directories, determine what movie is in a given file,
 find the critics for this movie, see if it fits
 your co-watchers, etc...
 
What you want to do is search your movies
by title keywords, by movie genre, by actor, or by 
custom tags, see the poster, see the internet critics, 
and launch the movie with one click.
That's what I wanted to do , and
I was not satisfied by the free software I found,
so I wrote this small DB management system.

It can be used also as a demonstration of various
java Spring techniques I learned recently.

Pre requisites
--------------

#### Install MySQL server

MyMovieDB uses a MySQL server as a local database.

If you do not already have a MySQL server installed,
download and install a MySQL server.
You can for example download MySQL Community Server from 
https://dev.mysql.com/downloads/

Create a database named mymoviedb.
You can use another name, in which case 
you must use that name in the configuration below
(see "Install from source" below)

Get the application
-------------------

Two solutions:
 - install the provided package at ...
 - compile and install from the github source (experienced users)

####Install from bundle

 - download and unzip the package where you want it
 - Tell MyMovieDB where your MySQL server is by editing the MyMovieDB-x.x.x.bat file.

####Install from sources
 use maven:
`mvn package -DskipTests=true -Ddatasource.username=<your mysql user>
 -Ddatasource.password=<your pass word>
 -Ddatasource.url=jdbc:mysql://localhost:3306/mymoviedb
`

(I have no tests, I just use it)

Install it by hand where you want it.

You can create a shortcut to the .bat script.


Getting Started
-----

#### Fill the local DB


The longest part is in filling in your own local DB: 
 - scan a directory with your movie files in it
 - MyMovieDB finds the video files in the directory and its sub directories,
 - for each video file it finds, MyMovieDB tries to 
 determine title keywords from the file 
 name (not always easy)
 - First MyMovieDB searches the local DB for these keywords,
 and you can point it to the movie if it is in the local DB,
 - if the movie is not in the local DB, MyMovieDB searches a famous internet movie database
 for the given movie (namely themoviedatabase). 
  - It will ask for confirmation once it found a movie,
  - or it will ask for help if it  does not find it. In that case, you search for yourself and enter
 in MyMovieDB just the "themoviedatabase" URL
 
 You can interrupt the process any time and come back to it later.

 #### Enjoy
 Once the local DB is filled, you can browse the DB, search, 
 and play movies.
 
 Getting help
 ------
 
 Mail me: gandji at free dot fr 
 
 File a bug report on github
 
Programming techniques
-----
I started implementing this database as an exercise for various techniques
I learned recently, and other techniques I learned along the way.
It can been seen as a demonstrator for :

 - Maven inheritance of modules. the project now has three modules,
 I tried to end up with more or less clean pom files.

 - Spring usage:
   - I do all I can to use annotation based configurations rather than file wise configurations.
   - spring boot
   - spring beans, singletons and prototypes
   - spring data with jpa and hibernate, annotation based configurations

 - Data management Architecture:
   - DAO layer
   - the underneath database can be mysql or h2 (I am less at ease with H2, 
 so everything does not work with h2). I did this as an exercise to 
 configure the data access bean
 through spring profiles. Spring profile "mysql" activates the use of MySQL database
 (the default), profile "h2" uses an h2 database. 
 
 - REST api
 
 - Web app with thymeleaf renderer. I did this because I wanted a nice carousel
 for the movie selection. I did not pursue because I want to be able 
 to launch a movie from the app, and this is "forbidden" from a web app,
 for obvious security reasons.
 
 - so I turned to a JavaFX GUI. I could integrate the JavaFX app with spring, thanks to the web.
 At launch time, you can choose between JavaFX app or Swing app. For services that depend
 on being swing or javafx, I internally define a spring
 profile 'javafx' in case we launch JavaFX, and profile 'swing' in case we launch swing app.  
 
 - In the JavaFX app, I use an embedded JavaFX browser. I use an embedded JavaFX browser
 because I want to reuse the thymeleaf rendering I made, and I still wanted the nice
 "material" carousel. Unfortunately, the carousel does not render as nicely
 in the JavaFX web browser (:sigh:).
 
 - For the browser:
    - I can call java from javascript
    - I can call javascript from java through the webEngine
    - all this thanks to various web tutos.