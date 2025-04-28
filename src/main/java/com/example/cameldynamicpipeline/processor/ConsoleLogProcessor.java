package com.example.cameldynamicpipeline.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("consoleLogProcessor")
public class ConsoleLogProcessor implements Processor {
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void process(Exchange exchange) throws Exception {
        Object body = exchange.getIn().getBody();
        System.out.println("=== Data Being Processed ===");
        System.out.println(objectMapper.writeValueAsString(body));
        System.out.println("==========================");
    }
}