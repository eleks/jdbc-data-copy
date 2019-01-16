## JDBC Data Copy tool

### goal

copy data between jdbc data sources using batch mode

### details

the tool created as a set of groovy scripts that could be easily extended with knowledge of java/groovy.

### how to run

*requirements*: java8 (or java7) ( *note*: do not use java9+ )

* download the latest [release](https://github.com/eleks/jdbc-data-copy/releases) or clone/download latest master version 
* edit connection and data copy parameters in `datacopy.properties` file
* check the sql scripts in `sql-pre` and `sql-post` folders. note, that target database tables must be created with `sql-pre` scripts and without foreign keys. the scripts for foreign keys and other constraints you can put into `sql-post` folder. 
* run `datacopy` command from console

### execution

* during first start required libraies will be downloaded into `lib` folder
* connection established to source and target databases according to parameters in `datacopy.properties` file
* all `*.sql` files from `sql-pre` folder are executed on target database (ordered by name)
* list of tables to copy evaluated according to parameters `table.*` in `datacopy.properties` file
* data copying executed (all table names and column names must be matched in source and target datasources)
* all `*.sql` files from `sql-post` folder are executed on target database (ordered by name)

