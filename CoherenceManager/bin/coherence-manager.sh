#!/bin/sh

# This will start a the coherence manager server

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

if [ ! -f ${COHERENCE_HOME}/bin/coherence-manager.sh ]; then
  echo "coherence-manager.sh: must be run from the Coherence installation directory."
  exit
fi

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
COHERENCE_PROPS=$COHERENCE_PROPS" -Dtangosol.coherence.session.localstorage=false"
COHERENCE_PROPS=$COHERENCE_PROPS" -Dtangosol.coherence.role=CoherenceManager"
COHERENCE_PROPS=$COHERENCE_PROPS" -Dtangosol.coherence.machine=CoherenceManager"
COHERENCE_PROPS=$COHERENCE_PROPS" -Dtangosol.coherence.member=CoherenceManager"
COHERENCE_PROPS=$COHERENCE_PROPS" -Dtangosol.coherence.cacheconfig=$COHERENCE_HOME/session-cache-config.xml"

JAVA_OPTS="-Xms$MEMORY -Xmx$MEMORY $JMXPROPERTIES $COHERENCE_PROPS"

#JAVA_OPTS="-Xms$MEMORY -Xmx$MEMORY"

$JAVAEXEC -server -showversion $JAVA_OPTS -jar $COHERENCE_HOME/CoherenceManager.jar 
