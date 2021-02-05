#!/bin/bash

C=$1
I=$2
J=$3

java -Xmx12g -cp ../sdg-cli/target/classes:../sdg-core/target/classes:"$HOME"/.m2/repository/commons-cli/commons-cli/1.4/commons-cli-1.4.jar:"$HOME"/.m2/repository/com/github/javaparser/javaparser-core/3.17.0/javaparser-core-3.17.0.jar:"$HOME"/.m2/repository/com/github/javaparser/javaparser-symbol-solver-core/3.17.0/javaparser-symbol-solver-core-3.17.0.jar:"$HOME"/.m2/repository/org/javassist/javassist/3.27.0-GA/javassist-3.27.0-GA.jar:"$HOME"/.m2/repository/com/google/guava/guava/30.0-jre/guava-30.0-jre.jar:"$HOME"/.m2/repository/com/google/guava/failureaccess/1.0.1/failureaccess-1.0.1.jar:"$HOME"/.m2/repository/com/google/guava/listenablefuture/9999.0-empty-to-avoid-conflict-with-guava/listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar:"$HOME"/.m2/repository/com/google/code/findbugs/jsr305/3.0.2/jsr305-3.0.2.jar:"$HOME"/.m2/repository/org/checkerframework/checker-qual/3.5.0/checker-qual-3.5.0.jar:"$HOME"/.m2/repository/com/google/errorprone/error_prone_annotations/2.3.4/error_prone_annotations-2.3.4.jar:"$HOME"/.m2/repository/com/google/j2objc/j2objc-annotations/1.3/j2objc-annotations-1.3.jar:"$HOME"/.m2/repository/org/jgrapht/jgrapht-core/1.5.0/jgrapht-core-1.5.0.jar:"$HOME"/.m2/repository/org/jheaps/jheaps/0.13/jheaps-0.13.jar:"$HOME"/.m2/repository/org/jgrapht/jgrapht-io/1.5.0/jgrapht-io-1.5.0.jar:"$HOME"/.m2/repository/org/antlr/antlr4-runtime/4.8-1/antlr4-runtime-4.8-1.jar:"$HOME"/.m2/repository/org/apache/commons/commons-text/1.8/commons-text-1.8.jar:"$HOME"/.m2/repository/org/apache/commons/commons-lang3/3.9/commons-lang3-3.9.jar \
  es.upv.mist.slicing.cli.Slicer \
  -c "$C" \
  -g \
  -i "$I" \
  -j "$J" \
  -o ../slice \
  -t SDG
