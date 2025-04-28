package com.example.cameldynamicpipeline.handler;

import org.apache.camel.model.RouteDefinition;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import com.example.cameldynamicpipeline.entity.IntegrationStep;
import com.example.cameldynamicpipeline.model.FlowFile;

@Component("mongodb")
public class MongoDBStepHandler implements StepHandler {
    @Override
    public RouteDefinition apply(RouteDefinition route, IntegrationStep step) {
        JSONObject config = new JSONObject(step.getConfig());
        String collection = config.getString("collection");
        String operation = config.optString("operation", "insert");
        
        return route
            .process(exchange -> {
                // Ensure we have a FlowFile
                FlowFile flowFile = exchange.getIn().getBody(FlowFile.class);
                if (flowFile == null) {
                    flowFile = new FlowFile();
                    exchange.getIn().setBody(flowFile);
                }
                flowFile.addAttribute("mongodb.collection", collection);
                flowFile.addAttribute("mongodb.operation", operation);
                exchange.getIn().setBody(flowFile.getContent());
            })
            .to(String.format("mongodb:mongoClient?database={{spring.data.mongodb.database}}&collection=%s&operation=%s", 
                collection, operation))
            .process(exchange -> {
                FlowFile flowFile = exchange.getProperty("flowFile", FlowFile.class);
                if (flowFile == null) {
                    flowFile = new FlowFile();
                }
                flowFile.addAttribute("mongodb.result", exchange.getIn().getBody());
                exchange.getIn().setBody(flowFile);
            });
    }
}