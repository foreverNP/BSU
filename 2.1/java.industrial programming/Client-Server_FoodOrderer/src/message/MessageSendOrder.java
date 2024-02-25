package message;

import protocol.*;

import java.io.Serializable;

import order.*;

//Client send order message, extends from super-class Message
public class MessageSendOrder extends Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private Order order;

    public MessageSendOrder(Order order) {
        super(Protocol.REQUEST_SEND_ORDER);
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

}