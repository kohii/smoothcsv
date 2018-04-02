cd `dirname $0`

cd ..

cd ../smoothcsv-rw
mvn clean install

cd ../smoothcsv-commons
mvn clean install

cd ../smoothcsv-swing
mvn clean install

cd ../smoothcsv
mvn clean install

cd smoothcsv-launcher-win
mvn package -Dsmoothcsv.fileVersion=2.0.0.0