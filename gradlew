#!/bin/sh
#
# Gradle wrapper script for POSIX compatible shells
#
APP_HOME="$(cd "$(dirname "$0")" && pwd -P)"
GRADLE_WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

# Find Java
if [ -n "$JAVA_HOME" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

exec "$JAVACMD" -jar "$GRADLE_WRAPPER_JAR" "$@"
