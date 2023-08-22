package com.atlmh.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListeners;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class DeadLetterQueueConsumer {

    //接受消息
    @RabbitListener(queues = "QE")
    public void receiveD(Message msg, Channel channel){
        String s = new String(msg.getBody());
        log.info("当前时间：{}，收到死信队列的消息：{}",new Date().toString(),s);
    }
}
