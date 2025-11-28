package com.restkeeper.operator.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConfig {
    public static final String ACCOUNT_QUEUE = "account_queue";

    public static final String ACCOUNT_QUEUE_KEY = "account_queue_key";

    public static final String SMS_EXCHANGE = "sms_exchange";


    @Bean(ACCOUNT_QUEUE)
    public Queue acccountQueue(){
        Queue queue = new Queue(ACCOUNT_QUEUE);
        return queue;
    }

    @Bean(SMS_EXCHANGE)
    public Exchange smsExchange(){
        return ExchangeBuilder.directExchange(SMS_EXCHANGE).build();
    }


    @Bean
    public Binding accountQueueToSmsExchange(@Qualifier(ACCOUNT_QUEUE) Queue queue, @Qualifier(SMS_EXCHANGE) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ACCOUNT_QUEUE_KEY).noargs();
    }

}
