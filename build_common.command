cd `dirname $0`
set -eu

cd ../smoothcsv-rw
mvn clean install

cd ../smoothcsv-commons
mvn clean install

cd ../smoothcsv-swing
mvn clean install

cd ../smoothcsv
mvn clean install -pl "!smoothcsv-launcher-mac,!smoothcsv-debuglauncher-mac,!smoothcsv-launcher-win"
