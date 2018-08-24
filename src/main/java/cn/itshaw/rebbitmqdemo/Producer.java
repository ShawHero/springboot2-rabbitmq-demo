package cn.itshaw.rebbitmqdemo;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Producer {

    private final AmqpTemplate amqpTemplate;

    @Autowired
    public Producer(RabbitTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public Boolean sendMessage(String msg){
        try {
            amqpTemplate.convertAndSend("shaw.exchange", "shaw.routingKey", msg);
            System.out.println("send message:"+msg);
        }catch (AmqpException ex){
            ex.printStackTrace();
            System.out.println("send message exception:"+ex.getMessage());
            return false;
        }
        return true;
    }

}
