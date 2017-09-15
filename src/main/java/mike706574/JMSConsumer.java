package mike706574;

import java.util.function.Consumer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import org.apache.commons.lang3.exception.ExceptionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMSConsumer {
    private final Logger log = LoggerFactory.getLogger( JMSConsumer.class );

    private final String consumerId;
    private final ConnectionFactory connFactory;
    private final Consumer<String> handler;

    private Connection conn;

    public JMSConsumer( String consumerId,
                        ConnectionFactory connFactory,
                        Consumer<String> handler ) {
        this.consumerId = consumerId;
        this.connFactory = connFactory;
        this.handler = handler;
    }

    public void start( String queueName ) {
        try {
            info( "Starting." );

            conn = connFactory.createConnection();
            Session session = conn.createSession( false,
                                                  javax.jms.Session.AUTO_ACKNOWLEDGE );

            MessageListener listener = new MessageListener() {
                    @Override
                    public void onMessage( Message message ) {
                        try {
                            debug( "Processing message." );
                            handler.accept( ((TextMessage)message).getText() );
                        }
                        catch( Exception ex ) {
                            exception( "Exception thrown by message handler.", ex );
                        }
                    }
                };

            Destination dest = session.createQueue( queueName );
            MessageConsumer consumer = session.createConsumer( dest );
            consumer.setMessageListener( listener );
            conn.start();
        }
        catch( JMSException ex ) {
            throw new ConsumerException( ex );
        }
    }

    public void stop() {
        try {
            info( "Stopping." );
            conn.close();
            conn = null;
        }
        catch( JMSException ex ) {
            throw new ConsumerException( ex );
        }
    }

    private void debug( String message ) { log.debug( logLine( message ) ); }
    private void error( String message ) { log.error( logLine( message ) ); }
    private void info( String message ) { log.info( logLine( message ) ); }
    private void trace( String message ) { log.trace( logLine( message ) ); }
    private void exception( String message, Exception ex ) {
        error( String.format( "An exception was thrown.\n",
                              ExceptionUtils.getStackTrace( ex ) ) );
    }

    private String logLine( String message ) {
        return String.format( "[consumer-%s] %s", consumerId, message );
    }
}
