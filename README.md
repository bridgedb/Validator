Void Validator

This is the Void Validator Stripped out of the BridgeDB project.

This includes RDF tools to store and query RDF. 
The tool also allows for reading in extra data if required.

The Validator uses the RDF tools to obtain its data.

There is a WebService to expose these services.

There is also a client to the webservice but that is mainly for testing/ examples to marshal and unmarshal the API.

Note:There is no longer any concept of Minimum or Direct.

If you have specific requirements please contact Christian!

Instalation:
This code can be built using Maven

All the dependencies are pulling in by Maven.

To run the webService just drop the war in the WebService Service package target directory into Tomcat.
We have tested apache-tomcat-7.0.29 but know of no reason similar WebServers would not work.

For configuration instructions please see:
https://github.com/openphacts/Validator/blob/master/RdfTools/resources/Config.txt


