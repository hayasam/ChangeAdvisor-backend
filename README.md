# ChangeAdvisor Back-end



## Getting started

#### Mongodb

*ChangeAdvisor* requires MongoDB v3.6.4.



#### Maven

Maven is also necessary. Version 3.5.3 was tested.



#### Ardoc & Review crawler

In order to run *ChangeAdvisor*, we first need to install the jar files from `main/resources/lib` into the local maven repository, run the following snippets from the same directory where the `pom.xml` resides.

```bash
mvn install:install-file -Dfile=src/main/resources/lib/ardoc.jar -DgroupId=it.unisa.sesa.ardoc -DartifactId=ardoc -Dversion=1.0 -Dpackaging=jar
```

```bash
mvn install:install-file -Dfile=src/main/resources/lib/reviewCrawler.jar -DgroupId=reviewCrawler -DartifactId=reviewCrawler -Dversion=1.0 -Dpackaging=jar
```



### Disclaimer

*ChangeAdvisor* was tested on Windows 10 (1709) and on macOS High Sierra (10.13.4), both running Java 1.8.0_73.
