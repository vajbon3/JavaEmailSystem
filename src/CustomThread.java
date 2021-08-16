import java.sql.SQLException;
import java.sql.Statement;

public class CustomThread implements Runnable{
    Object o;
    Statement stmt;
    String key;

    public CustomThread(Object o, Statement stmt, String key) {
        this.o = o;
        this.stmt = stmt;
        this.key = key;
    }

    @Override
    public void run() {
        if(o instanceof User)
            register();
        else if(o instanceof Letter)
            send();
    }

    public void register() {
        User u = (User) o;
        try {
            stmt.execute("INSERT INTO users" +
                    " VALUES('" + u.email + "','" + u.getPassword(key) + "');");
        } catch (SQLException throwable) {
            System.out.println("couldn't register user");
            return;
        }
        System.out.println("Registration Successful");
    }

    public void send() {
        Letter l = (Letter) o;

        try {
            stmt.execute("INSERT INTO letters" +
                    " VALUES('" + l.sender + "','" + l.recipient +
                    "','" + l.subject + "','" + l.body + "',now());");
        } catch (SQLException e) {
            System.out.println("letter couldn't be sent");
        }
    }
}
