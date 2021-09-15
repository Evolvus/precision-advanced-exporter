export CLASSPATH=./lib/.
java -Djava.util.concurrent.ForkJoinPool.common.parallelism=20 -jar ./lib/advanced-exporter-0.0.1.jar $1 $2 $3 $4 $5
