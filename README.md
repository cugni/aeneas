<img align="center" src="http://i.imgur.com/btdQJC2.jpg" title="Aeneas: An Extensible NoSql Enhancing Application System" />
Aeneas
======

Non-relational databases arise as a solution to solve the scalability problems of relational databases when dealing with big data applications. Since they are highly configurable, their performance is heavily affected by user decisions.To maximize performance, many different data models and queries must be analyzed, in order to then choose the best fit. This requires performing a wide range of test which can cause **productivity issues**.

We present **Aeneas**, a tool to support the design of data management code for applications using non-relational databases. Aeneas provides an easy and fast methodology to support the decision about how to organize and retrieve data in order to improve the performance.

The main goal of Aeneas is to offer a methodology to increase the productivity of the evaluation process, which will lead the user to get the best configuration parameters and data model in order to access data.
This goal is achieved through the three modules: 
 
* **Loader**
* **Workloader**
* **Node Agent**

The **Loader** abstracts the insertion  of data: it allows to  easily  reorganize and marshall information  in order to try different  configurations. At the same time,  the **Workloader** allows to tests  different types of  workloads (e.g one with a uniform distribution or ZipFian etc..). Finally, the **Node Agent** has a double role: firstly it  samples each node of the cluster getting many performance metrics (db's internal, SO' status, hard disks ..) and then it allows the user to easily **analyse** and **transform** the results through a  handy **web interface**.

##Aeneas' structure
 <img src="http://i.imgur.com/9LQOTKx.png" title="Aeneas' structure" />
Branches
=========
At the moment, Aeneas exists in two versions: the **first** is the one which is currently  used in production for supporting research. 

#Aeneas #1
It primarily relies on the Apache Cassandra Database. In the repository is provided both the core structure than a practical use case of its use.

The package structure is the following:

	.
	+-- CassandraBM
	|   +-- cassandra-datamodel  		# New internal Cassandra types and partitioners 
	|   +-- cassandra-metric-recorder   # a library to store in Cassandra metrics for any application
	|   +-- codeGenerator				# static java queries' code generator (INSTABLE) 
	|   +-- commons
	|   +-- loader						# the loader API: cli, source reading and inserting
	|   +-- model						# internal model representation
	|   +-- node-agent					# Node-agent daemon
	|   +-- workloader					# Workloader API: workload types and keys generation
	+-- bscproj1
	|   +-- bsc01-loader-impl          # use case loader implementation
	|   +-- bsc01-workloader          	# use case workloader implementation


###Installation 

Aeneas relies on a modified version of the Hector client in order to fix some incompatibility between the Integer serialization between Java and Cassandra.
Here, how to install it: 
	
	git clone https://github.com/cugni/hector
	cd hector
	mvn -DskipTests install 

Now we can clone Aeneas and install it

	git clone https://github.com/cugni/aeneas
	cd aeneas-1/CassandraBM
	mvn install	
	cd ../bscproj1
	mvn install
	
 *\*Some tests require a running instance of Cassandra on localhost; you can avoid it using the comand mvn -DskipTests install*

*\*\*Note that the installation on OSX may fail: The class JMXCassandraReader at the lines 145 and 148 requires the Oracle JVM instead of the Apple one. As a workaround,  you can both change JVM or comment those lines.*
 
 Now you can find in your local Maven repository all the required artifacts. 
 
###Building
#####NodeAgent

These are the comands for edit the configuration file and build an executable jar file with all the dependencies of the **NodeAgent**:

	cd CassandraBM/node-agent
	# edit this file to change the default paraments (e.g. cluster address etc..)
	nano src/main/resources/META-INF/nodeagent-config.xml 
	mvn package
	# lunching the Node Agent
	java -jar target/node-agent-1.0-SNAPSHOT-executable.jar 

Now we can start an instance of the NodeAgent in each Cassandra's node and control them remotely via HTTP requests.


The basic comands are:

**Init** : configures which  Cassandra's Column Family have to be sampled
	
	curl http://n1.example.com:2626/q/init?column=keyspace:columnFamily
	
**Start**: starts the sampling of metrics in the context of a specific test
	
	curl http://n1.example.com:2626/q/start?testname=example

**Stop**: interrupts the sampling
	
	curl http://n1.example.com:2626/q/start?testname=example
	


