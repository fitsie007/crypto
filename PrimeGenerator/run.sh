#!/bin/bash

totalArgs=$#
args=$@
if [ "$totalArgs" -gt 1 ]; then
	java -classpath . GeneratePrimes $1 $2
else
	echo  "Format for $0 is ' $0 <n confidence> '"
fi