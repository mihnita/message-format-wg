# MessageFormat 2 "Hello World!" in 5 minutes - Java (ICU4J)

We will assume that if you are interested in testing a pre-release Java library
you are already have (or know how to install) a JDK, Apache Maven, git,
with how to create a project in your favorite IDE, and so on.

### Option 1: Using Maven artifacts, with a the SNAPHSOT version

What you need:

* JDK, any version between 8 and 23
* Apache Maven

  ```sh
  mvn archetype:generate -DgroupId=com.mycompany.app -DartifactId=mf2_test_mvn -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.5 -DinteractiveMode=false

  cd mf2_test_mvn
  ```

Edit the `pom.xml` file and:

* The project created as above uses the Java 17 version. \
  If you are using a lower version then change `<maven.compiler.release>` in the `<properties>`) to whatever Java version you are using.
* Add this to `<dependencies>`: \

  ```xml
  <dependency>
    <groupId>com.ibm.icu</groupId>
    <artifactId>icu4j</artifactId>
    <version>77.0.1-SNAPSHOT</version>
  </dependency>
  ```

* Change the `src/test/java/com/mycompany/app/AppTest.java` file:

  * Add imports: \

    ```java
    import java.util.Date;
    import java.util.HashMap;
    import java.util.Locale;
    import java.util.Map;
    import com.ibm.icu.message2.MessageFormatter;
    ```

  * Add a new test method: \

    ```java
    @Test
    public void testMessageFormat2() {
      MessageFormatter mf2 = MessageFormatter.builder()
          .setLocale(Locale.US)
          .setPattern("Hello {$user}, today is {$now :date year=numeric month=long day=numeric}!")
          .build();
      Map<String, Object> arguments = new HashMap<>();
      arguments.put("user", "John");
      arguments.put("now", new Date());
      System.out.println(mf2.formatToString(arguments));
    }
    ```

Now run the tests:

```sh
mvn package -q
```

This will output _"Hello John, today is January 13, 2025!"_

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

This will output _"Hello John, today is January 13, 2025!"_

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

This will output _"Hello John, today is January 13, 2025!"_
