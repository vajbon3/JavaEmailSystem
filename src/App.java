import java.io.FileNotFoundException;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws SQLException, InterruptedException, FileNotFoundException {
        EmailSystem email = new EmailSystem("gmail.com");

        // email.register("Vajachelidze","vaja123");
        // email.register("Zurabgergaia","zurab123");
        // email.register("Lashautnelishvili", "lasha123");
        
        // email.startRegistration();

         email.start();

    }
}
