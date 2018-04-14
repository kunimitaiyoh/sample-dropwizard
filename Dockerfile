FROM ubuntu:17.10

# set locale
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8
RUN apt-get update \
    && apt-get -y install locales \
    && locale-gen en_US.UTF-8

# install Scala and sbt.
# fetch dependencies.
RUN apt-get update
RUN apt-get install -y default-jdk apt-transport-https scala dirmngr bc
RUN echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list \
    && apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823 \
    && apt-get update \
    && apt-get install -y sbt
RUN echo exit | sbt

# copy application source to the container and execute build.
RUN mkdir /usr/src/sample
WORKDIR /usr/src/sample
COPY build.sbt .
COPY project project
RUN sbt clean && sbt -Dpackaging.type=jar update

# build
COPY ./project ./project
COPY ./src ./src
RUN sbt -Dpackaging.type=jar assembly

# TODO: application config files should be specified by ARG.
COPY ./config.test.yaml .