#####Loader and workloader

Both the  loader  and workloader modules contain only the  main logic and the interfaces which have to be implemented in order to automize the loading and the testing. The package **bscproj1** provides two implementation samples for trying those functionalities.

	cd bscproj1/bsc01-loader-impl/
	mvn assembly:assembly
	
	# This command starts to load the data contained in test.txt
	# according to the model description of the file conf/FidXAid.cm.xml
	echo -e "load-data conf/FidXAid.cm.xml test.txt \n quit \n"| \ # you can type this command also via cli
	java -Dclusterlocation="minerva-5:29160" \ #entry point of the cluster
 		-Dqueuelength=5000 \   # queue size for batching the requests
 		-Dclustername=AeneasTesting \ 
 		-Dnumberofthreads=50 \ # concurrent insertions
        -jar target/bsc01loaderimpl-1.0-SNAPSHOT-jar-with-dependencies.jar 
	

Once loaded the data into the cluster is time to test different query types:

	cd bscproj1/bsc01-workloader/
	mvn assembly:assembly
	
	
###Run an experiment
Once loaded the data into the cluster and built the workloader we can start a test with the following script
	
	# We suppose to have a list of all the servers into the variable $servers
	
	#let's init the NodeAgents
	for i in $servers; do		
		curl http://$i:2626/q/init?column=FixXAid:points
	done
	
	#let's start the test!
	
	for i in $servers; do		
		curl http://$i:2626/q/start?testname=prova
	done
	
	java -Dmodel=FidXAid \
		 -Ddistribution=UNIFORM \ 		# Which statistical distribution use for generate the keys?
		 -Dreadconsistency=ONE \		
		 -Dtestname=prova \
		 -Dclusterlocation=somewhere:9160 \
		 -Dreportingserverlocation=somewhereelse:9160 \
		 -Dreportingserverclustername=AeneasMetrics \
 		 -Dntest=100 \  				# Number of times the tests are repeated
		 -Dconcurrency=10 \ 			# Concurrent requests
		 -Dcontinueonerror=false \    	# if there is an error, do you wish to stop the test?
		 -Dcontinueontimeout=false \	# if there is an timeout, do you wish to stop the test?
		 -jar target/bsc01workloader-1.0-SNAPSHOT-jar-with-dependencies.jar 


Now that your experiment is done you can connect to any of the NodeAgents in your cluster (e.g. http://Now that your experiment is done you can connect to any of the NodeAgents in your cluster (e.g. http://n1.example.com:2626/) with any browser (but better chrome) and retrieve, print and manipulate your data. Enjoy!
 

	
 
#Aeneas #2


The second version is currently under developing. It has been heavily refactored in order to make Aeneas easily extensible to "*any*" kind of data store always keeping a  unique abstract data representation. The re-factory aims also to allow Aeneas to be exposed to clients via a standard REST interface. The  **Node Agent** web interface is also changing in order to provide to the user a powerful workbench where retrieve data, manipulate it through scripts and stored modified versions.

#Further reading

Aeneas has been presented at the 2013 International Conference on Computational Science in a paper titled "Aeneas: A Tool to Enable Applications to Effectively Use Non-relational Databases". The paper can be downloaded from  [http://www.sciencedirect.com/science/article/pii/S187705091300584X](http://www.sciencedirect.com/science/article/pii/S187705091300584X).

At [http://www.slideshare.net/CesareCugnasco/aeneas-an-extensible-nosql-enhancing-application-system](http://www.slideshare.net/CesareCugnasco/aeneas-an-extensible-nosql-enhancing-application-system) there are the slides made for a seminary held at the Universitat Politècnica de Catalunya in November 2013.


#Acknowledgements

This work is partially supported by the **BSC-CNS Severo Ochoa** program and the TIN2012-34557 project, with funding from the **Spanish Ministry of Economy and Competitivity** and the European Union’s **FEDER founds**.


#License

Copyright 2014  Barcelona Supercomputing Center

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

The framework relies on libraries which may have stricter licences. For instance, the Node Agent (version 1) uses the library [HighCharts](http://www.highcharts.com/) which is released with an [Attribution-NonCommercial 3.0 Unported Licence](http://creativecommons.org/licenses/by-nc/3.0/).
