package com.tianji.jingsuan.module;

import android.util.Log;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import com.tianji.jingsuan.utils.MqObject;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by chencentury on 2018/4/28.
 */

public class MQHelper {
    public static final String TAG = "======>";
    private static MQHelper mqHelper = null;
    private MQMessageListener mqMessageListener;
    private Connection conn;

    public static MQHelper getMqHelper() {
        if (mqHelper == null) {
            mqHelper = new MQHelper();
        }
        return mqHelper;
    }

    public interface MQMessageListener {
        void getMessage(String consumerTag, long deliveryTag, String message);
    }

    public void setMQMessageListener(MQMessageListener mqMessageListener) {
        this.mqMessageListener = mqMessageListener;
    }

    /**
     * RabbitMQ的客户端顶层包为com.rabbitmq.client，最关键（使用度最高）的类和接口有以下几个：
     * 1、Connection：连接，对于RabbitMQ而言，就是一个客户端与服务端(broker )之间的TCP连接（在逻辑上是一个客户端与broker之间有只有一个connection，但是实际上可能有多个tcp连接）
     * 2、ConnectionFactory：用来实例化Connection
     * 3、Channel:信道，仅仅创建了客户端与brokre之间的连接后并不能发送消息，需要为每一个connection创建channle，AMQP协议规定只能通过channel才能发送指令，
     * 一个conncetion可以包含多个channel。RabbitMQ建议客户端线程之间不要共用Channel，至少要保证共用Channel的线程发送消息必须是串行的，
     * 但是建议尽量共用Connection。
     * 4、Consumer：消费者，接收到消息的实体类，用于处理接收到的消息。
     * 一些注意事项：
     * 1) Consumers(注意是复数，所有的consumer)的回调执行的线程是与connection的管理线程单独分开管理的，因此可以在Consumers的消息处理回调中处理一些耗时或阻塞执行的任务。
     * 2) 每一个channel都有一个自己独立的线程，最常用的使用方法是每一个channel只有一个consumer，这样consumer就不会阻塞住其他的consumer。
     * 如果一个channel中有多个consumer的话，要避免在consumer中处理耗时任务，因为会阻塞住其他的consumer的回调。
     */

    public boolean connectRabbitMQServer(MqObject msg) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(msg.getUserName());
        factory.setPassword(msg.getPassword());
        factory.setVirtualHost(msg.getVirtualHost()); // 没有VirtualHost可以不填
        factory.setHost(msg.getHost()); // 服务器不一样要进行设置过，192.168.7.33
        factory.setPort(msg.getPort()); // 5672
        // 设置自动恢复
//            factory.setAutomaticRecoveryEnabled(true);
//            factory.setNetworkRecoveryInterval(5000);// 设置 每10s ，重试一次
//            factory.setTopologyRecoveryEnabled(false);// 设置不重新声明交换器，队列等信息。

