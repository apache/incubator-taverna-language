<!--
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
-->
# SCUFL2

SCUFL2 API is part of [Apache Taverna Language]().


This is the [API][9], model and format of [SCUFL2][1], which is the
workflow format of [Apache Taverna](http://taverna.incubator.apache.org/).
SCUFL2 replaces the `.t2flow` format of Taverna 2.
This API allows JVM applications to inspect, generate and modify 
Apache Taverna workflow definitions without depending on the Apache Taverna
runtime.

The format [Scufl2 Workflow Bundle][7] is defined alongside this
API. This format can be inspected, generated and modified independently
of this API.

Note that the ability for Scufl2 API to read a workflow bundle (using
the `scufl2-wfbundle` module) does not guarantee it is valid or
structurally sound. The 

For more information, see the [SCUFL2 API][9] pages, the
[Javadoc][10] and the [SCUFL2 examples][8].





Usage
-----

See the [Apache Taverna Language](../) README for details on building and 
using Apache Taverna Language using Maven.  You will typically want to also use
at least [taverna-scufl2-wfbundle](../taverna-scufl2-wfbundle) to support
loading and saving of SCUFL2 workflows in the `.wfbundle` format.

All Scufl2 modules are also valid OSGi bundles, see the [OSGi
section](#OSGI_services) below.  

See the [Taverna Language Javadoc][10] for documentation of classes and methods
of SCUFL2. The package
[org.apache.taverna.scufl2.api](http://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/scufl2/api/package-summary.html)
is a good starting point.

See the folder [taverna-scufl2-examples](../taverna-scufl2-examples/) 
for examples of usage. The best classes to start using would be
`org.apache.taverna.scufl2.api.io.WorkflowBundleIO` and
`org.apache.taverna.scufl2.api.container.WorkflowBundle`.

Example of converting `.t2flow` to `.wfbundle`:

    import org.apache.taverna.scufl2.api.container.WorkflowBundle;
    import org.apache.taverna.scufl2.api.io.ReaderException;
    import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
    import org.apache.taverna.scufl2.api.io.WriterException;

    // ..
    
    WorkflowBundleIO io = new WorkflowBundleIO();
    File t2File = new File("workflow.t2flow");
    File scufl2File = new File("workflow.wfbundle");
    WorkflowBundle wfBundle = io.readBundle(t2File, "application/vnd.taverna.t2flow+xml");
    io.writeBundle(wfBundle, scufl2File, "application/vnd.taverna.scufl2.workflow-bundle");

Supported file formats with `WorkflowBundleIO` and their required modules:

<table>
    <tr><th>Media type</th>  <th>Support</th> <th>JAR</th> <th>Description</th> </tr>
    <tr><td>application/vnd.taverna.t2flow+xml</td><td>read</td><td>taverna-scufl2-t2flow</td><td>Taverna 2 t2flow</td></tr>
    <tr><td>application/vnd.taverna.scufl2.workflow-bundle</td><td>read/write</td><td>taverna-scufl2-wfbundle</td><td>Taverna 3 workflow bundle</td></tr>
    <tr><td>application/vnd.taverna.scufl+xml</td><td>read</td><td>taverna-scufl2-scufl</td><td>Taverna 1 SCUFL (experimental)</td></tr>
    <tr><td>text/vnd.taverna.scufl2.structure</td><td>read/write</td><td>taverna-scufl2-api</td><td>Textual format for testing/debugging</td></tr>
    <tr><td>text/vnd.wf4ever.wfdesc+turtle</td><td>write</td><td><a href="https://github.com/wf4ever/scufl2-wfdesc">taverna-scufl2-wfdesc</a></td><td>Abstract workflow structure in <a href="http://www.w3.org/TR/turtle/">RDF Turtle</a> according to the <a href="http://purl.org/wf4ever/model#wfdesc">Wf4Ever wfdesc ontology</a></td></tr>
    <tr><td>text/vnd.mgrast.awe.awf+json</td><td>read</td><td><a href="https://github.com/stain/scufl2-awf">scufl2-awf</a></td> <td>Workflow definition of the MG-RAST <a href="https://github.com/MG-RAST/AWE">AWE</a> workflow engine. (experimental)</td></tr>
    <tr><td>application/vnd.shiwa.iwir+xml</td><td>read/write</td> <td><a href="https://github.com/stain/scufl2-iwir/">scufl2-iwir</a></td> <td><a href="http://www.shiwa-workflow.eu/">SHIWA</a>'s <a href="https://www.shiwa-workflow.eu/documents/10753/55350/IWIR+v1.1+Specification">IWIR</a> interoperabile workflow language (experimental)</td></tr>
    <tr><td>application/json</td> <td>write</td> <td><a href="https://github.com/myGrid/scufl2-examples">scufl2-examples</a></td> <td>Abstract workflow as JSON (experimental)</td></tr>
</table>


OSGi services
-------------
To use SCUFL2 from OSGi with Spring, use the following OSGi Services. Example,
from `META-INF/spring/run-context.osgi.xml`:

    <?xml version="1.0" encoding="UTF-8"?>
    <beans:beans xmlns="http://www.springframework.org/schema/osgi"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:beans="http://www.springframework.org/schema/beans"
                xsi:schemaLocation="http://www.springframework.org/schema/beans
                                    http://www.springframework.org/schema/beans/spring-beans.xsd
                                    http://www.springframework.org/schema/osgi
                                    http://www.springframework.org/schema/osgi/spring-osgi.xsd">

        <service ref="myService" interface="com.example.MyService"/>

        <reference id="workflowBundleIO" interface="org.apache.taverna.scufl2.api.io.WorkflowBundleIO" />

    </beans:beans>

And in `META-INF/spring/run-context.xml`:

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
                            http://www.springframework.org/schema/beans/spring-beans.xsd">

        <bean id="myService" class="com.example.impl.MyServiceImpl" >
                <property name="workflowBundleIO" ref="workflowBundleIO"/>
        </bean>

    </beans>

This will provide a `WorkflowBundleIO` instance with its readers and
writers loaded through OSGi, which when the bundles for `taverna-scufl2-t2flow`
and `taverna-scufl2-wfbundle` are also loaded, would include support for the
Taverna 2 t2flow format and the Taverna 3 wfbundle format.

Note that you do not need to use OSGi services to instantiate
`Scufl2Tools` or `URITools`, but may do so if you wish.




[1]: http://www.mygrid.org.uk/dev/wiki/display/developer/SCUFL2
[3]: http://www.mygrid.org.uk/dev/wiki/display/story/Dataflow+serialization
[4]: http://www.mygrid.org.uk/dev/wiki/display/developer/SCUFL2+use+cases
[7]: http://www.mygrid.org.uk/dev/wiki/display/developer/Taverna+Workflow+Bundle
[9]: http://www.mygrid.org.uk/dev/wiki/display/developer/SCUFL2+API
[10]: http://taverna.incubator.apache.org/javadoc/taverna-language/
