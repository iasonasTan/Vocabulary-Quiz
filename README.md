# Vocabulary Helper
This app will help you memorize key-value pairs.

## How to build?
To build the project, execute:
```
./gradlew build # Linux
.\gradlew.bat build # Windows
```
and to run it:
```
./gradlew run # Linux
.\gradlew.bat run # Windows
```

## How to create a release?
I've made some scripts for windows and linux that automatically create a zip file that contains:
1. A minimal Java runtime
2. A minimal JavaFX instance
3. The application as jar
4. The required libraries (jars)
5. A run script (.bat or .sh)

To generate this zip, you need a JavaFX instance.
Just download a JavaFX zip for your OS and then execute:
```
./make-release.sh /path/to/jfx/instance/root # Linux
.\make-release.bat \path\to\jfx\instance\root # Windows
```
The zip will be generated.
