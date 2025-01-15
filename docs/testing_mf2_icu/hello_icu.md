# Hello `MessageFormat2`

We will assume that if you are interested in testing a pre-release Java library
you already have (or know how to install) a JDK, Apache Maven, git,
know how to create a project in your favorite IDE, and so on.


So this quick guide will not go into that.

## C++ Linux & macOS

Let's prepare a sandbox folder:

```sh
export ICU_SANDBOX=~/hello_icu_mf2
mkdir $ICU_SANDBOX
cd $ICU_SANDBOX
```

Build ICU4C (you only need to do this once):

```sh
git clone https://github.com/unicode-org/icu.git
pushd icu/icu4c/source

# Run this and choose the platform and toolchain you prefer
./runConfigureICU --help

# if macOS
./runConfigureICU macOS/gcc
# else if Linux  (gcc is just an example, there are 5 Linux options)
./runConfigureICU Linux/gcc
# end

export DESTDIR=$ICU_SANDBOX/icu_release
make -j8 releaseDist
popd
```

Create a minimal C++ file here (we are in the `$ICU_SANDBOX` folder). \
Let's call it `hello_mf2.cpp`.
The content is the one above (see the Linux section).

Build your application and run it:

```sh
g++ hello_mf2.cpp -I$DESTDIR/usr/local/include -std=c++17 -L$DESTDIR/usr/local/lib -licuuc -licudata -licui18n

# if macOS
DYLD_LIBRARY_PATH=$DESTDIR/usr/local/lib ./a.out
# else if Linux
LD_LIBRARY_PATH=$DESTDIR/usr/local/lib ./a.out
# end
```

This will output `"Hello John, today is January 13, 2025!"`

## C++ Windows with Visual Studio

### From command line

Start the Visual Studio "x64 Native Tools Command Prompt for VS 20xx"

Let's prepare a sandbox folder:

```cmd
set ICU_SANDBOX=%USERPROFILE%\hello_icu_mf2
md %ICU_SANDBOX%
cd %ICU_SANDBOX%
```

Build ICU4C (you only need to do this once):

```cmd
git clone https://github.com/unicode-org/icu.git

cd icu\icu4c
msbuild source/allinone/allinone.sln /p:Configuration=Release /p:Platform=x64 /p:SkipUWP=true
cd ..\..

set DESTDIR=%ICU_SANDBOX%\icu_release
rd /q/s %DESTDIR%
md %DESTDIR%
xcopy icu\icu4c\include %DESTDIR%\include /E /V /I /Q /Y
xcopy icu\icu4c\bin64   %DESTDIR%\bin64   /E /V /I /Q /Y
xcopy icu\icu4c\lib64   %DESTDIR%\lib64   /E /V /I /Q /Y
```

Create a minimal C++ file here (in the $ICU_SANDBOX folder). \
Let's call it `hello_mf2.cpp`:

```cpp
// hello_mf2.cpp
#include <iostream>

#include "unicode/utypes.h"
#include "unicode/calendar.h"
#include "unicode/errorcode.h"
#include "unicode/locid.h"
#include "unicode/messageformat2.h"

using namespace icu;

int main() {
    ErrorCode errorCode;
    UParseError parseError;

    icu::Calendar* cal(Calendar::createInstance(errorCode));
    cal->set(2025, Calendar::JANUARY, 28);
    UDate date = cal->getTime(errorCode);

    message2::MessageFormatter::Builder builder(errorCode);
    message2::MessageFormatter mf = builder
            .setPattern("Hello {$user}, today is {$now :date style=long}!", parseError, errorCode)
            .setLocale(Locale("en_US"))
            .build(errorCode);

    std::map<UnicodeString, message2::Formattable> argsBuilder;
    argsBuilder["user"] = message2::Formattable("John");
    argsBuilder["now"] = message2::Formattable::forDate(date);
    message2::MessageArguments arguments(argsBuilder, errorCode);

    icu::UnicodeString result = mf.formatToString(arguments, errorCode);
    std::string strResult;
    result.toUTF8String(strResult);
    std::cout << strResult << std::endl;
}
```

