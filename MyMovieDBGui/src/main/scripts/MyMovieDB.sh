#!/usr/bin/env bash

java -Dspring.datasource.password="@datasource.password@" \
 -Dspring.datasource.username=@datasource.username@ \
 -Dspring.datasource.url=@datasource.url@ \
 -Dmymoviedb.tmdb.apikey=@tmdb.apikey@ \
 -jar @artifactId@-@version@.@packaging@ "$@"
