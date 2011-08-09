#!/bin/sh

# This will start a coherence web cache server

# specify the Coherence installation directory

SCRIPT_PATH="${BASH_SOURCE[0]}";
if([ -h "${SCRIPT_PATH}" ]) then
  while([ -h "${SCRIPT_PATH}" ]) do SCRIPT_PATH=`readlink "${SCRIPT_PATH}"`; done
fi
pushd . > /dev/null
cd `dirname ${SCRIPT_PATH}` > /dev/null
SCRIPT_PATH=`pwd`
COHERENCE_HOME=`dirname $SCRIPT_PATH`;
popd  > /dev/null

# specify the JVM heap size
MEMORY=256m

if [ ! -f ${COHERENCE_HOME}/bin/coherence-web-cache.sh ]; then
  echo "coherence-web-cache.sh: must be run from the Coherence installation directory."
  exit
fi

if [ "_$1" = "_" ]; then
  echo "Usage: coherence-web-cache.sh <Member name>"
  exit
fi
memberName=$1

if [ -f $JAVA_HOME/bin/java ]; then
  JAVAEXEC=$JAVA_HOME/bin/java
else
  JAVAEXEC=java
fi

JMXPROPERTIES=""
JMXPROPERTIES=$JMXPROPERTIES" -Dcom.sun.management.jmxremote"
JMXPROPERTIES=$JMXPROPERTIES" -Dtangosol.coherence.management=all"
JMXPROPERTIES=$JMXPROPERTIES" -Dtangosol.coherence.management.remote=true"

COHERENCE_PROPS=""
COHERENCE_PROPS=$COHERENCE_PROPS" -Dtangosol.coherence.session.localstorage=true"
COHERENCE_PROPS=$COHERENCE_PROPS" -Dtangosol.coherence.machine=$memberName"
COHERENCE_PROPS=$COHERENCE_PROPS" -Dtangosol.coherence.member=$memberName"
COHERENCE_PROPS=$COHERENCE_PROPS" -Dtangosol.coherence.cacheconfig=$COHERENCE_HOME/session-cache-config.xml"

JAVA_OPTS="-Xms$MEMORY -Xmx$MEMORY $JMXPROPERTIES $COHERENCE_PROPS"

COHERENCE_CLASSPATH="$COHERENCE_HOME/lib/coherence.jar:$COHERENCE_HOME/lib/coherence-web.jar"

$JAVAEXEC -server -showversion $JAVA_OPTS -cp $COHERENCE_CLASSPATH com.tangosol.net.DefaultCacheServer $2
