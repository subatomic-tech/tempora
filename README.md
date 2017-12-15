Realtime Analytics for Gmail Accounts
------

[![Build Status](https://travis-ci.org/subatomic-tech/tempora.svg?branch=master)](https://travis-ci.org/subatomic-tech/tempora)

## Introduction

Tempora is a simple web application that displays analytics about your Gmail account.

It uses OAuth 2.0 to connect to your Gmail account and fetch the necessary data.

## Disclaimer

Tempora does not connect to any third-party service other than Google, and all the data is stored in memory.

## Running the application

Tempora is a Java Web Application built using [Vaadin 7](https://vaadin.com) and uses [Gradle](https://gradle.org) as a build tool.

To build and run the application:

```shell
$ ./gradlew build
$ cd build/libs
$ java -jar tempora-${JAR_VERSION}.jar
```

The command will download all the necessary dependencies and run the application on port `8080`.

## Docker support

Tempora can also run inside a Docker container. First you need to build the Tempora Docker Image using:

```
$ ./gradlew buildDocker
```

To run a container based on the produced image:

```
$ docker run -d -p 8080:9000 subatomic/tempora
```

The container exposes, by default, port `8080`.

## Licence

```
Copyright 2017 Faissal Elamraoui

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```