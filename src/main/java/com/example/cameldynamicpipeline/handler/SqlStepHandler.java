package com.example.cameldynamicpipeline.handler;

import org.apache.camel.model.RouteDefinition;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import com.example.cameldynamicpipeline.entity.IntegrationStep;

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
        
        return route.to(endpoint);
    }
}

