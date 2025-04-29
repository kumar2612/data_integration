package com.example.cameldynamicpipeline.router;
import com.example.cameldynamicpipeline.config.ApplicationContextProvider;
import com.example.cameldynamicpipeline.config.StepHandlerRegistry;
import com.example.cameldynamicpipeline.entity.FlowExecution;
import com.example.cameldynamicpipeline.entity.IntegrationFlow;
import com.example.cameldynamicpipeline.entity.IntegrationStep;
import com.example.cameldynamicpipeline.entity.StepExecution;
import com.example.cameldynamicpipeline.handler.StepHandler;
import com.example.cameldynamicpipeline.repository.FlowExecutionRepository;
import com.example.cameldynamicpipeline.repository.IntegrationFlowRepository;
import com.example.cameldynamicpipeline.repository.IntegrationStepRepository;
import com.example.cameldynamicpipeline.repository.StepExecutionRepository;

import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.List;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;

@Component
public class DynamicRouteLoader {

    private static final Logger logger = LoggerFactory.getLogger(DynamicRouteLoader.class);

    @Autowired private CamelContext camelContext;
    @Autowired private IntegrationFlowRepository flowRepo;
    @Autowired private IntegrationStepRepository stepRepo;
    @Autowired
    private StepHandlerRegistry stepHandlerRegistry;
    @Autowired private FlowExecutionRepository executionRepo;
    @Autowired private StepExecutionRepository stepExecutionRepo;

    @PostConstruct
    public void loadRoutes() throws Exception {
        List<IntegrationFlow> flows = flowRepo.findByActiveTrue();
        for (IntegrationFlow flow : flows) {
            List<IntegrationStep> steps = stepRepo.findByFlowIdOrderByStepOrder(flow.getId());
            camelContext.addRoutes(buildRoute(flow, steps));
        }
    }

    private RouteBuilder buildRoute(IntegrationFlow flow, List<IntegrationStep> steps) {
        return new RouteBuilder() {
            @Override
            public void configure() {
                onException(Exception.class)
                    .process(exchange -> {
                        FlowExecution flowExecution = exchange.getProperty("flowExecution", FlowExecution.class);
                        if (flowExecution != null) {
                            flowExecution.setStatus("FAILED");
                            flowExecution.setEndTime(LocalDateTime.now());
                            flowExecution.setError(exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class).getMessage());
                            executionRepo.save(flowExecution);

                            // Update current step execution if exists
                            StepExecution stepExecution = exchange.getProperty("currentStepExecution", StepExecution.class);
                            if (stepExecution != null) {
                                stepExecution.setStatus("FAILED");
                                stepExecution.setEndTime(LocalDateTime.now());
                                stepExecution.setError(exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class).getMessage());
                                stepExecutionRepo.save(stepExecution);
                            }
                        }
                        logger.error("Flow execution failed: {}", flow.getName(), exchange.getException());
                    });

                RouteDefinition route = from("quartz://" + flow.getName() + "?cron=" + flow.getScheduleCron())
                    .routeId(flow.getName())
                    .process(exchange -> {
                        FlowExecution execution = new FlowExecution();
                        execution.setFlowId(flow.getId());
                        execution.setFlowName(flow.getName());
                        execution.setStatus("RUNNING");
                        execution.setStartTime(LocalDateTime.now());
                        executionRepo.save(execution);
                        exchange.setProperty("flowExecution", execution);
                        logger.info("Starting flow execution: {}", flow.getName());
                    });

                for (IntegrationStep step : steps) {
                    StepHandler handler = stepHandlerRegistry.getHandler(step.getStepType());
                    
                    // Start step execution
                    route = route.process(exchange -> {
                        FlowExecution flowExecution = exchange.getProperty("flowExecution", FlowExecution.class);
                        StepExecution stepExecution = new StepExecution();
                        stepExecution.setFlowExecutionId(flowExecution.getId());
                        stepExecution.setStepId(step.getId());
                        stepExecution.setStepType(step.getStepType());
                        stepExecution.setStatus("RUNNING");
                        stepExecution.setStartTime(LocalDateTime.now());
                        stepExecutionRepo.save(stepExecution);
                        exchange.setProperty("currentStepExecution", stepExecution);
                    });
                    
                    route = handler.apply(route, step);
                    
                    // Complete step execution
                    route = route.process(exchange -> {
                        StepExecution stepExecution = exchange.getProperty("currentStepExecution", StepExecution.class);
                        stepExecution.setStatus("COMPLETED");
                        stepExecution.setEndTime(LocalDateTime.now());
                        
                        // Try to get record count from exchange
                        Object body = exchange.getIn().getBody();
                        if (body instanceof List) {
                            stepExecution.setRecordsProcessed(((List<?>) body).size());
                        }
                        
                        stepExecutionRepo.save(stepExecution);
                        logger.info("Completed step: {} for flow: {}", step.getStepType(), flow.getName());
                    });
                }

                // Complete flow execution
                route.process(exchange -> {
                    FlowExecution execution = exchange.getProperty("flowExecution", FlowExecution.class);
                    execution.setStatus("COMPLETED");
                    execution.setEndTime(LocalDateTime.now());
                    executionRepo.save(execution);
                    logger.info("Completed flow execution: {}", flow.getName());
                });
            }
        };
    }
}

