#!/usr/bin/env sh

echo "- - - - - - - - - -  start build apk - - - - - - - - - - "
cd ../../

checkJava () {
    java -version
    echo $?
    if [ $? -eq 0 ]; then
        echo "Java installed."
        checkGradle
    else
        echo "Environment Error. Java not installed."
    fi
}

checkGradle (){
    gradle -v
    if [ $? -eq 0 ]; then
        echo "Gradle installed."
        buildApk
    else
        echo "Environment Error. Java not installed."
    fi
}

buildApk () {
    ./gradlew build

    if [ $? -eq 0 ]; then
        echo "Gradlew build success."
        echo "- - - - - - - - - -  end build apk - - - - - - - - - - "
    else
        echo "Environment Error. Use Android Studio open this project and install missed packages."
    fi
}

checkJava