Build your application and run it:

```cmd
set DESTDIR=%ICU_SANDBOX%\icu_release
cl /std:c++17 /EHsc /I %DESTDIR%\include %DESTDIR%/lib64/*.lib hello_mf2.cpp
```

Run it:

```cmd
rem set PATH only once, not every time
set PATH=%DESTDIR%\bin64;%PATH%

.\hello_mf2.exe
```

This will output `"Hello John, today is January 13, 2025!"`

### From Visual Studio (UI)

Will do all the work in one "sanbox" folder, let's call it `hello_icu_mf2`.

Build ICU4C (you only need to do this once):

* Clone the ICU repository from
  https://github.com/unicode-org/icu.git to the `hello_icu_mf2` folder. \
  So we will have `hello_icu_mf2\icu`.
* Start Visual Studio.
* Select _"Open a project or solution"_
* Open the `allinone.sln` solution from the `hello_icu_mf2\icu\icu4c\source\allinone` folder.
* Select the "Build" -- "Configuration Manager" menu
* Change the active solution to "Release" and "x64" (or another architecture, but you will have to be consistent everywhere after this)
* Select the "Build" -- "Build solution" menu
* Select the "File" -- "Close solution" menu

Create a minimal C++ project in the `hello_icu_mf2` folder. \
Let's call it `hello_mf2`.

* You are still in Visual Studio. Select "Create a new project"
* Choose the project template "Console App" (tagged `C++`, `Windows`, `Console`)
* Click the "Next" button
* Set the "Project name" to "HelloMF2" and set the "Location" to the `hello_icu_mf2` folder. \
* Click "Create"
* A project will be created in the `hello_icu_mf2\HelloMF2` folder.

Create a macro pointing to the ICU folder with the files we built.

