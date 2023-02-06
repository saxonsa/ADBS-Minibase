#!/bin/sh

mvn clean compile assembly:single

mkdir -p data/minimization/output

java -cp target/minibase-1.0.0-jar-with-dependencies.jar ed.inf.adbs.minibase.CQMinimizer data/minimization/input/$1 data/minimization/output/$1
