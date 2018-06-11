#!/usr/bin/env cwl-runner
cwlVersion: v1.0
class: Workflow

inputs:
  name: string

outputs: []

steps:
  step1:
    run: example.cwl

    inputs:
      - id: text
        source: "#x/name"

    outputs: []
