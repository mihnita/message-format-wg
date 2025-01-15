## Java

### Option 1: Using Maven artifacts, with a the SNAPHSOT version

SEE MAIN FILE

### Option 2: Build your own ICU4J

This has the benefit that you can track the progress and test the implementation
as it is getting closer and closer to finalization.

What you need:

* git
* JDK, any version between 8 and 23
* Apache Maven

```sh
git clone https://github.com/unicode-org/icu.git

cd icu/icu4j
mvn package -DskipTests -DskipITs
cd ../..

# Windows
md mf2_test
copy icu\icu4j\main\icu4j\target\icu4j-77.0.1-SNAPSHOT.jar mf2_test
# end Windows

# Linux, macOS
mkdir mf2_test
cp icu/icu4j/main/icu4j/target/icu4j-77.0.1-SNAPSHOT.jar mf2_test
# end Linux, macOS

cd mf2_test
javac -cp icu4j-77.0.1-SNAPSHOT.jar HelloMF2.java

# Windows
java -cp .;icu4j-77.0.1-SNAPSHOT.jar HelloMF2
# end Windows

# Linux, macOS
java -cp .:icu4j-77.0.1-SNAPSHOT.jar HelloMF2
# end Linux, macOS
```

This will output `"Hello John, today is January 13, 2025!"`

### Option 3: Download the prebuilt .jar files

What you need:

* JDK, any version between 8 and 23

Download from [here](http://www.sample.com) and unzip the `icu4j-binaries.zip` archive.
Copy `icu4j-77.0.1-SNAPSHOT.jar` in the same folder where the `HelloMF2.java` file is.

Compile and run:

```sh
javac -cp icu4j-77.0.1-SNAPSHOT.jar HelloMF2.java

# Windows
java -cp .;icu4j-77.0.1-SNAPSHOT.jar HelloMF2
# end Windows

# Linux, macOS
java -cp .:icu4j-77.0.1-SNAPSHOT.jar HelloMF2
# end Linux, macOS
```

This will output `"Hello John, today is January 13, 2025!"`
