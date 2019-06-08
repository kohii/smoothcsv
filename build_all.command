cd `dirname $0`
set -eu

./build_icons.command

cd ../smoothcsv-rw
./gradlew install

cd ../smoothcsv
./gradlew clean build

