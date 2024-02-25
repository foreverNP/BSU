package message;

import java.io.Serializable;

import protocol.*;
import menu.*;

public class MessageGetMenuResult extends MessageResult implements Serializable {
    private static final long serialVersionUID = 1L;

    public Menu menu;

    public MessageGetMenuResult(String errorMessage) {

        super(Protocol.REQUEST_GET_MENU, errorMessage);
    }

    public MessageGetMenuResult(Menu menu) { // No errors
        super(Protocol.REQUEST_GET_MENU);
        this.menu = menu;
    }
}
