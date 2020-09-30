#!/bin/sh
for request in "Hypertext_Transfer_Protocol" "Document_Object_Model" "CSS"; do
  echo "Checking request $request"

  java -jar main-module/build/libs/main-module.jar $request
  for plugin in "CounterPlugin" "FrequencyDictionaryPlugin"; do
    if ! diff --ignore-all-space \
      --ignore-case \
      integration-test/results/$request/results-$plugin.txt results/$request/results-$plugin.txt; then
      exit 1
    fi
  done
done