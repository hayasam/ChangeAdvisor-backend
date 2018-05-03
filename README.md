To install the jar files from `main/resources/lib` into the local maven repository, run the following snippets from the same directory where the `pom.xml` resides.

```bash
mvn install:install-file -Dfile=src/main/resources/lib/ardoc.jar -DgroupId=it.unisa.sesa.ardoc -DartifactId=ardoc -Dversion=1.0 -Dpackaging=jar
```

```bash
mvn install:install-file -Dfile=src/main/resources/lib/reviewCrawler.jar -DgroupId=reviewCrawler -DartifactId=reviewCrawler -Dversion=1.0 -Dpackaging=jar
```

