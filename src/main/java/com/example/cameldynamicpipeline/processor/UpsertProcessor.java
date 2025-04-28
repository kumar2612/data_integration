package com.example.cameldynamicpipeline.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import com.example.cameldynamicpipeline.model.FlowFile;
import java.util.List;
import java.util.Map;

@Component("upsertProcessor")
public class UpsertProcessor implements Processor {
    
    @Override
    public void process(Exchange exchange) throws Exception {
        FlowFile flowFile = exchange.getIn().getBody(FlowFile.class);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) flowFile.getContent();
        
        flowFile.addAttribute("processor.name", "upsertProcessor");
        flowFile.addAttribute("processor.start.time", System.currentTimeMillis());
        
        // Process the data
        // Your upsert logic here
        
        flowFile.addAttribute("processor.end.time", System.currentTimeMillis());
        flowFile.addAttribute("records.processed", rows.size());
        
        exchange.getIn().setBody(flowFile);
    }
}
