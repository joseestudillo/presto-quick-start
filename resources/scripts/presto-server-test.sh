export PRESTO_HOME=/usr/local/facebook/presto/current

# Development configuration
mkdir -p /tmp/presto-test-server/data
mkdir -p /tmp/presto-test-server/log
DATA_DIR=/tmp/presto-test-server/data
PID_FILE=/tmp/presto-test-server/launcher.pid
LOG_DIR=/tmp/presto-test-server/log
PRESTO_TEST_HOME=./test-config


launcher $1 --launcher-config=$PRESTO_TEST_HOME/bin/launcher.properties\
 --node-config=$PRESTO_TEST_HOME/etc/node.properties\
 --jvm-config=$PRESTO_HOME/etc/jvm.config\
 --config=$PRESTO_TEST_HOME/etc/config.properties\
 --log-levels-file=$PRESTO_HOME/etc/log.properties\
 --data-dir=$DATA_DIR\
 --pid-file=$PID_FILE\ 
 --launcher-log-file=$LOG_DIR/launcher.log\
 --server-log-file=$LOG_DIR/server.log 

# to set a Java system property use: -D NAME=VALUE         