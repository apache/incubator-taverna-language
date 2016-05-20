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

# SCUFL2 API

SCUFL2 API is part of [Apache Taverna Language](http://taverna.incubator.apache.org/download/language/).

This is the [API](http://taverna.incubator.apache.org/documentation/scufl2/api),
model and format of
[SCUFL2](http://taverna.incubator.apache.org/documentation/scufl2/), which is the
workflow format of [Apache Taverna](http://taverna.incubator.apache.org/).

SCUFL2 replaces the `.t2flow` format of Taverna 2.
This API allows JVM applications to inspect, generate and modify
Apache Taverna workflow definitions without depending on the Apache Taverna
runtime.

The format
[Scufl2 Workflow Bundle](http://taverna.incubator.apache.org/documentation/scufl2/bundle)
is defined alongside this API. This format can be inspected, generated and modified independently
of this API.


For more information, see the [SCUFL2 API][9] pages, the
[Javadoc](http://taverna.incubator.apache.org/javadoc/taverna-language/)
and the [SCUFL2 examples](../taverna-scufl2-examples/).


Usage
-----

See the [Apache Taverna Language](../) README for details on building and
using Apache Taverna Language using Maven.  You will typically want to also use
at least [taverna-scufl2-wfbundle](../taverna-scufl2-wfbundle) to support
loading and saving of SCUFL2 workflows in the `.wfbundle` format.

All Scufl2 modules are also valid OSGi bundles, see the [OSGi
section](#osgi-services) below.  

See the [Taverna Language Javadoc](http://taverna.incubator.apache.org/javadoc/taverna-language/)
for documentation of classes and methods
of SCUFL2. The package
[org.apache.taverna.scufl2.api](http://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/scufl2/api/package-summary.html)
is a good starting point.

See the folder [taverna-scufl2-examples](../taverna-scufl2-examples/)
for examples of usage. The best classes to start using would be
`org.apache.taverna.scufl2.api.io.WorkflowBundleIO` and
`org.apache.taverna.scufl2.api.container.WorkflowBundle`.

Example of converting `.t2flow` to `.wfbundle`:

```java
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
```

Supported file formats with `WorkflowBundleIO` and their required modules:

| Media type | Support | JAR | Description |
| ---------- | ------- | --- | ----------- |
| application/vnd.taverna.t2flow+xml | read | taverna-scufl2-t2flow | Taverna 2 t2flow |
| application/vnd.taverna.scufl2.workflow-bundle | read/write | taverna-scufl2-wfbundle | Taverna 3 workflow bundle |
| application/vnd.taverna.scufl+xml | read | taverna-scufl2-scufl | Taverna 1 SCUFL (experimental) |
| text/vnd.taverna.scufl2.structure | read/write | taverna-scufl2-api | Textual format for testing/debugging |
| text/vnd.wf4ever.wfdesc+turtle | write | taverna-scufl2-wfdesc | Abstract workflow structure in [RDF Turtle](http://www.w3.org/TR/turtle/") according to the [Wf4Ever wfdesc ontology](https://w3id.org/ro/#wfdesc) |
| text/vnd.mgrast.awe.awf+json | read | [scufl2-awf](https://github.com/stain/scufl2-awf) | Workflow definition of the MG-RAST [AWE](https://github.com/MG-RAST/AWE) workflow engine. (experimental) |
| application/vnd.shiwa.iwir+xml | read/write | [scufl2-iwir](https://github.com/stain/scufl2-iwir/) | [SHIWA](http://www.shiwa-workflow.eu/)'s [IWIR](https://www.shiwa-workflow.eu/documents/10753/55350/IWIR+v1.1+Specification) interoperabile workflow language (experimental) |
| application/json | write | taverna-scufl2-examples | Abstract workflow as JSON (experimental) |


Note that the ability for Scufl2 API to read a workflow bundle (using
the `scufl2-wfbundle` module) does not guarantee it is valid or
structurally sound. You can use the [validators](http://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/scufl2/validation/Validator.html)
to [validate structure](http://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/scufl2/validation/structural/StructuralValidator.html)
or [correctness](http://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/scufl2/validation/correctness/CorrectnessValidator.html).

You can use the [tavtool](../taverna-tavlang-tool) command line to perform
workflow format conversions, inspection and validation.


OSGi services
-------------
To use SCUFL2 from OSGi with Spring, use the following OSGi Services. Example,
from `META-INF/spring/run-context.osgi.xml`:

```xml
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
```

And in `META-INF/spring/run-context.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
                        http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="myService" class="com.example.impl.MyServiceImpl" >
            <property name="workflowBundleIO" ref="workflowBundleIO"/>
    </bean>

</beans>
```

This will provide a `WorkflowBundleIO` instance with its readers and
writers loaded through OSGi, which when the bundles for
[taverna-scufl2-t2flow](../taverna-scufl2-t2flow)
and [taverna-scufl2-wfbundle](../taverna-scufl2-wfbundle)
are also loaded, would include support for the
Taverna 2 t2flow format and the Taverna 3 wfbundle format.

Note that you do not need to use OSGi services to instantiate
`Scufl2Tools` or `URITools`, but may do so if you wish.
