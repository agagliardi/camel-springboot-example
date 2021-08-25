package com.redhat.reproducer;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.zipfile.ZipAggregationStrategy;
import org.springframework.stereotype.Component;

/**
 * A simple Camel route that triggers from a timer and calls a bean and prints
 * to system out.
 * <p/>
 * Use <tt>@Component</tt> to make Camel auto detect this route when starting.
 */
@Component
public class MySpringBootRouter extends RouteBuilder {

    private boolean warmup = System.getProperty("warmup") != null;
    private String inFolder = System.getProperty("inFolder", "/tmp/input");
    private String outFolder = System.getProperty("outFolder", "/tmp/output");
    private byte[] fileContent = System.getProperty("fileContent", "This Is test File").getBytes();

    private int fileCount = Integer.parseInt(System.getProperty("fileCount", "100"));

    private static final AtomicLong COUNTER = new AtomicLong();
    private static final AtomicLong START = new AtomicLong(-1);

    @Override
    public void configure() {

        if (warmup) {
            from("timer://foo?fixedRate=true&period=100&repeatCount=" + fileCount).routeId("test-file-producer")
                .setBody(() -> fileContent)
                .to("file:" + inFolder)
                .process(exchange -> System.out.println("File created:" + exchange.getIn().getHeader("CamelFileNameProduced")));
        } else {            
            from("file:" + inFolder + "?noop=true&maxMessagesPerPoll=" + fileCount).routeId("test-file-zipper")
            .process(new Processor(){
                @Override
                public void process(Exchange exchange) throws Exception {
                    Message in = exchange.getIn();
                    long start = START.get();
                    if(start<0){
                        START.set(start = System.currentTimeMillis());
                    }
                    long counter = COUNTER.getAndIncrement();
                    System.err.println(((double)(System.currentTimeMillis()-start)/1000));
                    double throughput = counter/((double)(System.currentTimeMillis()-start)/1000);
                    in.setHeader("counter", counter);
                    in.setHeader("throughput",throughput);
                }})
            .log(LoggingLevel.INFO, this.getClass().getName(),"File ${header.counter};throughput File/s ${header.throughput}")
                    .aggregate(constant(true), new ZipAggregationStrategy()).completionFromBatchConsumer()
                    .eagerCheckCompletion()
                    // .completionSize(3421)
                    .to("file:" + outFolder).process(new Processor() {

                        @Override
                        public void process(Exchange exchange) throws Exception {
                            //this is the aggregation exchange not the aggregated ones, so it's ok to use the header "Timer"
                            Message in = exchange.getIn();
                            System.out.println("Zip File created in "
                                    + (System.currentTimeMillis() - START.get()) + "ms ("
                                    + in.getHeader("CamelFileNameProduced") + ")");
                        }
                    });

        }

    }

}
