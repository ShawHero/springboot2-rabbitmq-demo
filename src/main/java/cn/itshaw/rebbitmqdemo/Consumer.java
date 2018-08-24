package cn.itshaw.rebbitmqdemo;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Consumer {

    @RabbitListener(queues = "shaw.queue")
    public void processMessage(String msg,Channel channel, Message message) {
        System.out.println("receive message:" + msg);
        try {
            //如果业务处理成功则ack确认，否则不执行ack确认
            if(true){
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
