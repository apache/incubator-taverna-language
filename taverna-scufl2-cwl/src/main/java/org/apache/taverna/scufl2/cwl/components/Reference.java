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

package org.apache.taverna.scufl2.cwl.components;


public class Reference extends Process {

    private String source;

    public Reference() {
        source = "";
        name = "";
    }

    public Reference(String src) {
        this.source = src;
        this.name = "";
    }

    public void parse() {
        // TODO: read source file and parse nested workflow
    }

    public String toString() {
        return source;
    }

    public String getSource() {
        return source;
    }
}