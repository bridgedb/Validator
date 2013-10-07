Void Validator

This is the Void Validator Stripped out of the BridgeDB project.

This includes RDF tools to store and query RDF. 
The tool also allows for reading in extra data if required.

The Validator uses the RDF tools to obtain its data.

There is a WebService to expose these services.

There is also a client to the webservice but that is mainly for testing/ examples to marshal and unmarshal the API.

Note:There is no longer any concept of Minimum or Direct.

If you have specific requirements please contact Christian!

Configuration 
-------------
Properties File Location:
========================
Both BridgeDB and the Validator look for the configuration files in the following locations. 
Once it finds a configuration file the other locations are ignored. 
* Directly in the run directory  (Mainly for java *.jar runs)
* Environment Variable OPS_IMS_CONFIG: can be used to point to any location
* Tomcat configuration folder: $CATALINA_HOME/conf/OPS-IMS
* ../conf/OPS-IMS                  #Allows tomcat to pick up $CATALINA_HOME/conf/OPS-IMS even if it can not get $CATALINA_HOME
* Using classLoader getResource. This will pick up the files included in Jars and Wars.

======================
The configuration files used (and described below are)
validator.properties
MetaDataSpecifications.properties
log4j.properties (can be shared with other projects)
local.properties (can be shared with other projects)
VoidInfo.owl
simpleOntology.owl

======================
local.properties

This is the recommended place to overwrite individual property values of any other *.property file.

Local properties will overwrite values with the same key in any other properties file.
Properties not overwritten in local will keep their original values.

To install local properties you need to.
1. Create a local.properties file
2. Store it in a high priority location as described above
3. Copy the keys from the original file 
4. Addition Key values pairs added to the original properties file will not be blocked by a local.properties file

To remove all properties from a properties file you will need to.
1. Create a copy of that file in a high priority location as described above
2. Edit your copy of that properties file
3. Any additions to the original properties file will have to be manual added as required

======================
validator.properties 
------------
(Included in the build and can be found at rdf-tools/resources)

This file contain the local setup information which MUST be checked and ideally overwritten in local.properties to match the specific local setup.

The default configuration validator.properties file can be found at
	$VALIDATOR_HOME/RdfTools/resources/validator.properties
		
You must either supply local.properties values matching your local setup 
or setup your rdf directories (including permissions!) to use the defaults. 

To load Data from password protected services you MUST overwrite the values in the Accounts section. 
The example values here are NOT correct for security reasons.

MetaDataSpecifications.properties
---------------------------------
(Included in the build and can be found at metadata/resources)

This file list the specification that are loaded.

The default file includes a test specification that us currently required for unit tests including the client tests.

The default file also included the OpenPhacts specifications.

To remove either or both the test or OpenPhacts specifications please provide a local version of MetaDataSpecifications.properties.
See Properties File Location section for where to place this,

The file referred to in MetaDataSpecifications.properties are without path so looked for as described in Properties File Location section.

VoidInfo.owl and simpleOntology.owl
------------------------------------
These are the files pointed to by the default MetaDataSpecifications.properties

An alternative version of these files can be provided by using a higher priority location as discussed in Properties File Location section.
However this is NOT recommended as these files are subject to change and these changes would be lost.

Instead please include improvements directly in the source file.

Instalation
------------
This code can be built using Maven
There is no ant build nor plans to write one.

All the dependencies are pulling in by Maven.

To run the webService just drop the war in the WebService Service package target directory into Tomcat.
We have tested apache-tomcat-7.0.29 but know of no reason similar WebServers would not work.



