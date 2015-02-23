#Shipyard provider for jclouds
- jclouds-shipyard allows one to connect and administer multiple docker daemons from a single endpoint.

--------------

#Setup

- Notes on how to standup a Shipyard instance can be found here:

	https://github.com/shipyard/shipyard
	
- More detailed information (docs, API, etc..) can be found on their site:

	http://shipyard-project.com/

--------------

#Notes
- jclouds-shipyard is still at alpha stage please report any issues you find at [jclouds issues](https://issues.apache.org/jira/browse/JCLOUDS)

--------------

#Testing
- To run integration tests we need a valid Shipyard instance/endpoint, identity-key, and docker daemon to use. Testing, for now, assumes the docker daemon does not have ssl enabled. To run integration tests you could do something like:

	$> mvn clean install -Plive -Dtest.shipyard.endpoint=http://10.0.0.8:8080 -Dtest.shipyard.identity=zEswusMbqMR8D7QA0yJbIc1CxGYqfLAG5bZO -Dtest.shipyard.docker.endpoint=http://10.0.0.8:2375
