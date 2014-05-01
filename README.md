SCUFL2
======

[![Build Status](https://travis-ci.org/myGrid/scufl2.svg?branch=master)](https://travis-ci.org/myGrid/scufl2)

See also the [SCUFL2 wiki][1]

(c) 2009-2014 University of Manchester, UK

Licensed under the [GNU Lesser General Public License (LGPL) 2.1][6]. 
See LICENSE.txt for the full terms of LGPL 2.1.

This is the [API][9], model and format of [SCUFL2][1], which replaces 
[Taverna][5]'s workflow format .t2flow. This API allows 
JVM applications to inspect, generate and modify Taverna workflow
definitions without depending on the Taverna runtime.

A new format, called [Scufl2 Workflow Bundle][7] is defined alongside this
API. This format can be inspected, generated and modified independently
of this API.

Note that the ability for Scufl2 API to read a workflow bundle (using
the `scufl2-rdfxml` module) does not guarantee it is valid or
structurally sound. The experimental modules `scufl2-validation-*` will
in the future be able to provide such verification.

For more information, see the [SCUFL2 API][9] pages, the
[Javadoc][10] and the [SCUFL2 examples][8].

Requisites
----------

* Java 1.6 or newer (tested with Java 1.7)
* Maven 2.2.2 or newer (for building, tested with Maven 3.0.5)


Building
--------

* `mvn clean install`

This will build each module and run their tests, producing JARs like
`scufl2-api/target/scufl2-api-0.14.0.jar`. 

First time you build Scufl2 this might download dependencies needed for
compliation. These have separate open source licenses, but should be
compatible with LGPL. None of the dependencies are neccessary for
using the compiled SCUFL2 API.

Some of the experimental modules are not built automatically, to build
them separately, run the same command from within their folder.



Usage
-----

Scufl2 is built as a Maven project, and the easiest way to use it is
from other Maven projects.

Typical users of the Scufl2 API will depend on the three modules
*scufl2-api*, *scufl2-t2flow* and *scufl2-rdfxml*. In your Maven
project's POM file, add this to your `<dependencies>` section:

		<dependency>
			<groupId>uk.org.taverna.scufl2</groupId>
			<artifactId>scufl2-api</artifactId>
			<version>0.14.0</version>
		</dependency>
		<dependency>
			<groupId>uk.org.taverna.scufl2</groupId>
			<artifactId>scufl2-rdfxml</artifactId>
			<version>0.14.0</version>
		</dependency>
		<dependency>
			<groupId>uk.org.taverna.scufl2</groupId>
			<artifactId>scufl2-t2flow</artifactId>
			<version>0.14.0</version>
		</dependency>

All Scufl2 modules are also valid OSGi bundles, see the OSGi section
below.  

You can alternatively copy and add the JARs from these modules to your
classpath:

* scufl2-api/target/scufl2-api-0.14.0.jar
* scufl2-rdfxml/target/scufl2-rdfxml-0.14.0.jar
* scufl2-t2flow/target/scufl2-t2flow-0.14.0.jar

See the [SCUFL2 Javadoc](http://mygrid.github.io/scufl2/api/0.14/) for 
documentation of classes and methods of SCUFL2. The package
[uk.o.rg.taverna.scufl2.api](http://mygrid.github.io/scufl2/api/0.14/uk/org/taverna/scufl2/api/package-summary.html)
is a good starting point.

See the *scufl2-validation* folder for examples of
usage. The best classes to start exploring would be
`uk.org.taverna.scufl2.api.io.WorkflowBundleIO` and
`uk.org.taverna.scufl2.api.container.WorkflowBundle`.

Example of converting .t2flow to .wfbundle:

    import uk.org.taverna.scufl2.api.container.WorkflowBundle;
    import uk.org.taverna.scufl2.api.io.ReaderException;
    import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
    import uk.org.taverna.scufl2.api.io.WriterException;

    // ..
    
    WorkflowBundleIO io = new WorkflowBundleIO();
    File t2File = new File("workflow.t2flow");
    File scufl2File = new File("workflow.wfbundle");
    WorkflowBundle wfBundle = io.readBundle(t2File, "application/vnd.taverna.t2flow+xml");
    io.writeBundle(wfBundle, scufl2File, "application/vnd.taverna.scufl2.workflow-bundle");

Check out the GitHub project scufl2-examples[8] for examples of using Scufl2, 
including the above code.

Supported file formats with WorkflowBundleIO and their required modules:

<table>
    <tr><th>Media type</th>  <th>Support</th> <th>JAR</th> <th>Description</th> </tr>
    <tr><td>application/vnd.taverna.t2flow+xml</td><td>read</td><td>scufl2-t2flow</td><td>Taverna 2 t2flow</td></tr>
    <tr><td>application/vnd.taverna.scufl2.workflow-bundle</td><td>read/write</td><td>scufl2-rdfxml</td><td>Taverna 3 workflow bundle</td></tr>
    <tr><td>application/vnd.taverna.scufl+xml</td><td>read</td><td>scufl2-scufl</td><td>Taverna 1 SCUFL (experimental)</td></tr>
    <tr><td>text/vnd.taverna.scufl2.structure</td><td>read/write</td><td>scufl2-api</td><td>Textual format for testing/debugging</td></tr>
    <tr><td>text/vnd.wf4ever.wfdesc+turtle</td><td>write</td><td><a href="https://github.com/wf4ever/scufl2-wfdesc">scufl2-wfdesc</a></td><td>Abstract workflow structure in <a href="http://www.w3.org/TR/turtle/">RDF Turtle</a> according to the <a href="http://purl.org/wf4ever/model#wfdesc">Wf4Ever wfdesc ontology</a></td></tr>
    <tr><td>text/vnd.mgrast.awe.awf+json</td><td>read</td><td><a href="https://github.com/stain/scufl2-awf">scufl2-awf</a></td> <td>Workflow definition of the MG-RAST <a href="https://github.com/MG-RAST/AWE">AWE</a> workflow engine. (experimental)</td></tr>
    <tr><td>application/vnd.shiwa.iwir+xml</td><td>read/write</td> <td><a href="https://github.com/stain/scufl2-iwir/">scufl2-iwir</a></td> <td><a href="http://www.shiwa-workflow.eu/">SHIWA</a>'s <a href="https://www.shiwa-workflow.eu/documents/10753/55350/IWIR+v1.1+Specification">IWIR</a> interoperabile workflow language (experimental)</td></tr>
    <tr><td>application/json</td> <td>write</td> <td><a href="https://github.com/myGrid/scufl2-examples">scufl2-examples</a></td> <td>Abstract workflow as JSON (experimental)</td></tr>
</table>


OSGi services
-------------
To use SCUFL2 from OSGi, use the following OSGi Services. Example, from
META-INF/spring/run-context.osgi.xml:

    <?xml version="1.0" encoding="UTF-8"?>
    <beans:beans xmlns="http://www.springframework.org/schema/osgi"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:beans="http://www.springframework.org/schema/beans"
                xsi:schemaLocation="http://www.springframework.org/schema/beans
                                    http://www.springframework.org/schema/beans/spring-beans.xsd
                                    http://www.springframework.org/schema/osgi
                                    http://www.springframework.org/schema/osgi/spring-osgi.xsd">

        <service ref="myService" interface="com.example.MyService"/>

        <reference id="workflowBundleIO" interface="uk.org.taverna.scufl2.api.io.WorkflowBundleIO" />

    </beans:beans>

And in run-context.xml:

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
                            http://www.springframework.org/schema/beans/spring-beans.xsd">

        <bean id="myService" class="com.example.impl.MyServiceImpl" >
                <property name="workflowBundleIO" ref="workflowBundleIO"/>
        </bean>

    </beans>

This will provide a WorkflowBundleIO instance with its readers and
writers loaded through OSGi, which when the bundles for scufl2-t2flow
and scufl2-rdfxml are loaded, would include support for the Taverna 2
t2flow format and the Taverna 3 wfbundle format.

Note that you do not need to use OSGi services to instantiate
Scufl2Tools or URITools, but may do so if you wish.


Modules
-------

Official modules:

* *scufl2-api* Java Beans for working with SCUFL2 
* *scufl2-t2flow* .t2flow import from Taverna 2
* *scufl2-rdfxml* .wfbundle import/export (RDF/XML)

Experimental modules:

* *scufl2-usecases* Example code covering [SCUFL2 use cases][4] (out of date)
* *scufl2-rdf* Pure RDF export/import (out of date)
* *scufl2-scufl* SCUFL 1 .xml import from Taverna 1
* *scufl2-validation* API for validating a Scufl2 workflow bundle
* *scufl2-validation-correctness* 
  Validate correctness of Scufl2 workflow definition
* *scufl2-validation-structural*
  Validate that a Scufl2 workflow definition is structurally sound
* *scufl2-validation-integration*
  Integration tests for scufl2-validation modules



[1]: http://www.mygrid.org.uk/dev/wiki/display/developer/SCUFL2
[2]: http://www.mygrid.org.uk/
[3]: http://www.mygrid.org.uk/dev/wiki/display/story/Dataflow+serialization
[4]: http://www.mygrid.org.uk/dev/wiki/display/developer/SCUFL2+use+cases
[5]: http://www.taverna.org.uk/
[6]: http://www.gnu.org/licenses/lgpl-2.1.html
[7]: http://www.mygrid.org.uk/dev/wiki/display/developer/Taverna+Workflow+Bundle
[8]: https://github.com/mygrid/scufl2-examples
[9]: http://www.mygrid.org.uk/dev/wiki/display/developer/SCUFL2+API
[10]: http://mygrid.github.io/scufl2/api/0.14/
