package com.atlmh.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TtlQueueConfig {
    /**
     * 定义普通交换机的名字
     */
    public static final String X_EXCHANGE = "X";
    /**
     * 定义死信交换机的名字
     */
    public static final String Y_DEAD_LETTER_EXCHANGE = "Y";
    /**
     * 定义另外一个死信交换机用来解决延迟队列问题，
     * 插件处理
     */
    public static final String YY_DEAD_LETTER_EXCHANGE = "YY";
    /**
     * 定义普通队列的名称
     */
    public static final String QUEUE_A = "QA";
    public static final String QUEUE_B = "QB";
    public static final String QUEUE_C = "QC";
    /**
     * 定义死信队列的名称
     */
    public static final String DEAD_LETTER_QUEUE_D = "QD";
    public static final String YY_DEAD_LETTER_QUEUE_E = "QE";

    /**
     * 声明x普通换机的别名
     */
    @Bean("xExchange")
    public DirectExchange xExchange(){

        DirectExchange directExchange = new DirectExchange(X_EXCHANGE);
        directExchange.setDelayed(true);
        return directExchange;
    }
    /**
     * 声明死信交换机的别名
     */
    @Bean("yExchange")
    public DirectExchange yExchange(){
        return new DirectExchange(Y_DEAD_LETTER_EXCHANGE);
    }
    /**
     * 声明另外一个死信交换机主要用于解决延迟队列的问题
     * 用自定义交换机声明
     */
    @Bean("yyExchange")
    public CustomExchange yyExchange(){
        Map<String, Object> map = new HashMap<>();
        map.put("x-delayed-type","direct");
        /**
         * 交换机的名称、交换机的类型、是否共享，是否自动删除、其它的参数
         */
        return new CustomExchange(YY_DEAD_LETTER_EXCHANGE,"x-delayed-message",true,false,map);
    }

    /**
     * 声明队列普通队列A
     */
    @Bean("queueA")
    public Queue queueA(){
        Map<String,Object> map = new HashMap<>();
        /**
         *设置死信交换机，设置死信Routing——key，设置ttl，单位ms
         */
        map.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
        map.put("x-dead-letter-routing-key","YD");
        map.put("x-message-ttl",10000);
        return QueueBuilder.durable(QUEUE_A).withArguments(map).build();
    }
    /**
     * 声明队列普通队列B
     */
    @Bean("queueB")
    public Queue queueB(){
        Map<String,Object> map = new HashMap<>();
        /**
         *设置死信交换机，设置死信Routing——key，设置ttl，单位ms
         */
        map.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
        map.put("x-dead-letter-routing-key","YD");
        map.put("x-message-ttl",40000);
        return QueueBuilder.durable(QUEUE_B).withArguments(map).build();
    }

    /**
     * 声明队列普通队列C,不设置超时时间，
     * 不解决延时队列的问题，延时由生产者动态指定
     */
    @Bean("queueC")
    public Queue queueC(){
        Map<String, Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
        map.put("x-dead-letter-routing-key","YD");
        return QueueBuilder.durable(QUEUE_C).withArguments(map).build();
    }

    /**
     * 声明队列普通队列C,不设置超时时间，有生产者指定
     */
//   @Bean("queueC")
//   public Queue queueC(){
//       Map<String, Object> map = new HashMap<>();
//       map.put("x-dead-letter-exchange",YY_DEAD_LETTER_EXCHANGE);
//       map.put("x-dead-letter-routing-key","YYD");
//       return QueueBuilder.durable(QUEUE_C).withArguments(map).build();
//   }

    /**
     * 声明死信队列D
     */
    @Bean("queueD")
    public Queue queueD(){
        return QueueBuilder.durable(DEAD_LETTER_QUEUE_D).build();
    }

    /**
     * 声明延时死信队列E
     */
    @Bean("queueE")
    public Queue queueE(){
        return QueueBuilder.durable(YY_DEAD_LETTER_QUEUE_E).build();
    }
    /**
     * 声明队列A与普通交换机X的绑定关系
     */
    @Bean
    public Binding queueABindingX(@Qualifier("queueA") Queue queueA, @Qualifier("xExchange") Exchange xExchange){
        return BindingBuilder.bind(queueA).to(xExchange).with("XA").noargs();
    }
    /**
     * 声明队列B与普通交换机X的绑定关系
     */
    @Bean
    public Binding queueBBindingX(@Qualifier("queueB") Queue queueB, @Qualifier("xExchange") Exchange xExchange){
        return BindingBuilder.bind(queueB).to(xExchange).with("XB").noargs();
    }
    /**
     * 声明队列C与普通交换机X的绑定关系
     */
    @Bean
    public Binding queueCBindingX(@Qualifier("queueC") Queue queueC, @Qualifier("xExchange") Exchange xExchange){
        return BindingBuilder.bind(queueC).to(xExchange).with("XC").noargs();
    }
    /**
     * 声明死信队列D与死信交换机Y的绑定关系
     */
    @Bean
    public Binding queueDBindingY(@Qualifier("queueD") Queue queueD, @Qualifier("yExchange") Exchange yExchange){
        return BindingBuilder.bind(queueD).to(yExchange).with("YD").noargs();
    }
    /**
     * 绑定死信队列E和死信交换机YY的绑定关系
     */
    @Bean
    public Binding queueEBindingYY(@Qualifier("queueE") Queue queueE, @Qualifier("yyExchange") CustomExchange yyExchange){
        return BindingBuilder.bind(queueE).to(yyExchange).with("YYD").noargs();
    }

}
