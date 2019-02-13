cd `dirname $0`
set -eu

cd ../smoothcsv-rw
./gradlew install

cd ../smoothcsv
./gradlew build
