package com.example.cameldynamicpipeline.handler;

import java.util.List;

import org.apache.camel.model.RouteDefinition;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import com.example.cameldynamicpipeline.entity.IntegrationStep;
import com.example.cameldynamicpipeline.model.FlowFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.camel.builder.Builder.constant;

@Component("sql")
public class SqlStepHandler implements StepHandler {
    private static final Logger logger = LoggerFactory.getLogger(SqlStepHandler.class);

    @Override
    public RouteDefinition apply(RouteDefinition route, IntegrationStep step) {
        JSONObject config = new JSONObject(step.getConfig());
        String query = config.getString("query");
        boolean isBatch = config.optBoolean("batch", false);
        Long flowId = Long.valueOf(step.getFlow().getId()); // Get flowId from step
        
        logger.info("Configuring SQL step for flow: {}, query: {}", flowId, query);
        
        return route
            .setProperty("flowId", constant(flowId)) // Set flowId as exchange property
            .process(exchange -> {
                FlowFile flowFile = exchange.getIn().getBody(FlowFile.class);
                Object content = flowFile != null ? flowFile.getContent() : null;
                
                logger.info("SQL Step Pre-execution - FlowId: {}, Query: {}", flowId, query);
                logger.info("SQL Step Pre-execution - Content: {}", content);
                
                if (isBatch && content instanceof List) {
                    List<?> list = (List<?>) content;
                    if (list.isEmpty()) {
                        throw new IllegalStateException("No data available for batch insert");
                    }
                    exchange.getIn().setBody(list);
                }
            })
            .to("sql:" + query + "?dataSource=#dataSource" + (isBatch ? "&batch=true" : ""))
            .process(exchange -> {
                Object result = exchange.getIn().getBody();
                logger.info("SQL Step Result - FlowId: {}, Result: {}", flowId, result);
                
                FlowFile flowFile = new FlowFile();
                flowFile.setContent(result);
                flowFile.addAttribute("sql.query", query);
                flowFile.addAttribute("sql.flowId", String.valueOf(flowId));
                exchange.getIn().setBody(flowFile);
            });
    }
}

