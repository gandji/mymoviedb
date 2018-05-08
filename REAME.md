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
 for the given movie (namely imdb). 
  - It will ask for confirmation once it found a movie,
  - or it will ask for help if it  does not find it. In that case, you search for yourself and enter
 in MyMovieDB just the imdb URL
 
 You can interrupt the process any time and come back to it later.

 #### Enjoy
 Once the local DB is filled, you can browse the DB, search, 
 and play movies.
 
 Getting help
 ------
 
 Mail me: gandji at free dot fr 
 
 File a bug report on github
 

 
 
 