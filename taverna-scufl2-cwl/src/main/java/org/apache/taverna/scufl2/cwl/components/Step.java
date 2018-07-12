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


import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class Step {


    private String id;
    private Process run;

    private Set<StepInput> inputs;
    private Set<StepOutput> outputs;

    public Step() {
        inputs = new HashSet<>();
        outputs = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Process getRun() {
        return run;
    }

    public void setRun(Process run) {
        this.run = run;
    }

    public void addInput(String id, String source) {
        inputs.add(new StepInput(id, source));
    }

    public void setInputs(Set<StepInput> inputs) {
        this.inputs = inputs;
    }

    public Set<StepInput> getInputs() {
        return inputs;
    }

    public void addOutput(String id) {
        outputs.add(new StepOutput(id));
    }

    public void setOutputs(Set<StepOutput> outputs) {
        this.outputs = outputs;
    }

    public Set<StepOutput> getOutputs() {
        return outputs;
    }

    public String toString() {
        return "Step " + id + ": run = " + run;
    }

}