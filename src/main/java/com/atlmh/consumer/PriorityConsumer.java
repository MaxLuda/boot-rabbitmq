package com.atlmh.consumer;

import com.atlmh.config.PriorityQueueConfig;
import com.atlmh.config.PublishConfirmConfig;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class PriorityConsumer {

    @RabbitListener(queues = {PriorityQueueConfig.PRIORITY_QUEUE})
    public void receiveMsg(Message message, Channel channel){
        String s = new String(message.getBody());
        log.info("收到优先级队列的消息：{}",s);
    }
}
