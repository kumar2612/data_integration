package com.example.cameldynamicpipeline.handler;

import org.apache.camel.model.RouteDefinition;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import com.example.cameldynamicpipeline.entity.IntegrationStep;
import com.example.cameldynamicpipeline.model.FlowFile;
import java.util.List;
import java.util.Map;

@Component("sql")
public class SqlStepHandler implements StepHandler {
    @Override
    public RouteDefinition apply(RouteDefinition route, IntegrationStep step) {
        JSONObject config = new JSONObject(step.getConfig());
        String query = config.getString("query");
        boolean isBatch = config.optBoolean("batch", false);
        
        String endpoint = "sql:" + query + "?dataSource=#dataSource";
        if (isBatch) {
            endpoint += "&batch=true&useIterator=false";
        }
        
        return route
            .process(exchange -> {
                FlowFile flowFile = exchange.getIn().getBody(FlowFile.class);
                if (flowFile == null) {
                    flowFile = new FlowFile();
                }
                
                // For batch inserts, we need to prepare the parameters
                if (isBatch && flowFile.getContent() instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> rows = (List<Map<String, Object>>) flowFile.getContent();
                    exchange.getIn().setBody(rows);
                }
                
                exchange.setProperty("flowFile", flowFile);
            })
            .to(endpoint)
            .process(exchange -> {
                FlowFile flowFile = exchange.getProperty("flowFile", FlowFile.class);
                if (flowFile == null) {
                    flowFile = new FlowFile();
                }
                
                Object result = exchange.getIn().getBody();
                flowFile.setContent(result);
                flowFile.addAttribute("sql.query", query);
                flowFile.addAttribute("sql.batch", isBatch);
                flowFile.addAttribute("sql.timestamp", System.currentTimeMillis());
                
                exchange.getIn().setBody(flowFile);
            });
    }
}

