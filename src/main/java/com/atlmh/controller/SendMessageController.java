package com.atlmh.controller;

import com.atlmh.config.DelayQueueConfig;
import com.atlmh.config.PriorityQueueConfig;
import com.atlmh.config.PublishConfirmConfig;
import com.atlmh.config.TtlQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping("/ttl")
@Slf4j
public class SendMessageController {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 生产者1
     * 进行消息的发送
     */
    @GetMapping("/sendMessage/{message}")
    public void sendMsg(@PathVariable("message") String message){
        log.info("当前时间：{}，发送一条消息给两个TTl队列：{}",new Date().toString(),message);
        rabbitTemplate.convertAndSend("X","XA","消息来自ttl为10s的队列："+message);
        rabbitTemplate.convertAndSend("X","XB","消息来自ttl为40s的队列："+message);
    }
    /**
     * 生产者2
     * 进行消息的发送
     */
    @GetMapping("/sendExpirationMsg/{message}/{ttlTime}")
    public  void sendTtlMsg(@PathVariable("message") String message,@PathVariable("ttlTime") String ttlTime){
        log.info("当前时间：{}，发送一条时长{}毫秒TTL消息给队列QC：{}",new Date().toString(),ttlTime,message);
        rabbitTemplate.convertAndSend("X","XC",message, msg -> {
            msg.getMessageProperties().setExpiration(ttlTime);
            return msg;
        });
    }

    /**
     * 生产者3
     * 进行消息的发送
     */
    @GetMapping("/sendDelayMsg/{message}/{delayTime}")
    public  void sendDelayMsg(@PathVariable("message") String message,@PathVariable("delayTime") String ttlTime){
        log.info("当前时间：{}，发送一条时长{}毫秒TTL消息给队列QC：{}",new Date().toString(),ttlTime,message);
        rabbitTemplate.convertAndSend(TtlQueueConfig.X_EXCHANGE,"XC",message, msg -> {
            msg.getMessageProperties().setExpiration(ttlTime);
            return msg;
        });
    }

    /**
     * 生产者4
     * 进行消息发布确认高级的发送
     */
    @GetMapping("/sendConfirmMsg/{message}")
    public  void sendDelayMsg(@PathVariable("message") String message){
        log.info("当前时间：{}，发送一条确认消息给确认队列：{}",new Date().toString(),message);
        rabbitTemplate.convertAndSend(PublishConfirmConfig.PUBLISH_CONFIRM_EXCHANGE,PublishConfirmConfig.PUBLISH_CONFIRM_ROUTING_KEY,
                message,new CorrelationData("1"));
        rabbitTemplate.convertAndSend(PublishConfirmConfig.PUBLISH_CONFIRM_EXCHANGE,PublishConfirmConfig.PUBLISH_CONFIRM_ROUTING_KEY+"32",
                message+"33",new CorrelationData("2"));
    }

    /**
     * 生产者5
     * 优先级队列发送消息
     */
    @GetMapping("/sendPriority")
    public  void sendPriority(){
        for (int i = 1; i < 11; i++) {
            log.info("发送一条消息给队列：{}",i);
            if(i==5){
                rabbitTemplate.convertAndSend(PriorityQueueConfig.PRIORITY_EXCHANGE,PriorityQueueConfig.PRIORITY_ROUTING_KEY,"第"+i+"条消息",msg->{
                    msg.getMessageProperties().setPriority(10);
                    return msg;
                });
            }else{
                rabbitTemplate.convertAndSend(PriorityQueueConfig.PRIORITY_EXCHANGE,PriorityQueueConfig.PRIORITY_ROUTING_KEY,"第"+i+"条消息");
            }
        }

    }


}
