package com.example.cameldynamicpipeline.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import com.example.cameldynamicpipeline.model.FlowFile;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component("dynamicMappingProcessor")
public class DynamicMappingProcessor implements Processor {
    
    private static final Logger logger = LoggerFactory.getLogger(DynamicMappingProcessor.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void process(Exchange exchange) throws Exception {
        FlowFile flowFile = exchange.getIn().getBody(FlowFile.class);
        Long flowId = exchange.getProperty("flowId", Long.class);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> sourceData = (List<Map<String, Object>>) flowFile.getContent();
        System.out.println("sourceData: " + sourceData);
        System.out.println("flowId: " + flowId);
        
        // Get active mappings for this flow
        List<Map<String, Object>> mappings = jdbcTemplate.queryForList(
            "SELECT source_column, target_column, transform_expression FROM column_mappings WHERE flow_id = ? AND active = 1",
            flowId
        );

        
        List<Map<String, Object>> transformedData = new ArrayList<>();

        // Log source data structure
        logger.debug("Source data first row: {}", sourceData.isEmpty() ? "empty" : sourceData.get(0));

        for (Map<String, Object> row : sourceData) {
            Map<String, Object> transformedRow = new HashMap<>();
            
            // Ensure required columns exist
            transformedRow.put("id", null);  // Default values
            transformedRow.put("name", null);
            
            for (Map<String, Object> mapping : mappings) {
                String sourceColumn = (String) mapping.get("source_column");
                String targetColumn = (String) mapping.get("target_column");
                String transform = (String) mapping.get("transform_expression");

                Object value = row.get(sourceColumn);
                if (value != null) {
                    value = applyTransform(value, transform);
                }
                transformedRow.put(targetColumn, value);
            }
            
            // Validate required fields
            if (transformedRow.get("id") == null || transformedRow.get("name") == null) {
                logger.warn("Skipping row due to missing required fields: {}", transformedRow);
                continue;
            }
            
            transformedData.add(transformedRow);
        }

        // Log transformed data structure
        logger.debug("Transformed data first row: {}", transformedData.isEmpty() ? "empty" : transformedData.get(0));

        flowFile.setContent(transformedData);
        flowFile.addAttribute("mapped.columns", mappings.size());
        flowFile.addAttribute("mapped.rows", transformedData.size());
        
        // Set the raw list as body for SQL component
        System.out.println("Transformed data: " + transformedData);
        exchange.getIn().setBody(transformedData);
    }

    private Object applyTransform(Object value, String transform) {
        if (transform == null || transform.isEmpty()) {
            return value;
        }

        // Add your transformation logic here
        // Example: UPPER, LOWER, TRIM, etc.
        switch (transform.toUpperCase()) {
            case "UPPER":
                return value.toString().toUpperCase();
            case "LOWER":
                return value.toString().toLowerCase();
            case "TRIM":
                return value.toString().trim();
            default:
                return value;
        }
    }
}