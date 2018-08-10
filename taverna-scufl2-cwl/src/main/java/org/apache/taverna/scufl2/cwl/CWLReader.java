/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.taverna.scufl2.cwl;

import static java.util.Collections.singleton;

import java.io.*;
import java.util.Set;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.WorkflowBundleReader;
import org.apache.taverna.scufl2.api.io.ReaderException;

import org.apache.taverna.scufl2.cwl.components.WorkflowProcess;

public class CWLReader implements WorkflowBundleReader {

    public static final String CWL_TYPE = "text/cwl";

    @Override
    public Set<String> getMediaTypes() {
        return singleton(CWL_TYPE);
    }

    @Override
    public WorkflowBundle readBundle(File bundleFile, String mediaType) throws ReaderException, IOException {
        try (BufferedInputStream is = new BufferedInputStream(
                new FileInputStream(bundleFile))) {
            Converter converter = new Converter();
            return converter.buildWorkflowBundle(new WorkflowProcess(is));
        }
    }

    @Override
    public WorkflowBundle readBundle(InputStream inputStream, String mediaType) throws ReaderException, IOException {
        Converter converter = new Converter();
        return converter.buildWorkflowBundle(new WorkflowProcess(inputStream));
    }

    @Override
    public String guessMediaTypeForSignature(byte[] firstBytes) {
        if (new String(firstBytes)
                .contains("cwlVersion"))
            return CWL_TYPE;
        return null;
    }
}