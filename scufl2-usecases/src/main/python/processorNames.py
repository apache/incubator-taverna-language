#!/usr/bin/env python

import sys
import zipfile
import os.path
import xml.etree.ElementTree
from rdflib import Graph
from rdflib import Namespace
import urllib
from rdflib.term import URIRef

Scufl2NS = Namespace("http://ns.taverna.org.uk/2010/scufl2#")


NS_CONTAINER="{urn:oasis:names:tc:opendocument:xmlns:container}"


import rdflib

class Scufl2(object):
    def __init__(self, filename=None):
        self.filename = filename
        if filename is not None:
            self.zip = zipfile.ZipFile(filename)
            self.check_mime_type()
            self.parse()    

    def check_mime_type(self):       
        mimetype = self.zip.open("mimetype").read()
        if mimetype != "application/vnd.taverna.scufl2.workflow-bundle":
            raise Scufl2Error("Unknown mimetype %r" % mimetype)

    def _is_valid_prefix(self, filename):
        valid_prefixes = ["workflow/", "annotation/", "profile/"]
        for prefix in valid_prefixes:
            if filename.startswith(prefix):
                return True
        return False        

    def parse_all_graphs(self, sameAs):
        filenames = {}
        for filename in self.zip.namelist():
            if not self._is_valid_prefix(filename):
                continue
            if filename.endswith(".ttl"):
                base = filename[:-4]
                filenames[base] = filename
            elif filename.endswith(".rdf"):
                base = filename[:-4]
                if not base in filenames:
                    filenames[base] = filename
        
        for name in filenames:
            filename = filenames[name]
            rdf_file = self.zip.open(filename)
            format = "n3"
            if filename.endswith(".rdf"):
                format = "xml"
            base = sameAs + filename
            self.graph.parse(rdf_file, base, format=format)

    def parse(self):
        if "workflowBundle.ttl" in self.zip.namelist():
            format = "n3" 
            rootfile = "workflowBundle.ttl"
        elif "workflowBundle.rdf" in self.zip.namelist():
            rootfile = "workflowBundle.rdf"
            format = "xml" 
        else:
            raise Scufl2Error("Can't find workflowBundle.ttl or "
                              "workflowBundle.rdf")

        self.uri = "file://" + urllib.pathname2url(os.path.abspath(self.filename)) + "/"
        early_graph = Graph()    
        rdf_file = self.zip.open(rootfile)
        early_graph.parse(rdf_file, self.uri, format=format)
        sameBaseAs = list(early_graph.objects(subject=URIRef(self.uri), predicate=Scufl2NS.sameBaseAs))

        if not sameBaseAs:
            # Fall back to the file:/// URIs   
            self.graph = early_graph
        else:    
            # Use the sameBaseAs as the base
            self.uri = sameBaseAs[0]
            self.graph = Graph()
            # Reparse it
            rdf_file = self.zip.open(rootfile)
            self.graph.parse(rdf_file, self.uri, format=format)

        self.parse_all_graphs(self.uri)


class Scufl2Error(Exception):
    pass


def main(prg="processorNames.py", filename=None):
    if filename is None:
        filename = os.path.join(os.path.dirname(prg), "..", "resources",
        "workflows", "example.scufl2")
    
    scufl2 = Scufl2(filename)
    
    for workflowUri in scufl2.graph.objects(predicate=Scufl2NS.workflow):
        for name in scufl2.graph.objects(workflowUri, Scufl2NS.name):
            print name
        for processorUri in scufl2.graph.objects(workflowUri, Scufl2NS.processor):
            for name in scufl2.graph.objects(processorUri, Scufl2NS.name):
                print "---", name, processorUri


if __name__ == "__main__":
    main(*sys.argv)

