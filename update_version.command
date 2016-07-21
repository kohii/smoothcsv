cd `dirname $0`

mvn versions:set -DnewVersion=0.1.0-Beta
mvn versions:commit