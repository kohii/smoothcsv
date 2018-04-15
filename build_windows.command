cd `dirname $0`
set -eu

cd smoothcsv-launcher-win

mvn clean package -Dsmoothcsv.fileVersion="2.0.0.0"
