/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ************************************************************************/
/*  This file is derived from ODFDOM 0.8.6, and
 *  has been modified for Apache Taverna.
 *  (c) 2010-2014 University of Manchester
 *  (c) 2015 The Apache Software Foundation
 */
package org.apache.taverna.scufl2.ucfpackage.impl.odfdom.pkg;

import java.io.File;
import java.io.IOException;

class TempDir {
    /**
     * Creates a temp directory with a generated name (given a certain prefix)
     * in a given directory.
     * @param prefix The prefix for the directory name; must be three characters
     * or more.
     * @param parentDir A directory where the new tmep directory is created;
     * if this is null, the java,io.tmpdir will be used.
     */
    static File newTempOdfDirectory(String prefix, File parentDir)
            throws IOException {
        File tempFile = File.createTempFile(prefix, "", parentDir);
        if (!tempFile.delete())
            throw new IOException();
        if (!tempFile.mkdir())
            throw new IOException();
        TempDirDeleter.getInstance().add(tempFile);
        return tempFile;
    }

    /**
     * Delete a temporary created directory. As a precaution, only directories
     * created by this class can be deleted. To avoid memory leaks, this class
     * should be used exclusively for deleting.
     * @param odfDirectory the temp directory to delete.
     */
    static void deleteTempOdfDirectory(File odfDirectory) {
        if (TempDirDeleter.getInstance().remove(odfDirectory))
            TempDirDeleter.getInstance().deleteDirectory(odfDirectory);
    }
}
