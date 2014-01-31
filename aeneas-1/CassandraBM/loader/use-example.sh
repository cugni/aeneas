echo -e "load-data conf/t1FxP.cm.xml test.txt\nquit\n"|\
java -Dclusterlocation=localhost:9160 \
  -Dqueuelength=10 \
  -Dnumberofthreads=10 \
  -Dwriteconsistency=ANY \
  -Xms5g -Xms2g \
  -Djava.net.preferIPv4Stack=true \
  -Dcom.sun.management.jmxremote.port=2626 \
  -Dcom.sun.management.jmxremote.ssl=false \
  -Dcom.sun.management.jmxremote.authenticate=false \
  -jar target/loader-0.0.4-SNAPSHOT-jar-with-dependencies.jar 

 
