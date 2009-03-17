#! /bin/bash

HOME="$(cd "${0%/*}/.." && pwd)"

# Source our environment setup script
. $HOME/bin/env.sh

if [ $# -lt 1 ] ; then
   echo "PEP Test Request"
   echo "Usage:  testreg <CONF_FILE>"
   exit 0
fi

JVMOPTS="-Dorg.glite.authz.pep.home=$HOME $JVMOPTS"

$JAVACMD $JVMOPTS 'org.glite.authz.pep.client.PEPCLI' $@