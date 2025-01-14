
git clone https://github.com/unicode-org/icu.git
cd icu/icu4c/source
./runConfigureICU Linux/gcc

./runConfigureICU --help

export DESTDIR=~/test_mf2

make -j8
make dist
make install

cd $DESTDIR

gcc -I usr/local/include -std=c++17 TestMF2.cpp

LD_LIBRARY_PATH
