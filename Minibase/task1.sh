#!/bin/sh

### if there is no available output file, create one
#mkdir -p data/minimization/output
#
### test all the pre-defined cases
mvn clean test

mvn clean compile assembly:single

## usage: bash run.sh (input_file_name under input folder).txt
java -cp target/minibase-1.0.0-jar-with-dependencies.jar ed.inf.adbs.minibase.CQMinimizer data/minimization/input/$1 data/minimization/output/$1
