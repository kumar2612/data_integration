package com.example.cameldynamicpipeline.router;
import com.example.cameldynamicpipeline.config.ApplicationContextProvider;
import com.example.cameldynamicpipeline.config.StepHandlerRegistry;
import com.example.cameldynamicpipeline.entity.IntegrationFlow;
import com.example.cameldynamicpipeline.entity.IntegrationStep;
import com.example.cameldynamicpipeline.handler.StepHandler;
import com.example.cameldynamicpipeline.repository.IntegrationFlowRepository;
import com.example.cameldynamicpipeline.repository.IntegrationStepRepository;

import jakarta.annotation.PostConstruct;

import java.util.List;
import org.json.JSONObject;

import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DynamicRouteLoader {

    @Autowired private CamelContext camelContext;
    @Autowired private IntegrationFlowRepository flowRepo;
    @Autowired private IntegrationStepRepository stepRepo;
    @Autowired
    private StepHandlerRegistry stepHandlerRegistry;

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
                RouteDefinition route = from("quartz://" + flow.getName() + "?cron=" + flow.getScheduleCron())
                        .routeId(flow.getName());

                for (IntegrationStep step : steps) {
                    StepHandler handler = stepHandlerRegistry.getHandler(step.getStepType());
                    route = handler.apply(route, step);
                }
            }
        };
    }
}

