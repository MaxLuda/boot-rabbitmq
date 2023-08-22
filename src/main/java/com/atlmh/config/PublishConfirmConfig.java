package com.atlmh.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class PublishConfirmConfig {
    public static final String PUBLISH_CONFIRM_EXCHANGE="publish_confirm_exchange";
    public static final String PUBLISH_CONFIRM_QUEUE="publish_confirm_queue";
    public static final String PUBLISH_CONFIRM_ROUTING_KEY="publish_confirm_routing_key";
    public static final String BACKUP_EXCHANGE="backup_exchange";
    public static final String BACKUP_QUEUE="backup_queue";
    public static final String WARNING_QUEUE="warning_queue";

    @Bean
    public DirectExchange publishConfirmExchange(){
        return new DirectExchange(PUBLISH_CONFIRM_EXCHANGE,true,false, Collections.singletonMap("alternate-exchange",BACKUP_EXCHANGE));
    }
    @Bean("backupExchange")
    public FanoutExchange backupExchange(){
        return ExchangeBuilder.fanoutExchange(BACKUP_EXCHANGE).durable(true).build();
    }
    @Bean
    public Queue publishConfirmQueue(){
        return QueueBuilder.durable(PUBLISH_CONFIRM_QUEUE).build();
    }
    @Bean
    public Queue backupQueue(){
        return QueueBuilder.durable(BACKUP_QUEUE).build();
    }
    @Bean
    Queue warningQueue(){
        return QueueBuilder.durable(WARNING_QUEUE).build();
    }
    @Bean
    public Binding publishConfirmRoutingKey(@Qualifier("publishConfirmExchange")Exchange exchange,@Qualifier("publishConfirmQueue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with(PUBLISH_CONFIRM_ROUTING_KEY).noargs();
    }
    @Bean Binding backupQueueBindingBackupExchange(@Qualifier("backupQueue") Queue queue,@Qualifier("backupExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }
    @Bean Binding warningQueueBindingBackupExchange(@Qualifier("warningQueue") Queue queue,@Qualifier("backupExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }

}
