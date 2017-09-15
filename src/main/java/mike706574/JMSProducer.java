package mike706574;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class JMSProducer {
    private final Logger log = LoggerFactory.getLogger(JMSProducer.class);

    private final String producerId;
    private final ConnectionFactory connFactory;

    public JMSProducer(String producerId,
                       ConnectionFactory connFactory) {
        this.producerId = producerId;
        this.connFactory = connFactory;
    }

    public void sendTextMessage(String queueName, String message) {
        try {
            Connection conn = null;

            try {
                conn = connFactory.createConnection();
                Session session = conn.createSession(false,
                        javax.jms.Session.AUTO_ACKNOWLEDGE);
                MessageProducer producer = session.createProducer(null);
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                info(String.format("Publishing message to queue %s.",
                        queueName));

                Destination destination = session.createQueue(queueName);

                Message textMessage = session.createTextMessage(message);

                producer.send(destination, textMessage);
                info("Published message: " + message);
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        } catch (JMSException ex) {
            throw new ProducerException(ex);
        }
    }

    private void info(String message) {
        log.info(String.format("[producer-%s] %s", producerId, message));
    }
}
