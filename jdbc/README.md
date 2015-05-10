## Jdbc api ##
jclouds-jdbc is a storage api for relational databases. It is implemented using Guice Persist and JPA, so the user must
provide an entity manager and a persistence.xml file with the required data source. An example of the persistence.xml can be
found [here](https://github.com/jclouds/jclouds-labs/blob/master/jdbc/src/test/resources/META-INF/persistence.xml).

## Running the tests ##
Jdbc tests set up an embedded database and run the tests against it. To run the tests you can use this command.
```
mvn test
```
You can also run the integration tests with
```
mvn integration-test
```
