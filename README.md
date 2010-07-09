SCUFL2
======
[SCUFL2 wiki][1]

(c) 2009-2010 myGrid, University of Manchester

Licensed under the [GNU Lesser General Public License (LGPL) 2.1][9],
except for `scufl2-usecases`, which are licensed under a [modified BSD
license](scufl2-usecases/LICENSE.txt).

This is the code, model and XML syntax of SCUFL2 which will replace the current 
[Taverna][8] format [t2flow][2].

Compare this with the [t2flow XML schema][3] - which has documentation
about workflow elements as currently serialized.

The t2flow serialization format suffers from being very close to the
Java object model, and contains various items that are simply Java beans
serialized using _XMLBeans_. As the t2flow format is very verbose, it
can be difficult to deal with for third party software to do
*inspection* ("Which services does this workflow use?"), *modification*
("Change all calls to http://broken.com/ to http://fixed.com/") and
*generation* ("Build a custom workflow from a button").

Developers have informed us that the old SCUFL format of Taverna 1 was
significantly easier to work with. However, this format also has its
caveats, like no schema, unidentified ways to extend service definitions
for Taverna plugins and not supporting various new features in the
Taverna 2 engine.

We have therefore decided to form a new serialisation format for
workflows, called *SCUFL2*. This format will be accompanied with an
*UML* model, and a primary serialisation format as *XML*, but also with
possible secondary serialisations as *JSON* and *RDF*, all following the
UML model. This model will also be reflected in a lightweight *API*,
which can deserialize and serialize these formats, in addition to
`.scufl` and `.t2flow`, but also more easily allow inspection of
workflow structures, modification and generation.

Rough overview:

* [*scufl2-api*](scufl2-api/) Java Beans for SCUFL2 objects (and currently XML import/export)
* [*scufl2-t2flow*](scufl2-t2flow/) .t2flow import (and later export)
* [*scufl2-rdf*](scufl2-rdf/) RDF export (and later import)
* [*scufl2-usecases*](scufl2-usecases/) Example code covering [SCUFL2 use cases][4]


Here is an attempt at demonstrating the new proposed *XML syntax* for
Scufl2: [as.scufl2.xml][5] - a translation of
[as.t2flow][6]

Specification of *identifiers* in [Taverna URI templates][7].




[1]: http://www.mygrid.org.uk/dev/wiki/display/developer/SCUFL2
[2]: http://www.mygrid.org.uk/dev/wiki/display/story/Dataflow+serialization
[3]: http://code.google.com/p/taverna/source/browse/taverna#taverna/dev/xsd/trunk/t2flow
[4]: http://www.mygrid.org.uk/dev/wiki/display/developer/SCUFL2+use+cases
[5]: http://www.mygrid.org.uk/dev/wiki/download/attachments/3572756/as.scufl2.xml?version=1&modificationDate=1270028271000
[6]: http://www.mygrid.org.uk/dev/wiki/download/attachments/3572756/as.t2flow?version=1&modificationDate=1270028403000
[7]: http://www.mygrid.org.uk/dev/wiki/display/developer/Taverna+URI+templates
[8]: http://www.taverna.org.uk/
[9]: http://www.gnu.org/licenses/lgpl-2.1.html
