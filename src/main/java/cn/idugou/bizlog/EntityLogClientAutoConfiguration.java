package cn.idugou.bizlog;

import com.pflm.common.entitylog.receiver.EntityLogReceiver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @Author : hejun
 * @Create : 2020/10/15 11:22
 * @Version : 1.0.1
 * @Copyright : Copyright (c) 2020
 * @Description :
 */
@Configuration
@EnableConfigurationProperties({EntityLogProperties.class})
public class EntityLogClientAutoConfiguration {

    @Autowired
    private EntityLogProperties entityLogProperties;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Bean
    public EntityLogReceiver entityLogReceiver() { return new EntityLogReceiver(); }


    @Bean
    public Queue dataLogMsgQueue(Environment env) {
        String applicationName = env.getProperty("spring.application.name");
        String queueName = entityLogProperties.getQueueName();
        if (queueName == null && StringUtils.isNotEmpty(applicationName)) {

            queueName = "entitylog";
            queueName = queueName + "." + applicationName;
            return QueueBuilder.durable(queueName).build();
        }
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    @ConditionalOnProperty(name = {"mq.receiver.enable"}, havingValue = "true", matchIfMissing = true)
    public MessageListenerContainer entityLogListenerContainer(EntityLogReceiver entityLogReceiver, Queue dataLogMsgQueue) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(new Queue[] { dataLogMsgQueue });
        container.setPrefetchCount(20);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setMessageListener(entityLogReceiver);
        return container;
    }
}
