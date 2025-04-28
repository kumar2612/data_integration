package com.example.cameldynamicpipeline;

import org.apache.camel.Exchange;
import org.apache.camel.TypeConversionException;
import org.apache.camel.TypeConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.example.cameldynamicpipeline.model.FlowFile;

@SpringBootApplication
public class CameldynamicpipelineApplication {

    public static void main(String[] args) {
        SpringApplication.run(CameldynamicpipelineApplication.class, args);
    }
    
    @Bean
    public TypeConverter flowFileConverter() {
        return new TypeConverter() {
            @Override
            public <T> T convertTo(Class<T> type, Object value) throws TypeConversionException {
                if (type.isAssignableFrom(FlowFile.class)) {
                    // Create new FlowFile if null or convert existing
                    FlowFile flowFile;
                    if (value instanceof FlowFile) {
                        flowFile = (FlowFile) value;
                    } else {
                        flowFile = new FlowFile();
                        if (value != null) {
                            flowFile.setContent(value);
                        }
                    }
                    return type.cast(flowFile);
                }
                throw new TypeConversionException(value, type, null);
            }

            @Override
            public <T> T convertTo(Class<T> type, Exchange exchange, Object value) throws TypeConversionException {
                if (type.isAssignableFrom(FlowFile.class) && exchange != null) {
                    // Try to get or create FlowFile
                    FlowFile flowFile = exchange.getProperty("flowFile", FlowFile.class);
                    if (flowFile == null) {
                        flowFile = new FlowFile();
                        exchange.setProperty("flowFile", flowFile);
                    }
                    
                    // Update content if value provided
                    if (value != null && !(value instanceof FlowFile)) {
                        flowFile.setContent(value);
                    }
                    
                    return type.cast(flowFile);
                }
                return convertTo(type, value);
            }

            @Override
            public <T> T tryConvertTo(Class<T> type, Exchange exchange, Object value) {
                try {
                    return convertTo(type, exchange, value);
                } catch (TypeConversionException e) {
                    return null;
                }
            }

            @Override
            public <T> T tryConvertTo(Class<T> type, Object value) {
                try {
                    return convertTo(type, value);
                } catch (TypeConversionException e) {
                    return null;
                }
            }

            @Override
            public <T> T mandatoryConvertTo(Class<T> type, Exchange exchange, Object value) throws TypeConversionException {
                return convertTo(type, exchange, value);
            }

            @Override
            public <T> T mandatoryConvertTo(Class<T> type, Object value) throws TypeConversionException {
                return convertTo(type, value);
            }

            @Override
            public boolean allowNull() {
                return true;
            }
        };
    }
}
