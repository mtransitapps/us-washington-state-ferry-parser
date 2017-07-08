#!/bin/bash
source ../commons/commons.sh
echo ">> Downloading..."
URL=`cat input_url`;
FILENAME=$(basename "$URL");
if [ -e input/gtfs.zip ]; then
    mv input/gtfs.zip $FILENAME;
    checkResult $?;
    wget --header="User-Agent: MonTransit" --timeout=60 --tries=6 -N $URL;
    checkResult $?;
else
    wget --header="User-Agent: MonTransit" --timeout=60 --tries=6 -S $URL;
    checkResult $?;
fi;
# DEBUG
DEBUG=${pwd}; # DEBUG
echo "DEBUG ---------------------------------";
echo "DEBUG > pwd:"; # DEBUG
pwd;
echo "DEBUG > ls -al:"; # DEBUG
ls -al;
echo "DEBUG ---------------------------------";
# DEBUG
if [ -e $FILENAME ]; then
    mv $FILENAME input/gtfs.zip;
    checkResult $?;
fi;
echo ">> Downloading... DONE"
