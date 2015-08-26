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