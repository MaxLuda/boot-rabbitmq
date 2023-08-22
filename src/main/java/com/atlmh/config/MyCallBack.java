package com.atlmh.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

//@Component
@Slf4j
public class MyCallBack implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback{

    @Resource
    private RabbitTemplate rabbitTemplate;
    @PostConstruct
    void init(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    /**
     * 生产者发送消息，交换机接收到进行的回调，不管成功还是失败
     * @param correlationData  回调消息的id及相关信息
     * @param ack ture 交换机收到消息，false 发送失败
     * @param s  错误的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String s) {
        String id = correlationData == null ? "" : correlationData.getId();
        if(ack){
            log.info("交换机已经收到id为{}的消息",id);
        }else{
            log.info("交换机未收到id为{}的消息，原因：{}",id,s);
        }
    }

    /**
     * 当消息传递过程中不可达目的地时，回退给生产者
     * 只有不可达目的地时，才进行回退
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        try {
            log.error("消息{}，被交换机{}退回，错误原因时{}",new String(message.getBody(),"UTF-8"),exchange,replyText);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


}
