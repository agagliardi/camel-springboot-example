package com.redhat.reproducer;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;

/**
 * A simple Camel route that triggers from a timer and calls a bean and prints
 * to system out.
 * <p/>
 * Use <tt>@Component</tt> to make Camel auto detect this route when starting.
 */
//Disalbed
//@Component
public class MySpringBootRouter extends RouteBuilder {

    @Value("greeting")
    private String greeting;

    @Value("${timer.period}")
    private Long timer;
    
    @Override
    public void configure() {
        from("timer://foo?fixedRate=true&period="+timer).routeId("javaDSL").to("log:"+greeting+" every "+timer);
    }

}
