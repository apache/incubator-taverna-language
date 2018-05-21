#!/usr/bin/env cwl-runner
cwlVersion: v1.0
class: Workflow

inputs:
  message: string

outputs:
  download:
    type: File
    outputSource:  "#step1/curl"

steps:
  step1:
    run:
      class: CommandLineTool
      baseCommand: echo
      inputs:
        text:
          type: string
          inputBinding:
            position: 1
      outputs: []
    in:
      text: message

    out: [curl]
