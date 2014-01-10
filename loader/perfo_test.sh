 echo INFO: Starting testing about queue length $(date) >results_test_perfo
 for len in  10 100 1000 10000;
 do
  ssh -f -L 9160:localhost:29160 bscgrid20 sleep 100
  echo INFO: QTEST length $len loading started at $(date) >>results_test_perfo
  echo -e "load-data conf/t1FxP.cm.xml bigfile.txt\nquit\n"|\
  java -Dclusterlocation=localhost:9160 \
  -Dqueuelength=$len \
  -Dnumberofthreads=20 \
  -Dwriteconsistency=ANY \
  -Xms5g -Xms2g \
  -Djava.net.preferIPv4Stack=true \
  -Dcom.sun.management.jmxremote.port=2626 \
  -Dcom.sun.management.jmxremote.ssl=false \
  -Dcom.sun.management.jmxremote.authenticate=false \
  -jar target/loader-0.0.4-SNAPSHOT-jar-with-dependencies.jar >>results_test_perfo
  echo INFO: QTEST length $len terminated at $(date) >>results_test_perfo
done

 echo Starting mixed testing $(date) >>results_test_perfo
 
 for thread in  10 100 1000 10000;
 do
  ssh -f -L 9160:localhost:29160 bscgrid20 sleep 100
  echo INFO: TTEST nthreads $thread loading started at $(date) >>results_test_perfo
  echo -e "load-data conf/t1FxP.cm.xml bigfile.txt\nquit\n"|\
  java -Dclusterlocation=localhost:9160 \
  -Dqueuelength=$thread \
  -Dnumberofthreads=$thread \
  -Dwriteconsistency=ANY \
  -Xms5g -Xms2g \
  -Djava.net.preferIPv4Stack=true \
  -Dcom.sun.management.jmxremote.port=2626 \
  -Dcom.sun.management.jmxremote.ssl=false \
  -Dcom.sun.management.jmxremote.authenticate=false \
  -jar target/loader-0.0.4-SNAPSHOT-jar-with-dependencies.jar >> results_test_perfo
  echo INFO: TTEST nthreads $thread terminated at $(date) >>results_test_perfo
done
