# Foundation
Foundational components for aiSSEMBLE&trade; efforts


## Build

To build the project, use maven to do a clean install `./mvnw clean install`

To build the project with integration tests use the `-Pintegration-test` profile
- Some integration tests require `Docker` and automatically start/stop `Docker Compose` services while executing tests (i.e. see
the `test/test-mda-models/test-data-delivery-pyspark-patterns` module).

### Troubleshooting

When executing the `aissemble` build for the first time, you may encounter the following transient error when building
the `test-data-delivery-pyspark-patterns` module:

```
:: problems summary 
:::::: WARNINGS
                [NOT FOUND  ] org.apache.commons#commons-math3;3.2!commons-math3.jar (0ms)
         ==== local-m2-cache: tried           file:/Users/ekonieczny/.m2/repository/org/apache/commons/commons-math3/3.2/commons-math3-3.2.jar
                 ::::::::::::::::::::::::::::::::::::::::::::::
                 ::              FAILED DOWNLOADS            ::
                 :: ^ see resolution messages for details ^. ::
                 ::::::::::::::::::::::::::::::::::::::::::::::
                 :: org.apache.commons#commons-math3;3.2!commons-math3.jar
                 :::::::::::::::::::::::::::::::::::::::::::::: 
:::: ERRORS
        SERVER ERROR: Bad Gateway url=https://dl.bintray.com/spark-packages/maven/org/sonatype/oss/oss-parent/9/oss-parent-9.jar
         SERVER ERROR: Bad Gateway url=https://dl.bintray.com/spark-packages/maven/org/antlr/antlr4-master/4.7/antlr4-master-4.7.jar
         SERVER ERROR: Bad Gateway url=https://dl.bintray.com/spark-packages/maven/org/antlr/antlr-master/3.5.2/antlr-master-3.5.2.jar
```
         
If this occurs, remove your local Ivy cache (`rm -rf ~/.ivy2`) and rerun the build.
