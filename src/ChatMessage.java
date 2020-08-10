import java.io.Serializable;

/**
 *
 * [Add your documentation here]
 *
 * @author your name and section
 * @version date
 */
final class ChatMessage implements Serializable {
    private static final long serialVersionUID = 6898543889087L;
    // Here is where you should implement the chat message object.
    // Variables, Constructors, Methods, etc.

    private String message;
    private int type;
    private String recipient;

    public ChatMessage(String message, int type, String recipient) {
        this.message = message;
        this.type = type;
        this.recipient = recipient;
    }

    public ChatMessage(String message, int type) {
        this.message = message;
        this.type = type;
        recipient = "";
    }

    public String getMessage() {
        return message;
    }
    public String getRecipient(){return recipient;}
    public int getType() {
        return type;
    }


}
