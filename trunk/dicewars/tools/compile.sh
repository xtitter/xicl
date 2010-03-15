#!/bin/sh
export CLASSPATH="$CLASSPATH:./dicewars-client.jar:./players"
$JAVA_HOME/bin/javac -g ./players/$1
