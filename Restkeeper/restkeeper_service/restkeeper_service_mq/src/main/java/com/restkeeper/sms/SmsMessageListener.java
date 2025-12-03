package com.restkeeper.sms;

import com.restkeeper.constants.SystemCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmsMessageListener {

    @RabbitListener(queues = SystemCode.SMS_ACCOUNT_QUEUE)
    public void getAccountMessage(String message){
        log.info("发送短信监听类接收到消息："+message);
      String filename = "/home/hengyu/tmp/java/practice_java/Restkeeper/config/enterpriseAccount.txt";

      try {
        // 如果文件不存在会创建，存在则追加
        Files.write(Paths.get(filename), message.getBytes(),
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND);
        System.out.println("内容已成功写入文件");

      } catch (IOException e) {
        e.printStackTrace();
      }

    }
}
