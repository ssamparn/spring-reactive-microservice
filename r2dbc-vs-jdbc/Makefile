package:
	mvn clean install

traditional-efficiency-test:
	java -Xmx6000m -jar ./traditional-jdbc/target/traditional-jdbc-1.0.0.jar --efficiency.test=true

reactive-efficiency-test:
	java -Xmx1000m -jar ./reactive-r2dbc/target/reactive-r2dbc-1.0.0.jar --efficiency.test=true

traditional-throughput-test:
	java -Xmx1000m -jar ./traditional-jdbc/target/traditional-jdbc-1.0.0.jar --throughput.test=true --useVirtualThreadExecutor=false

reactive-throughput-test:
	java -Xmx1000m -jar ./reactive-r2dbc/target/reactive-r2dbc-1.0.0.jar --throughput.test=true