package com.example;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * A wrapper around the other example main() methods
 * 
 * This in order to make a single executable JAR file.
 * 
 * @author Stian Soiland-Reyes
 *
 */
public class Scufl2Tool {
    
    public enum Tool {
        t2flowtowfbundle(ConvertT2flowToWorkflowBundle.class, "Convert t2flow workflows to wfbundle"),
        jsonexport(JsonExport.class, "Export JSON structure of workflow"),
        processornames(ProcessorNames.class, "List tree of processor names in workflow"),
        servicetypes(ServiceTypes.class, "List service types used in workflow"),
        workflowmaker(WorkflowMaker.class, "Create an example workflow programmatically");
        
        private final String description;
        private final Class<?> mainClass;

        Tool(Class<?> mainClass, String description) {
            this.mainClass = mainClass;
            this.description = description;
        }

        @Override
        public String toString() {
            return name() + " - " + description;
        }

        public Class<?> getMainClass() {
            return mainClass;
        }
    }
    
    public static void main(String[] args) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        List<String> argsList = Arrays.asList(args);
        if (argsList.isEmpty() || argsList.get(0).equals("-h")) {
            help();
            return;
        }
        Tool tool;
        try {
            tool = Tool.valueOf(argsList.get(0).toLowerCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid tool: " + argsList.get(0));
            help();
            return;
        }
        
        // Find and invoke the main method
        Class<?> mainCls = tool.getMainClass();
        Method main = mainCls.getMethod("main", String[].class);
        // But stripping out the first argument
        args = argsList.subList(1, argsList.size()).toArray(new String[0]);
        // Array of arrays!
        Object[] mainArgs = new Object[]{args};
        main.invoke(null, mainArgs);
    }

    public static void help() {
        System.out.println("SCUFL2 workflow tool");
        System.out.println("Usage: scufl2tool <tool> [option] ...");
        System.out.println();
        System.out.println("Available tools:");
        for (Tool tool : Tool.values()) {
            System.out.println(tool);
        }
        
    }
}
;