        try {
            // 一、与我们的RabbitMQ服务器建立连接
            conn = factory.newConnection();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (TimeoutException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Connection getConn() {
        return conn;
    }

    public Channel getChannelMemberExchange() {
        return channelMemberExchange;
    }

    // 二、创建channel（创建一个通道）
    // 这里需要三个channel,三个consumers,单独管理，消息相互就不会阻塞
    private Channel channelMemberExchange; // 入口门 通道
    public static final String memberMsgExchange = "memberMsgExchange"; // ，服务器指定的Exchange
    private String memberEntranceMsgQueue = "GdaiDeviceOrderPayMsg_"; // 开入口门消息对列
    private String memberEntranceMsgRoutingKey = "memberEntranceMsg_"; // 开入口门消息RoutingKey
    public static final String consumerTag_MemberEntrance = "consumerTag_MemberEntrance"; //消费者标签

    /**
     * 声明一个持久化、不自动删除的exchange
     *
     * @param xMessageTtl：消息过期时间 60
     * @param xExpires：队列过期时间    直接传null
     */
    public boolean createChannel(String deviceMac, Long xMessageTtl, Long xExpires) {
        String deviceType = "guide_tablet";
        String mqQueue = memberEntranceMsgQueue + deviceType + "_" + deviceMac;
        Log.i(TAG, "createChannel: " + mqQueue);
        // 入口门
        try {
            // 是否收到消息后就给服务器回复ack包，服务器收到ack包会删除queue中的消息（一次交付）
            boolean autoAck = false;

            channelMemberExchange = conn.createChannel();
            // 三、 如果是多个客户端要使用同一个queue的话，则必须指定queue的名称，不能采用匿名的方式创建queue。
            // 注意 如果声明一个已经存在的queue或exchange时，声明的属性(持久化、自动删除等)必须与服务器一致，否则会抛出以下异常：
            // inequivalent arg 'durable' for queue '...' in vhost '/': received 'false' but current is 'true', class-id=50, method-id=10)
            /**
             * 声明一个持久化、不自动删除的exchange
             * @param exchange：exchange的名称
             * @param type：exchange的类型，取值有三个:fanout、direct、topic
             * @param durable:是否持久话
             * @throws IOException if an error is encountered
             */
            channelMemberExchange.exchangeDeclare(memberMsgExchange, "direct", true);
            Map<String, Object> arguments = new HashMap<String, Object>();
            if (xMessageTtl != null && xMessageTtl.longValue() > 0L) {
                arguments.put("x-message-ttl", xMessageTtl * 1000); //（单位：毫秒）
            }
            if (xExpires != null && xExpires.longValue() > 0L) {
                arguments.put("x-expires", xExpires * 1000); //（单位：毫秒）
            }
            if (arguments.isEmpty()) {
                arguments = null;
            }
            /**
             * 声明一个queue
             * @param queue：queue名称
             * @param durable：是否持久化
             * @param exclusive： 是否独占，仅限于当前connection
             * @param autoDelete ：是否自动删除，不使用时自动删除
             * @param arguments other properties (construction arguments) for the queue
             * @throws IOException if an error is encountered
             */
            // 声明订单消息队列
            channelMemberExchange.queueDeclare(mqQueue, true, false, false, arguments);
            // 将队列绑定到消息交换机exchange上
            // routingKey路由关键字，exchange根据这个关键字进行消息投递
            channelMemberExchange.queueBind(mqQueue, memberMsgExchange, mqQueue);

            channelMemberExchange.basicConsume(mqQueue, autoAck, consumerTag_MemberEntrance,
                    new DefaultConsumer(channelMemberExchange) {
                        @Override
                        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                            String routingKey = envelope.getRoutingKey();
                            String contentType = properties.getContentType();
                            long deliveryTag = envelope.getDeliveryTag();

//                            System.out.println("handleDelivery()方法::"
//                                    + "\nconsumerTag:" + consumerTag
//                                    + "\nroutingKey:" + routingKey
//                                    + "\ncontentType:" + contentType
//                                    + "\ndeliveryTag:" + deliveryTag);

                            String message = new String(body);
                            System.out.println("接收订单消息：" + message);

                            mqMessageListener.getMessage(consumerTag, deliveryTag, message);
                        }

                        // 在其他回调之前调用，能够得到consumerTag
                        @Override
                        public void handleConsumeOk(String consumerTag) {
                            super.handleConsumeOk(consumerTag);
                            System.out.println("在其他回调之前调用，能够得到consumerTag:" + consumerTag);
                        }

                        // 取消消费者的回调，在试验中此回调并未执行
                        @Override
                        public void handleCancel(String consumerTag) throws IOException {
                            super.handleCancel(consumerTag);
                            System.out.println("取消消费者的回调，在试验中此回调并未执行:" + consumerTag);
                        }

                        // 当channel和connection关闭执行的回调
                        @Override
                        public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                            super.handleShutdownSignal(consumerTag, sig);
                            System.out.println("当channel和connection关闭执行的回调:" + consumerTag + ",sig:" + sig.getMessage());
                        }

                    });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "链接失败: " + e.toString());
            return false;
        }
    }

    // 删除消息（业务处理完删除消息）
    public boolean deleteMessage(String consumerTag, long deliveryTag) {
        // 给服务器回复ack包，服务器会删除消息(业务处理完删除消息)
        // multiple是消费收到多条消息的意思
        try {
            switch (consumerTag) {
                case consumerTag_MemberEntrance: // 清除开入口门消息
                    if (channelMemberExchange != null && channelMemberExchange.isOpen()) {
                        channelMemberExchange.basicAck(deliveryTag, false);
                        return true;
                    }
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    // 三、断开连接时，需要关闭所有的channel和connection
    public void close() {
        try {
            if (conn != null && conn.isOpen())
                conn.close();
            if (channelMemberExchange != null && channelMemberExchange.isOpen())
                channelMemberExchange.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
