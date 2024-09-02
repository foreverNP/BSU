package message;

import java.io.Serializable;

import protocol.*;

public class MessageSendOrderResult extends MessageResult implements Serializable {
    private static final long serialVersionUID = 1L;

    public MessageSendOrderResult(String errorMessage) { //Error

        super(Protocol.REQUEST_SEND_ORDER, errorMessage);
    }

    public MessageSendOrderResult() { // No errors

        super(Protocol.REQUEST_SEND_ORDER);
    }
}
