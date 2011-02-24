#!/bin/sh
set -e
rm example.scufl2
cd example
zip -0 -X ../example.scufl2 mimetype
zip -r ../example.scufl2 . -x mimetype 
