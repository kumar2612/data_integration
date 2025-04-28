package com.example.cameldynamicpipeline.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component("headerLogProcessor")
public class HeaderLogProcessor implements Processor {
    
    private static final Logger logger = LoggerFactory.getLogger(HeaderLogProcessor.class);

    @Override
    public void process(Exchange exchange) {
        Map<String, Object> headers = exchange.getIn().getHeaders();
        
        logger.info("=== Exchange Headers ===");
        headers.forEach((key, value) -> {
            logger.info("Header - {}: {}", key, value);
        });
        logger.info("=====================");
    }
}