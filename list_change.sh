#!/bin/bash
source ../commons/commons.sh
echo ">> Listing change...";
TARGET=$(cat "change_directory");
RESULT=$(git -C $TARGET status);
checkResult $? false;
RESULT=$(cat $RESULT | grep "res/raw");
checkResult $? false;
git -C $TARGET diff res/values/gtfs_rts_values_gen.xml
checkResult $?;
echo ">> Listing change... DONE";
