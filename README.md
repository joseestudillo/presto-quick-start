# Presto quick start guide

[Presto][presto]
[Presto Documentation][presto-docs]

## Concepts

- _Coordinator_:
- _Worker_:
- _Catalog_:


## Quick Installation

- Create the folder `/usr/local/facebook/presto` with the command: 

```bash
sudo mkdir -p /usr/local/facebook/presto
```

- copy into it:
  - `presto-server-x.y` (decompressed)
  - `presto-cli-x.y-executable.jar`
  - `presto-verifier-x.y-executable.jar`
  - `presto-benchmark-driver-x.y-executable.jar`
  
- create the `current` directory symb link (in case you need to update this will pick up the latest version): 

```bash
ln -s $(ls -rtd presto-server*/ | head -1) current
```

- grant execution permissions to the all the executable jar files: 

```bash
sudo chmod o+rx presto-*-executable.jar
```

- creating symb link into the bin folder to be able to call the jar with simpler commands (`presto-cli`, `presto-verifier`, `presto-benchmark-driver`):
```bash
(
cd current/bin; 
ln -s $(ls -rt ../../presto-cli-*-executable.jar | head -1) presto-cli;
ln -s $(ls -rt ../../presto-verifier-*-executable.jar | head -1) presto-verifier;
ln -s $(ls -rt ../../presto-benchmark-driver-*-executable.jar | head -1) presto-benchmark-driver;
)
```

- Create presto environment var and add it to the path (this should be added in your profile.d or bash initialization script)

```bash
export PRESTO_HOME=/usr/local/facebook/presto/current
export PATH=$PATH:$PRESTO_HOME/bin
```

- for development, I would recommend adding the following script to the Presto binaries directory, that allows to run Presto locally relaying in the default configuration (`resources/scripts/presto-server.sh`):

```BASH
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

## Starting the CLI

```
presto-cli --server localhost:8080
```

By default, the results are paginated using `less`, thiscan be change setting the env var `PRESTO_PAGER` to use a different program, or empty to disable the pagination.
 
## Adding a catalog

You just need to drop a properties file in to `$PRESTO_HOME/etc/catalog`. 

For a local [Hive][hive-quickstart] instance you just need to add the file:

- `hive-local.properties`:
```
connector.name=hive-hadoop2
hive.metastore.uri=thrift://localhost:10000
```

This will require to restart the server and the hive instance must be running.

## Example using local hive

- Create a table in hive and add content

connect to hive using `!connect jdbc:hive2://localhost:10000`

create a dummy table:

```SQL		
DROP TABLE IF EXISTS presto_qs_tbl;

CREATE EXTERNAL TABLE IF NOT EXISTS presto_qs_tbl(aString String, aInt INT) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
LOAD DATA LOCAL INPATH '/tmp/data.txt' OVERWRITE INTO TABLE presto_qs_tbl;
```

where `/tmp/data.txt` content looks like:

```bash
a, 1
b, 2
c, 3
```


- query the table from presto-cli:

```
SELECT * FROM CATALOG_FILENAME.SCHEMA_OR_DATABASE.TABLE_NAME
```

in out case this translates to:

```
SELECT * FROM hive_local.default.presto_qs_tbl;
```



[presto]: https://prestodb.io
[presto-docs]: https://prestodb.io/docs/current
[hive-quickstart]: www.joseestudillo.com