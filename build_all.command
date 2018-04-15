cd `dirname $0`
set -eu

./build_icons.command
./build_common.command
./build_macos.command
./build_windows.command
