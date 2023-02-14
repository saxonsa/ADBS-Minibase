mvn clean compile assembly:single

java -cp target/minibase-1.0.0-jar-with-dependencies.jar ed.inf.adbs.minibase.Minibase data/evaluation/db data/evaluation/input/$1.txt data/evaluation/output/$1.csv