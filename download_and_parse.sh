#!/bin/bash
source ../commons/commons.sh
echo ">> Downloading & Parsing..."
./download.sh
checkResult $?;
./parse.sh
checkResult $?;
./list_change.sh
checkResult $?;
echo ">> Downloading & Parsing... DONE"
