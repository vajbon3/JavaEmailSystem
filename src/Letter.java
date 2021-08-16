
public class Letter {
    public String sender;
    public String recipient;
    public String subject;
    public String body;

    public Letter(String sender,
                  String recipient,
                  String subject,
                  String body) {
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
    }

    @Override
    public String toString() {
        return  "from " + sender +
                "     subject: " + subject;
    }
}
