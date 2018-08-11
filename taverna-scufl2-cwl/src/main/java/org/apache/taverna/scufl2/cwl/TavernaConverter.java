package org.apache.taverna.scufl2.cwl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.taverna.scufl2.api.common.NamedSet;
import org.apache.taverna.scufl2.api.core.DataLink;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.scufl2.api.port.SenderPort;
import org.apache.taverna.scufl2.cwl.components.InputPort;
import org.apache.taverna.scufl2.cwl.components.OutputPort;
import org.apache.taverna.scufl2.cwl.components.Reference;
import org.apache.taverna.scufl2.cwl.components.Step;
import org.apache.taverna.scufl2.cwl.components.WorkflowProcess;

public class TavernaConverter {

    public WorkflowProcess convertWorkflow(Workflow workflow) {
        WorkflowProcess process = new WorkflowProcess();
        process.setName(workflow.getName());
        Set<InputPort> inputs = convertInputPorts(workflow);
        Set<OutputPort> outputs = convertOutputPorts(workflow);

        process.setInputPorts(inputs);
        process.setOutputPorts(outputs);

        return process;
    }

    public Set<InputPort> convertInputPorts(Workflow workflow) {
        Set<InputPort> result = new HashSet<>();
        for(InputWorkflowPort workflowPort: workflow.getInputPorts()) {
            InputPort port = new InputPort(workflowPort.getName(), "");
            result.add(port);
        }
        return result;
    }

    public Set<OutputPort> convertOutputPorts(Workflow workflow) {
        Set<OutputPort> result = new HashSet<>();
        for(OutputWorkflowPort workflowPort: workflow.getOutputPorts()) {
            OutputPort port = new OutputPort(workflowPort.getName());
            result.add(port);
        }
        return result;
    }

    public Set<Step> convertProcessors(Workflow workflow) {
        Set<Step> result = new HashSet<>();
        NamedSet<Processor> processors = workflow.getProcessors();
        Set<DataLink> dataLinks = workflow.getDataLinks();
        Map<String, SenderPort> portNameToSource = new HashMap<>();
        for(DataLink link: dataLinks) {
            portNameToSource.put(link.getSendsTo().getName(), link.getReceivesFrom());
        }

        for(Processor processor: processors) {
            Step step = convertProcessor(processor, portNameToSource);
            result.add(step);
        }
        return result;
    }

    public Step convertProcessor(Processor processor, Map<String, SenderPort> portNameToSource) {
        Step step = new Step();
        step.setRun(new Reference(processor.getName())); // TODO: Support nested steps. Check name value.
        for(InputProcessorPort port: processor.getInputPorts()) {
            InputPort stepPort = new InputPort();
            stepPort.setName(port.getName());
            SenderPort senderPort = portNameToSource.get(port.getName());
            stepPort.setSource(senderPort.getName());
            step.getInputs().add(stepPort);
        }
        return step;
    }
}
