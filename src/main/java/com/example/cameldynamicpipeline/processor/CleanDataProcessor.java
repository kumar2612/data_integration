package com.example.cameldynamicpipeline.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("cleanDataProcessor")
public class CleanDataProcessor implements Processor {
    
    @Override
    public void process(Exchange exchange) throws Exception {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = exchange.getIn().getBody(List.class);
        List<Map<String, Object>> cleanedRows = new ArrayList<>();
        
        for (Map<String, Object> row : rows) {
            // Clean each row's data
            row.computeIfPresent("name", (k, v) -> v.toString().trim());
            cleanedRows.add(row);
        }
        
        exchange.getIn().setBody(cleanedRows);
    }
}

