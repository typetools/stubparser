## Stub Parser

This package contains a parser for the Checker Framework's stub files:
https://checkerframework.org/manual/#stub
It is a fork of the [JavaParser](http://javaparser.org) project.

## Differences between the StubParser and JavaParser

These are the differences between JavaParser and StubParser:

1. StubUnit class that represents the parsed [stubfile](https://checkerframework.org/manual/#stub).
2. Changes to the `java.jj` file to parse the stub files.
3. Methods for parsing the stub files in the `JavaParser` class.

To see the {diffs between the forks](https://stackoverflow.com/questions/4927519/diff-a-git-fork),
enter the root directory of the StubParser and perform following commands:
```bash
git remote add original https://github.com/javaparser/javaparser
git fetch original
git diff HEAD original/master
```

## Updating from upstream JavaParser

This section describes how to incorporate changes from JavaParser into
StubParser.  Only developers, not users, of StubParser need to do this.

1. Fork [the StubParser project](https://github.com/typetools/stubparser) to your GitHub account.
2. Enable Travis build for your fork of the StubParser. 
[How to get started with Travis CI](https://docs.travis-ci.com/user/getting-started/#To-get-started-with-Travis-CI).
3. Clone the repository, using *one* of the following two commands:
```bash
git clone git@github.com:{user.name}/stubparser.git
git clone https://github.com/{user.name}/stubparser
```
4. Update from StubParser.
```bash
cd stubparser
git pull --ff-only https://github.com/typetools/stubparser
```
5. Create and checkout a new branch named `updating`.
```bash
git checkout -b updating
```
6. Pull the upstream of [the JavaParser project](https://github.com/javaparser/javaparser).
Find an appropriate [tag name](https://github.com/javaparser/javaparser/tags)
such as `javaparser-parent-3.10.2`.
```bash
git pull https://github.com/javaparser/javaparser TAG-NAME
```
7. Resolve conflicts if required and commit it.
8. Run maven tests in the root directory. If any tests fail, fix them before continuing.
```bash
mvn test
```
9. Run Checker Framework tests, using your StubParser branch. If any tests fail, fix them before continuing.
10. Push commits to your fork of the StubParser.
```bash
git push
```
11. Check that the Travis build was successful. If not, resolve the issues and repeat steps 7-9.
12. Create a [pull request to `typetools/stubparser`](https://github.com/typetools/stubparser).
When merging the pull request, give it a commit message like "Update to JavaParser 3.10.2".
Do *not* squash-and-merge the pull request;
you want to keep a history of what upstream commits were merged in.


## Original JavaParser README

The remainder of this README file is the original JavaParser README.


# JavaParser

[![Maven Central](https://img.shields.io/maven-central/v/com.github.javaparser/javaparser-core.svg)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.github.javaparser%22%20AND%20a%3A%22javaparser-core%22)
[![Build Status](https://travis-ci.org/javaparser/javaparser.svg?branch=master)](https://travis-ci.org/javaparser/javaparser)
[![Coverage Status](https://coveralls.io/repos/javaparser/javaparser/badge.svg?branch=master&service=github)](https://coveralls.io/github/javaparser/javaparser?branch=master)
[![Join the chat at https://gitter.im/javaparser/javaparser](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/javaparser/javaparser?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![License LGPL-3/Apache-2.0](https://img.shields.io/badge/license-LGPL--3%2FApache--2.0-blue.svg)](LICENSE)

This project contains a set of libraries implementing a Java 1.0 - Java 12 Parser with advanced analysis functionalities.

Our main site is at [JavaParser.org](http://javaparser.org)

## Setup

The project binaries are available in Maven Central. 

We strongly advise users to adopt Maven, Gradle or another build system for their projects.
If you are not familiar with them we suggest taking a look at the maven quickstart projects 
([javaparser-maven-sample](https://github.com/javaparser/javaparser-maven-sample), 
[javasymbolsolver-maven-sample](https://github.com/javaparser/javasymbolsolver-maven-sample)).

Just add the following to your maven configuration or tailor to your own dependency management system.

[Please refer to the Migration Guide when upgrading from 2.5.1 to 3.0.0+](https://github.com/javaparser/javaparser/wiki/Migration-Guide)

**Maven**: 

```xml
<dependency>
    <groupId>com.github.javaparser</groupId>
    <artifactId>javaparser-symbol-solver-core</artifactId>
    <version>3.14.5</version>
</dependency>
```

**Gradle**:

```
implementation 'com.github.javaparser:javaparser-symbol-solver-core:3.14.5'
```

Since Version 3.5.10, the JavaParser project includes the JavaSymbolSolver. 
While JavaParser generates an Abstract Syntax Tree, JavaSymbolSolver analyzes that AST and is able to find 
the relation between an element and its declaration (e.g. for a variable name it could be a parameter of a method, providing information about its type, position in the AST, ect).

Using the dependency above will add both JavaParser and JavaSymbolSolver to your project. If you only need the core functionality of parsing Java source code in order to traverse and manipulate the generated AST, you can reduce your projects boilerplate by only including JavaParser to your project:

**Maven**: 

```xml
<dependency>
    <groupId>com.github.javaparser</groupId>
    <artifactId>javaparser-core</artifactId>
    <version>3.14.5</version>
</dependency>
```

**Gradle**:

```
implementation 'com.github.javaparser:javaparser-core:3.14.5'
```

Since version 3.6.17 the AST can be serialized to JSON.
There is a separate module for this:

**Maven**: 

```xml
<dependency>
    <groupId>com.github.javaparser</groupId>
    <artifactId>javaparser-core-serialization</artifactId>
    <version>3.14.5</version>
</dependency>
```

**Gradle**:

```
implementation 'com.github.javaparser:javaparser-core-serialization:3.14.5'
```

## How To Compile Sources

If you checked out the project from GitHub you can build the project with maven using:

```
mvn clean install
```

If you checkout the sources and want to view the project in an IDE, it is best to first generate some of the source files; otherwise you will get many compilation complaints in the IDE. (mvn clean install already does this for you.)

```
mvn javacc:javacc
```

If you modify the code of the AST nodes, specifically if you add or remove fields or node classes,
the code generators will update a lot of code for you.
The `run_metamodel_generator.sh` script will rebuild the metamodel,
which is used by the code generators which are run by `run_core_generators.sh`
Make sure that `javaparser-core` at least compiles before you run these.

## More information

#### [JavaParser.org](https://javaparser.org) is the main information site.

## License

JavaParser is available either under the terms of the LGPL License or the Apache License. You as the user are entitled to choose the terms under which adopt JavaParser.

For details about the LGPL License please refer to [LICENSE.LGPL](ttps://github.com/javaparser/javaparser/blob/master/LICENSE.LGPL).

For details about the Apache License please refer to [LICENSE.APACHE](ttps://github.com/javaparser/javaparser/blob/master/LICENSE.APACHE).
