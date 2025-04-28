package com.example.cameldynamicpipeline.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component("upsertProcessor")
public class UpsertProcessor implements Processor{
    public void process(Exchange exchange) {
        System.out.println("UpsertProcessor called");
        Map<String, Object> row = exchange.getIn().getBody(Map.class);
        row.put("name", ((String) row.get("name")).trim());
        exchange.getIn().setBody(row);
    }
    
}
