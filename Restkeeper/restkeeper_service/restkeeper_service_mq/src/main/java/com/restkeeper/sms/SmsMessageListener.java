package com.restkeeper.sms;

import com.restkeeper.constants.SystemCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmsMessageListener {

    @RabbitListener(queues = SystemCode.SMS_ACCOUNT_QUEUE)
    public void getAccountMessage(String message){
        log.info("发送短信监听类接收到消息："+message);

    }
}
