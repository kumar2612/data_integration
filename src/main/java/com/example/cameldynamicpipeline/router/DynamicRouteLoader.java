package com.example.cameldynamicpipeline.router;
import com.example.cameldynamicpipeline.config.ApplicationContextProvider;
import com.example.cameldynamicpipeline.entity.IntegrationFlow;
import com.example.cameldynamicpipeline.entity.IntegrationStep;
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
                    switch (step.getStepType()) {
                        case "sql" -> {
                            String query = new JSONObject(step.getConfig()).getString("query");
                            System.out.println("SQL Query: " + query);
                            route = route.to("sql:" + query + "?dataSource=#dataSource");
                        }
                        case "processor" -> {
                            String bean = new JSONObject(step.getConfig()).getString("bean");
                            System.out.println("Processor Bean: " + bean);
                            route = route.process(resolveProcessor(bean));
                        }
                        case "rest" -> {
                            String url = new JSONObject(step.getConfig()).getString("url");
                            System.out.println("REST URL: " + url);
                            route = route.marshal().json().to(url);
                        }
                        default -> throw new RuntimeException("Unknown step type: " + step.getStepType());
                    }
                }
            }
        };
    }

    private Processor resolveProcessor(String beanName) {
        return ApplicationContextProvider.getBean(beanName, Processor.class);
    }
}

