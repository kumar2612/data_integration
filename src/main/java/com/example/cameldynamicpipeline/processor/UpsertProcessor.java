package com.example.cameldynamicpipeline.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("upsertProcessor")
public class UpsertProcessor implements Processor{
    public void process(Exchange exchange) {
        System.out.println("UpsertProcessor called");
        List<Map<String, Object>> rows = exchange.getIn().getBody(List.class);
        List<Map<String, Object>> cleanedRows = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            row.put("name", ((String) row.get("name")).trim());
            cleanedRows.add(row);
        }
       
        exchange.getIn().setBody(cleanedRows);
    }
    
}
