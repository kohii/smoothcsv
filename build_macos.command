cd `dirname $0`
set -eu

cd smoothcsv-launcher-mac
mvn clean package appbundle:bundle -Pmac