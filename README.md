## JDBC Data Copy tool

### goal

copy data between jdbc data sources using batch mode

### details

the tool created as a set of groovy scripts that could be easily extended with knowledge of java/groovy.

### execution

*requirements*: java8 (or java7) ( *note*: do not use java9+ )

* during first start required libraies will be downloaded into `lib` folder
* connection established to source and target databases according to parameters in `datacopy.properties` file
* all `*.sql` files from `sql-pre` folder are executed on target database (ordered by name)
* list of tables to copy evaluated according to parameters `table.*` in `datacopy.properties` file
* data copying executed (all table names and column names must be matched in source and target datasources)
* all `*.sql` files from `sql-post` folder are executed on target database (ordered by name)

### how to run

* download the latest [release](https://github.com/eleks/jdbc-data-copy/releases) or clone/download latest master version 
* edit connection parameters in `datacopy.properties` file
* run `datacopy` command from console
