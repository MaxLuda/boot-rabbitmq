package com.atlmh.consumer;

import com.atlmh.config.PublishConfirmConfig;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class PublishConfirmConsumer {

    @RabbitListener(queues = PublishConfirmConfig.PUBLISH_CONFIRM_QUEUE)
    public void receiveMsg(Message message, Channel channel){
        String s = new String(message.getBody());
        log.info("当前时间：{}，收到确认队列的消息：{}",new Date().toString(),s);
    }
}
