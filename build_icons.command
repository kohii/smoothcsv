set -eu
cd `dirname $0`

export PATH=/Applications/Sketch.app/Contents/Resources/sketchtool/bin:$PATH

WORK_DIR=icon_work
if [ -d "$WORK_DIR" ]; then
  rm -rf "$WORK_DIR"
fi

mkdir "$WORK_DIR"
cd "$WORK_DIR"

mkdir icons
sketchtool export slices ../smoothcsv_icon.sketch  --output=icons

# common
cp icons/* ../smoothcsv-app-modules/smoothcsv-core/src/main/resources/img/app/

# macOS
mkdir SmoothCSV.iconset
cp icons/* SmoothCSV.iconset/
iconutil -c icns SmoothCSV.iconset
mv SmoothCSV.icns ../smoothcsv-launcher-mac/

# Windows
convert icons/icon_512x512.png -define icon:auto-resize app.ico
cp app.ico ../smoothcsv-launcher-win/src/main/resources/