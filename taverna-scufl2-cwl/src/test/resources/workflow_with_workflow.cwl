#!/usr/bin/env cwl-runner

#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements. See the NOTICE file
#  distributed with this work for additional information
#  regarding copyright ownership. The ASF licenses this file
#  to you under the Apache License, Version 2.0 (the
#  "License"); you may not use this file except in compliance
#  with the License. You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing,
#  software distributed under the License is distributed on an
#  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#  KIND, either express or implied. See the License for the
#  specific language governing permissions and limitations
#  under the License.
cwlVersion: v1.0
class: Workflow

inputs:
  message: string

outputs:
  download:
    type: File
    outputSource:  "curl"

steps:
  step1:
    run:
      class: Workflow
      inputs:
        name: string
      outputs:
        output1: string
      steps:
        step2:
          run: example.cwl
          inputs:
            - id: text
              source: "name"
          outputs: []
    in:
      text: message

    out: [curl]
