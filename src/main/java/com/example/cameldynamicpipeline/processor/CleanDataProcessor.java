package com.example.cameldynamicpipeline.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.List;

@Component("cleanDataProcessor")
public class CleanDataProcessor implements Processor {
    public void process(Exchange exchange) {
        System.out.println("CleanDataProcessor called" + exchange.getIn().getBody());
        List<Map<String, Object>> rowList = (List<Map<String, Object>>) exchange.getIn().getBody();
        rowList.forEach(row -> {
            row.put("name", ((String) row.get("name")).trim());
        });
        System.out.println("Cleaned Data row 0: " + rowList.get(0));
        exchange.getIn().setBody(rowList.get(0));
    }
}

