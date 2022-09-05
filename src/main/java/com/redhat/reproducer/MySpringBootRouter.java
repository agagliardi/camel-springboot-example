package com.redhat.reproducer;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.MulticastProcessor;
import org.springframework.stereotype.Component;

/**
 * A simple Camel route that triggers from a timer and calls a bean and prints
 * to system out.
 * <p/>
 * Use <tt>@Component</tt> to make Camel auto detect this route when starting.
 */
@Component
public class MySpringBootRouter extends RouteBuilder {

    private static final String RECIPIENT_LIST_LEAK = "recipientListLeak";
    static Field field;

    static int getErrorHandlerSize(CamelContext context) throws Exception {
        if (field == null){
                field = MulticastProcessor.class.getDeclaredField("errorHandlers");
                field.setAccessible(true);
        }

        return ((Map)field.get(context.getProcessor(RECIPIENT_LIST_LEAK))).size();
    }

    @Override
    public void configure() throws Exception {
        onException(java.lang.Exception.class).redeliveryDelay(1)
                .maximumRedeliveries(3)
                .log(LoggingLevel.INFO, "retry");


                String rLURI1 = "sftp://foo@localhost:2222/upload?password=pass&passiveMode=true&throwExceptionOnConnectFailed=true&binary=true&serverAliveInterval=30000&serverAliveCountMax=999999999&maximumReconnectAttempts=0&fileName=1${headers.CamelFileName}.tmp&fastExistsCheck=true&disconnect=true&autoCreate=false";
                String rLURI2 = "sftp://foo@localhost:2222/upload?password=pass&passiveMode=true&throwExceptionOnConnectFailed=true&binary=true&serverAliveInterval=30000&serverAliveCountMax=999999999&maximumReconnectAttempts=0&fileName=2${headers.CamelFileName}.tmp&fastExistsCheck=true&disconnect=true&autoCreate=false";
                String rLURI3 = "sftp://foo@localhost:2222/upload?password=pass&passiveMode=true&throwExceptionOnConnectFailed=true&binary=true&serverAliveInterval=30000&serverAliveCountMax=999999999&maximumReconnectAttempts=0&fileName=3${headers.CamelFileName}.tmp&fastExistsCheck=true&disconnect=true&autoCreate=false";
                //"file:///tmp/foo-out-${date:now:yyyyMMddHHmmssSSS}?allowNullBody=true";

        from("file:///tmp/sftp-in-foo?maxMessagesPerPoll=100&delete=false&sortBy=file:modified;ignoreCase:file:name&sendEmptyMessageWhenIdle=true")
        .routeId("reproducer")
                .choice().when(body().isNull())
                .otherwise()
                .recipientList(
                        simple(rLURI1+"~~~"+rLURI2+"~~~"+rLURI3),"~~~")
                .cacheSize(-1)
                .id(RECIPIENT_LIST_LEAK)
                .process(exchange -> System.err.println("\nError Handlers "+ getErrorHandlerSize(exchange.getContext()) + "\n"))
                .log("end exchange");
    }

}