You can also downloaded a pre-build artifact from [GitHub releases](https://github.com/unicode-org/icu/releases/tag/release-76-1). \
But in that case the MessageFormat 2 implementation will be behind the spec.

* Select the "View" -- "Property Manager" menu
* In the new dialog select the root of the tree ("HelloMF2", not the Debug / Release leafs)
* Right click and select "Add New Project Property Sheet..."
* Let's call it `IcuPropertySheet.props` and click "Add"
* Open (double-click) `IcuPropertySheet` in any of the "leafs" of the tree on the left. \
  For example in `HelloMF2 / Release | 64 / IcuPropertySheet`
* Select "User Macros" under "Common Properties" (left tree)
* Click the "Add Macro" button
* Let's name it `IcuDistro` and set the "Value" to the `..\icu\icu4c` folder. \
  If the test project we created is not in `hello_icu_mf2`, next to `icu`, then you can use a full path to the ICU folder where you just did a built. \
  Or point it to one you downloaded (from the GitHub releases, see above).
* Click the "OK" button

Configure the project:

* Select `HelloMF2` in the left-side tree
* Select the "Project" -- "Properties" menu
* For "Configuration" (top-left) select "All Configurations"
* For "Platform" (top-right) select "All Platforms"
* In the left side tree:
  * "C/C++" / "General" set "Additional Include Directories" to
    `$(IcuDistro)\include;%(AdditionalIncludeDirectories)`
  * "C/C++" / "Language" set "C++ Language Standard" to
    `ISO C++17 Standard (/std:c++17)`
  * "Linker" / "General" set "Additional Dependencies" to \
    `icudt.lib;icuin.lib;icuuc.lib;$(CoreLibraryDependencies);%(AdditionalDependencies)`
  * "Linker" / "General" set "Additional Library Directories" to \
    `$(IcuDistro)/lib64;%(AdditionalLibraryDirectories)`
  * "Debugging" set "Environment" to \
    `PATH=$(IcuDistro)/bin64;%PATH%`
* For "Platform" (top-right) select "Win32"
* In the left side tree remove the `"64"` in two of the settings:
  * "Linker" / "General" set "Additional Library Directories" to \
    `$(IcuDistro)/lib;%(AdditionalLibraryDirectories)`
  * "Debugging" set "Environment" to \
    `PATH=$(IcuDistro)/bin;%PATH%`

Update the default source file to use some ICU functionality.

* Select the "View" -- "Solution Explorer" menu
* In the left-side tree "HelloMF2" / "Source Files" open `HelloMF2.cpp`
* Copy-paste the code from the `hello_mf2.cpp` (in this document) and paste it in
  `HelloMF2.cpp`, replacing all the existing content (select all + paste).

At this point you should be able to build and run the application, debug it, etc.

When run it will output `"Hello John, today is January 13, 2025!"`

### From Visual Studio with minimal work

This will use the released version of ICU4C (tested with 76.1) and
a "Hello ICU world" project already created.

So all you have to do is download them, open in Visual Studio, and start testig.

**Warning:** the MessageFormat 2 implementation will be behind the spec. \
It will be "mostly there", will small differences.

But this will be enough to get you started in no time.

**Steps:**

Download the Visual Studio artifacts from the
[official release of ICU 76.1](https://github.com/unicode-org/icu/releases/tag/release-76-1):
* [icu4c-76_1-Win32-MSVC2022.zip](https://github.com/unicode-org/icu/releases/download/release-76-1/icu4c-76_1-Win32-MSVC2022.zip)
* [icu4c-76_1-Win64-MSVC2022.zip](https://github.com/unicode-org/icu/releases/download/release-76-1/icu4c-76_1-Win64-MSVC2022.zip)

Download the "Hello ICU / MF2 World" project [from here](https://where.to.put.it). \
<span style="color:red">**!!! TDB Where to put it !!!**</span>

Unzip the files you just downloaded and merge the content of the two ICU folders.
The "hello world" project to be a sibling to the icu folder.

The folder tree structure should look like this:

```
someFolderOfYourChoice\
  +- HelloMF2\
  | \- HelloMF2\
  \- icu\
    \- icu4c\
      +- bin\
      +- bin64\
      +- include\
      | \- unicode\
      +- lib\
      \- lib64\
```

Open the `HelloMF2.sln` solution in Visual Studio and you are ready to go.

## Java

What you need:

* JDK, any version between 8 and 23
* Apache Maven

  ```sh
  mvn archetype:generate -DgroupId=com.mycompany.app -DartifactId=hello_icu_mf2 -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.5 -DinteractiveMode=false

  cd hello_icu_mf2
  ```

The project created as above uses the Java 17 version. \
If you are using a lower version then change `<maven.compiler.release>` property to whatever Java version you are using.

  ```sh
  # Example on how to set the Java version to 11
  mvn versions:set-property -Dproperty=maven.compiler.release -DnewVersion=11
  ```

Edit the `pom.xml` file and:

* Add this to `<dependencies>`: \

  ```xml
  <dependency>
    <groupId>com.ibm.icu</groupId>
    <artifactId>icu4j</artifactId>
    <version>77.0.1-SNAPSHOT</version>
  </dependency>
  ```

**Warning:** make sure it is done in `dependencies`, not in `dependencyManagement / dependencies`

Edit the `src/test/java/com/mycompany/app/AppTest.java` file:

* Add a new test method:

  ```java
  @Test
  public void testMessageFormat2() {
      MessageFormatter mf2 = MessageFormatter.builder()
              .setLocale(Locale.US)
              .setPattern("Hello {$user}, today is {$now :date style=long}!")
              .build();
      Calendar cal = Calendar.getInstance();
      cal.set(2025, 0, 28);

      Map<String, Object> arguments = new HashMap<>();
      arguments.put("user", "John");
      arguments.put("now", cal);
      System.out.println(mf2.formatToString(arguments));
  }
  ```

* Add imports:

  ```java
  import java.util.Date;
  import java.util.HashMap;
  import java.util.Locale;
  import java.util.Map;
  import com.ibm.icu.message2.MessageFormatter;
  ```

Now run the tests:

```sh
mvn package -q
```

This will output `"Hello John, today is January 13, 2025!"`
