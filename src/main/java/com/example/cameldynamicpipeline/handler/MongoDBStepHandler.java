package com.example.cameldynamicpipeline.handler;

import org.apache.camel.model.RouteDefinition;
import org.json.JSONObject;
import static org.apache.camel.builder.Builder.constant;
import org.springframework.stereotype.Component;
import com.example.cameldynamicpipeline.entity.IntegrationStep;

@Component("mongodb")
public class MongoDBStepHandler implements StepHandler {
    @Override
    public RouteDefinition apply(RouteDefinition route, IntegrationStep step) {
        JSONObject config = new JSONObject(step.getConfig());
        String collection = config.getString("collection");
        String operation = config.optString("operation", "insert");
        
        return route
            // Set headers that will be available in subsequent routes
            .setHeader("mongoCollection", constant(collection))
            .setHeader("mongoOperation", constant(operation))
            .setHeader("lastProcessedStep", constant("mongodb"))
            // Use the MongoDB endpoint
            .to(String.format("mongodb:mongoClient?database={{spring.data.mongodb.database}}&collection=%s&operation=%s", 
                collection, operation));
    }
}