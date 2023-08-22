package com.atlmh.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class DelayQueueConfig {
    public static final  String DELAY_EXCHANGE= "delay_exchange";
    public static final String DELAY_QUEUE="delay_queue";

    public static  final  String DELAY_ROUTING_KEY="delay_routing_key";

    @Bean("delayExchange")
    public CustomExchange delayExchange(){
        return new CustomExchange(DELAY_EXCHANGE,"x-delayed-message",true,false, Collections.singletonMap("x-delayed-type","direct"));
    }
    @Bean("delayQueue")
    public Queue delayQueue(){
        return QueueBuilder.durable(DELAY_QUEUE).build();
    }
    @Bean
    public Binding delayQueueBindingDelayExchange(@Qualifier("delayQueue") Queue queue,@Qualifier("delayExchange") CustomExchange customExchange){
        return BindingBuilder.bind(queue).to(customExchange).with(DELAY_ROUTING_KEY).noargs();
    }
}
