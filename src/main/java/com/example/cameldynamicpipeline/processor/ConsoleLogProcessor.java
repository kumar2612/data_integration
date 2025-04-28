package com.example.cameldynamicpipeline.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import com.example.cameldynamicpipeline.model.FlowFile;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("consoleLogProcessor")
public class ConsoleLogProcessor implements Processor {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void process(Exchange exchange) throws Exception {
        FlowFile flowFile = exchange.getIn().getBody(FlowFile.class);
        
        System.out.println("=== FlowFile Details ===");
        System.out.println("ID: " + flowFile.getId());
        System.out.println("Created Time: " + flowFile.getCreatedTime());
        System.out.println("Attributes: " + objectMapper.writeValueAsString(flowFile.getAttributes()));
        System.out.println("Content: " + objectMapper.writeValueAsString(flowFile.getContent()));
        System.out.println("=====================");
        
        exchange.getIn().setBody(flowFile);
    }
}