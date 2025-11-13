## Stub Parser

This project contains a parser for the Checker Framework's stub files:
https://checkerframework.org/manual/#stub .
It is a fork of the [JavaParser](http://javaparser.org) project.

## Differences between the StubParser and JavaParser

These are the differences between JavaParser and StubParser:

1. StubUnit class that represents the parsed [stubfile](https://checkerframework.org/manual/#stub).
2. Changes to the `java.jj` file to parse the stub files.
3. Methods for parsing the stub files in the `JavaParser` class.

To see the [diffs between the forks](https://stackoverflow.com/questions/4927519/diff-a-git-fork),
enter the root directory of the StubParser and perform the following commands:
```sh
git remote add upstream https://github.com/javaparser/javaparser
git fetch upstream
git diff $(git merge-base upstream/master HEAD) HEAD
```

## Updating from upstream JavaParser

This section describes how to incorporate changes from JavaParser into
StubParser.  Only developers, not users, of StubParser need to do this.

### Preparation

1. Fork [the StubParser project](https://github.com/typetools/stubparser) to your GitHub account.
2. Clone the repository, using *one* of the following two commands:

   ```sh
   git clone git@github.com:{user.name}/stubparser.git
   git clone https://github.com/{user.name}/stubparser
   ```

   and then `cd`:
   ```sh
   cd stubparser
   ```
3. Use JDK 17.  For example:
   ```sh
   usejdk17
   ```

### Updating

1. Update from StubParser.
   ```sh
   git pull --ff-only https://github.com/typetools/stubparser
   ```
2. Find an appropriate [tag name](https://github.com/javaparser/javaparser/tags):
   ```sh
   export VER=3.27.1
   export TAG_NAME=javaparser-parent-${VER}
   ```
3. Create and checkout a new branch, via ONE of the below:
   ```sh
   git checkout -b updating-${TAG_NAME}
   ```
   or (alternate version):
   ```sh
   gnb updating-${TAG_NAME}
   cd ../stubparser-fork-${USER}-branch-updating-${TAG_NAME}
   ```
4. Pull the upstream of [the JavaParser project](https://github.com/javaparser/javaparser).
   ```sh
   git remote add upstream https://github.com/javaparser/javaparser
   git fetch upstream
   git merge ${TAG_NAME}
   ```
5. If there are conflicts, resolve them and commit (but don't push yet).
6. Update the StubParser version number in the `<finalName>` block of `javaparser-core/pom.xml`.
   (There should not be "-SNAPSHOT" there or in `<version>` in the top-level `pom.xml`.)
7. Run Maven tests in the root directory, using JDK 17.  This takes about 2 minutes.
   ```sh
   ./mvnw install test
   ```
   If any tests fail, fix them before continuing.

8. Deploy the snapshot. (This has been tested on tern.)
   Update the `version` block in `javaparser-core/cfMavenCentral.xml` to be the same as the
   JavaParser version plus `-SNAPSHOT`.  Run the following in `javaparser-core` 
   ```sh
   export STUBPARSER=stubparser-${VER}
   export HOSTING_INFO_DIR=/projects/swlab1/checker-framework/hosting-info
    ../mvnw gpg:sign-and-deploy-file -Durl=https://oss.sonatype.org/content/repositories/snapshots/  \
       -DpomFile=cfMavenCentral.xml -Dfile=target/$STUBPARSER.jar\
       -Dgpg.keyname=checker-framework-dev@googlegroups.com \
       -Dgpg.passphrase="`cat $HOSTING_INFO_DIR/release-private.password`" \
       -DrepositoryId=sonatype-nexus-staging
   ```
   (You must have a file at `~/.m2/settings.xml` that lists [a user token](https://central.sonatype.org/publish/generate-token/) and password for server `sonatype-nexus-staging`. )

9. Update the stubparser version number in the Checker Framework.  Create
   a branch with the same name as your StubParser branch.  In
   `checker-framework/framework/build.gradle`, update `stubparserJar`.
10. Run Checker Framework tests (`./gradlew build`), using your StubParser branch.
If any tests fail, fix them before continuing.
11. Commit and push your changes to Checker Framework.
12. Once the Azure tests pass, release the Stubparser:

   Delete `-SNAPSHOT` from the version in `javaparser-core/cfMavenCentral.xml`.

   ```sh
   ../mvnw source:jar && \
   ../mvnw javadoc:javadoc && (cd target/reports/apidocs && jar -c -f ../../$STUBPARSER-javadoc.jar com)

   ../mvnw gpg:sign-and-deploy-file -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/  \
       -DpomFile=cfMavenCentral.xml -Dfile=target/$STUBPARSER.jar \
       -Dgpg.keyname=checker-framework-dev@googlegroups.com \
       -Dgpg.passphrase="`cat $HOSTING_INFO_DIR/release-private.password`" \
       -DrepositoryId=sonatype-nexus-staging

   ../mvnw gpg:sign-and-deploy-file -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/  \
       -DpomFile=cfMavenCentral.xml \
       -Dgpg.keyname=checker-framework-dev@googlegroups.com \
       -Dgpg.passphrase="`cat $HOSTING_INFO_DIR/release-private.password`" \
       -DrepositoryId=sonatype-nexus-staging \
       -Dclassifier=javadoc -Dfile=target/$STUBPARSER-javadoc.jar

   ../mvnw gpg:sign-and-deploy-file -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/  \
       -DpomFile=cfMavenCentral.xml \
       -Dgpg.keyname=checker-framework-dev@googlegroups.com \
       -Dgpg.passphrase="`cat $HOSTING_INFO_DIR/release-private.password`" \
       -DrepositoryId=sonatype-nexus-staging \
       -Dclassifier=sources -Dfile=target/$STUBPARSER-sources.jar
   ```

   Complete the release at https://oss.sonatype.org/#stagingRepositories.

13. In the Checker Framework, remove `SNAPSHOT` in the StubParser version numbers. 
   Commit and push your changes to Checker Framework.

14. Push commits to your fork of StubParser.
   ```sh
   git push
   ```
   GitHub Actions CI will not run for your branch.

15. Create a [pull request to `typetools/stubparser`](https://github.com/typetools/stubparser).
   Give it a title like "Update to JavaParser 3.24.3".
   Do *not* squash-and-merge the pull request;
   you want to keep a history of what upstream commits were merged in.

16. Create a [pull request to `typetools/checker-framework`](https://github.com/typetools/checkerframework).
   Give it a title like "Update to StubParser 3.24.3".

17. Merge both pull requests when both pass.

## Changes to StubParser that break the Checker Framework

If you commit a change to the StubParser that breaks the Checker Framework,
then change the StubParser version number.  Do this only if necessary (probably
not for minor bug fixes), because it breaks the `mvn javadoc:javadoc` command.
 * In `javaparser-core/pom.xml`, in the `<finalName>` block.
 * In the Checker Framework's top-level `build.gradle` file, on the
   `stubparserJar =` line.

## Original JavaParser README

The remainder of this README file is the original JavaParser README.


<!--
    Note that edits to this readme should be done via `docs/readme.md`.
    Modifying this file directly within the root directory risks it being overwritten. 
-->

# JavaParser

[![Maven Central](https://img.shields.io/maven-central/v/com.github.javaparser/javaparser-core.svg)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.github.javaparser%22%20AND%20a%3A%22javaparser-core%22)
[![Build Status](https://github.com/javaparser/javaparser/actions/workflows/maven_tests.yml/badge.svg?branch=master)](https://github.com/javaparser/javaparser/actions/workflows/maven_tests.yml)
[![Coverage Status](https://codecov.io/gh/javaparser/javaparser/branch/master/graphs/badge.svg?branch=master)](https://app.codecov.io/gh/javaparser/javaparser?branch=master)
[![Join the chat at https://gitter.im/javaparser/javaparser](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/javaparser/javaparser?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![License LGPL-3/Apache-2.0](https://img.shields.io/badge/license-LGPL--3%2FApache--2.0-blue.svg)](LICENSE)
[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.2667378.svg)](https://doi.org/10.5281/zenodo.2667378)


This project contains a set of libraries implementing a Java 1.0 - Java 21 Parser with advanced analysis functionalities.

Our main site is at [JavaParser.org](http://javaparser.org)

## Sponsors

Support this project by becoming a sponsor! [Become a sponsor](https://opencollective.com/javaparser). Your donation will help the project live and grow successfully.

Javaparser uses [OpenCollective](https://opencollective.com/javaparser) to gather money.

**Thank you to our sponsors!**


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
    <version>3.27.1</version>
</dependency>
```

**Gradle**:

```
implementation 'com.github.javaparser:javaparser-symbol-solver-core:3.27.1'
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
    <version>3.27.1</version>
</dependency>
```

**Gradle**:

```
implementation 'com.github.javaparser:javaparser-core:3.27.1'
```

Since version 3.6.17 the AST can be serialized to JSON.
There is a separate module for this:

**Maven**:

```xml
<dependency>
    <groupId>com.github.javaparser</groupId>
    <artifactId>javaparser-core-serialization</artifactId>
    <version>3.27.1</version>
</dependency>
```

**Gradle**:

```
implementation 'com.github.javaparser:javaparser-core-serialization:3.27.1'
```

## How To Compile Sources

If you checked out the project's source code from GitHub, you can build the project with maven using:
```
./mvnw clean install
```

If you want to generate the packaged jar files from the source files, you run the following maven command:
```
./mvnw package
```

**NOTE** the jar files for the two modules can be found in:
- `javaparser/javaparser-core/target/javaparser-core-\<version\>.jar`
- `javaparser-symbol-solver-core/target/javaparser-symbol-solver-core-\<version\>.jar`

If you checkout the sources and want to view the project in an IDE, it is best to first generate some of the source files;
otherwise you will get many compilation complaints in the IDE. (`./mvnw clean install` already does this for you.)

```
./mvnw javacc:javacc
```

If you modify the code of the AST nodes, specifically if you add or remove fields or node classes,
the code generators will update a lot of code for you.
The `run_metamodel_generator.sh` script will rebuild the metamodel,
which is used by the code generators which are run by `run_core_generators.sh`
Make sure that `javaparser-core` at least compiles before you run these.

**Note**: for Eclipse IDE follow the steps described in the wiki: https://github.com/javaparser/javaparser/wiki/Eclipse-Project-Setup-Guide

## More information

#### [JavaParser.org](https://javaparser.org) is the main information site or see the wiki page https://github.com/javaparser/javaparser/wiki.

## License

JavaParser is available either under the terms of the LGPL License or the Apache License. You as the user are entitled to choose the terms under which adopt JavaParser.

For details about the LGPL License please refer to [LICENSE.LGPL](https://github.com/javaparser/javaparser/blob/master/LICENSE.LGPL).

For details about the Apache License please refer to [LICENSE.APACHE](https://github.com/javaparser/javaparser/blob/master/LICENSE.APACHE).
