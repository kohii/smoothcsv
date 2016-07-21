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
mvn package -Dsmoothcsv.fileVersion=0.1.0.1