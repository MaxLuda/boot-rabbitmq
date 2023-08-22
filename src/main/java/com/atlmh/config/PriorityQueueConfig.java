package com.atlmh.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class PriorityQueueConfig {

    public static final String PRIORITY_EXCHANGE="priorityExchange";
    public static final String PRIORITY_QUEUE="priorityQueue";
    public static final String PRIORITY_ROUTING_KEY="priorityRoutingKey";

    @Bean("priorityExchange")
    public  DirectExchange directExchange(){
        return ExchangeBuilder.directExchange(PRIORITY_EXCHANGE).build();
    }

    @Bean("priorityQueue")
    public Queue queue(){
        return QueueBuilder.durable(PRIORITY_QUEUE).withArgument("x-max-priority",10).build();
    }

    @Bean
    public Binding binding(@Qualifier("priorityQueue") Queue queue,@Qualifier("priorityExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(PRIORITY_ROUTING_KEY).noargs();
    }
}
