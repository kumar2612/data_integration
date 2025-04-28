package com.example.cameldynamicpipeline.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import com.example.cameldynamicpipeline.model.FlowFile;
import java.util.List;
import java.util.Map;

@Component("cleanDataProcessor")
public class CleanDataProcessor implements Processor {
    
    @Override
    public void process(Exchange exchange) throws Exception {
        FlowFile flowFile = exchange.getIn().getBody(FlowFile.class);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) flowFile.getContent();
        
        flowFile.addAttribute("processor.name", "cleanDataProcessor");
        flowFile.addAttribute("processor.start.time", System.currentTimeMillis());
        flowFile.addAttribute("record.count", rows.size());
        
        for (Map<String, Object> row : rows) {
            row.computeIfPresent("name", (k, v) -> v.toString().trim());
        }
        
        flowFile.setContent(rows);
        flowFile.addAttribute("processor.end.time", System.currentTimeMillis());
        
        exchange.getIn().setBody(flowFile);
    }
}

