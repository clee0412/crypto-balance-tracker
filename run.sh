#!/bin/bash
  export JAVA_HOME=/opt/homebrew/Cellar/openjdk@21/21.0.7/libexec/openjdk.jdk/Contents/Home
  set -a
  source .env
  set +a
  ./gradlew bootRun