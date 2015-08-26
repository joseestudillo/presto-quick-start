# Presto quick start guide

[Presto][presto]
[Presto Documentation][presto-docs]
[Presto Thread at Ycombinator][ycombinator-presto]
[Architecture description and useful information at facebook][facebook-presto]

## Concepts

- _Node_: A node is a single installed instance of Presto on a machine.
- _Types of presto servers_:
  - _Coordinator_: In this mode, a presto server take the queries from clients and manage their execution on the cluster.
  - _Worker_:
- _Task_:  a fragment of a query plan running on a node.
- _Catalog_: Entry to define a connector. They are property files stored by default in `$PRESTO_HOME/etc/catalogs`.
- _Connector_: Defines a way for Presto to access different sources of data. There are predefined connectors for the most populars databases and data sources.


## Quick Installation

- Create the folder `/usr/local/facebook/presto` with the command: 

```bash
sudo mkdir -p /usr/local/facebook/presto
```

- copy into it:
  - `presto-server-x.y` (directory extracted from presto-server-x.y.tar.gz)
  - `presto-cli-x.y-executable.jar`
  - `presto-verifier-x.y-executable.jar`
  - `presto-benchmark-driver-x.y-executable.jar`
  
- create the `current` directory symb link (in case you need to update this will pick up the latest version): 

```bash
ln -sf $(ls -rtd presto-server*/ | head -1) current
```

- grant execution permissions to the all the executable jar files: 

```bash
sudo chmod o+rx presto-*-executable.jar
```

- creating symb link into the $PRESTO_HOME/bin folder to be able to call the jar with simpler command names (`presto-cli`, `presto-verifier`, `presto-benchmark-driver`):
```bash
(
cd current/bin; 
ln -sf $(ls -rt ../../presto-cli-*-executable.jar | head -1) presto-cli;
ln -sf $(ls -rt ../../presto-verifier-*-executable.jar | head -1) presto-verifier;
ln -sf $(ls -rt ../../presto-benchmark-driver-*-executable.jar | head -1) presto-benchmark-driver;
)
```

- Create presto environment vars and add it to the path (this should be added in your profile.d or bash initialization script)

```bash
export PRESTO_HOME=/usr/local/facebook/presto/current
export PATH=$PATH:$PRESTO_HOME/bin
```

- for development, I would recommend adding the following script to the Presto binaries directory, that allows to run Presto locally relaying in the default configuration (`resources/scripts/presto-server.sh`):

```bash
export PRESTO_HOME=/usr/local/facebook/presto/current

 # Proper directory configuration (It requires user creation for execution and proper permissioning)
 # mkdir -p /var/presto
 # mkdir -p /var/log/presto
 # DATA_DIR=/var/presto
 # PID_FILE=/var/run/launcher.pid
 # LOG_DIR=/var/log/presto

 # Development configuration
mkdir -p /tmp/presto/data
mkdir -p /tmp/presto/log
DATA_DIR=/tmp/presto/data
PID_FILE=/tmp/presto/launcher.pid
LOG_DIR=/tmp/presto/log

launcher $1 --launcher-config=$PRESTO_HOME/bin/launcher.properties\
 --node-config=$PRESTO_HOME/etc/node.properties\
 --jvm-config=$PRESTO_HOME/etc/jvm.config\
 --config=$PRESTO_HOME/etc/config.properties\
 --log-levels-file=$PRESTO_HOME/etc/log.properties\
 --data-dir=$DATA_DIR\
 --pid-file=$PID_FILE\ 
 --launcher-log-file=$LOG_DIR/launcher.log\
 --server-log-file=$LOG_DIR/server.log 

 # to set a Java system property use: -D NAME=VALUE 
```


## Starting the server

The server can be started as a daemon using `start` and in foreground using `run`, for development `run` allow you to see presto output directly on stdout:

```
presto-server.sh run
```


## Starting Presto CLI

```
presto-cli --server localhost:8080
```

By default, the results are paginated using `less`, this can be changed setting the env var `PRESTO_PAGER` to use a different program, or empty to disable the pagination.


## Launching more that one server in the same host

to launch a second instance of presto, it is just required to set a different port and configuration directories. There is a complete example and an script to run it at `resources/scripts/presto-server-test.sh` and the related configuration is stored at the same level at `test-config`.


## Using Presto Verifier

TODO


## Using Presto Benchmark Driver

TODO

 
## Adding a catalog

You just need to drop a properties file in to `$PRESTO_HOME/etc/catalog`. 

For a local [Hive][hive-quickstart] instance you just need to add the file:

- `hive-local.properties`:
```
connector.name=hive-hadoop2
hive.metastore.uri=thrift://localhost:10000
```

Check how to run a local version of hive at [Hive quick start][hive-quickstart]

This will require to restart the presto server.


## Example using local hive

- Open beeline and connect to hive using `!connect jdbc:hive2://localhost:10000`

- Create a dummy table and dummy data. The data file will be `/tmp/data.txt` and its content could be something like:

```bash
a, 1
b, 2
c, 3
```

Once the file is in place we can run the following HQL code in the Beeline CLI:

```SQL		
DROP TABLE IF EXISTS presto_qs_tbl;

CREATE EXTERNAL TABLE IF NOT EXISTS presto_qs_tbl(aString String, aInt INT) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
LOAD DATA LOCAL INPATH '/tmp/data.txt' OVERWRITE INTO TABLE presto_qs_tbl;
```

- From presto-cli hive is accessed as follows:

```
SELECT * FROM CATALOG_FILENAME.SCHEMA_OR_DATABASE.TABLE_NAME
```

so assuming that catalog file is in `$PRESTO_HOME/etc/catalog/hive_local.properties` we get to the query:

```
SELECT * FROM hive_local.default.presto_qs_tbl;
```

A working example of the catalog file can be found at `resources/etc/catalog/hive_local.properties` in the project directory.



[presto]: https://prestodb.io
[presto-docs]: https://prestodb.io/docs/current
[hive-quickstart]: https://github.com/joseestudillo/hive-quick-start
[facebook-presto]: https://www.facebook.com/notes/facebook-engineering/presto-interacting-with-petabytes-of-data-at-facebook/10151786197628920
[ycombinator-presto]: https://news.ycombinator.com/item?id=6684678