package cn.itshaw.rebbitmqdemo;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ProducerConfig {

    @Bean
    RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    Queue queue(RabbitAdmin rabbitAdmin) {
        Queue queue = new Queue("shaw.queue", true);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    @Bean
    Exchange exchange(RabbitAdmin rabbitAdmin) {
        DirectExchange exchange = new DirectExchange("shaw.exchange");
        rabbitAdmin.declareExchange(exchange);
        return exchange;
    }

    @Bean
    Binding bindingExchange(Queue queueFoo, DirectExchange exchange,RabbitAdmin rabbitAdmin) {
        Binding binding = BindingBuilder.bind(queueFoo).to(exchange).with("shaw.routingKey");
        rabbitAdmin.declareBinding(binding);
        return binding;
    }

}
