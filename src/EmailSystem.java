import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class EmailSystem extends UserSystem<User> {
    // array for inbox
    public ArrayList<Letter> letters;

    // scanner for input
    private final Scanner sc = new Scanner(System.in);
    public EmailSystem(String server) throws SQLException {
        super(server);
        letters = new ArrayList<>();
    }

    @Override
    public void fetch() throws SQLException {
        fetchLetters();
    }

    // fetches logged in user's inbox from the database
    private void fetchLetters() throws SQLException {
        // fetches user's inbox sorted
        // by date descending (newer first)
        query("SELECT * from letters WHERE recipient = '" +
                active.email +
                "' ORDER BY time DESC ");

        while(rs.next()) {
            // add incoming letters to letters array
            letters.add(new Letter(rs.getString("sender"),
                    rs.getString("recipient"),
                    rs.getString("subject"),
                    rs.getString("body")));
        }
    }

    public void sendEmail(String sender,String recipient,
                          String subject,String body) {
        // instantiate Letter object and
        // instantiate and execute a thread with that letter
        Letter l = new Letter(sender.toLowerCase(),recipient.toLowerCase(),subject,body);
        exec.execute(new CustomThread(l,stmt,key));
        exec.shutdown();
    }

    // program loop
    @Override
    protected void run() throws FileNotFoundException {
        printLetters();
        System.out.println("1. send a letter");
        System.out.println("2. read a letter");
        System.out.println("3. exit");

        // take input
        int input = sc.nextInt();

        if(input == 1) {
            // recipient
            System.out.println("input recipient: ");
            String recipient = sc.next();
            // subject
            System.out.println("input subject: ");
            sc.nextLine();
            String subject = sc.nextLine();
            // body
            System.out.println("input file name of your letter");
            String filename = sc.next();
            String body = readMessage(filename);

            // send
            sendEmail(active.email,recipient,subject,body);

        } else if(input == 2) {
            System.out.println("input which letter to open: ");
            int index = sc.nextInt();
            try {
                printLetter(letters.get(index));
            } catch(IndexOutOfBoundsException e) {
                System.out.println("no such letter!");
            }
        } else if(input == 3) {
            System.out.println("exiting program");
            System.exit(1);
        }
        run();
    }

    // read text from a file and return as a string
    private String readMessage(String filename) throws FileNotFoundException {
        return new Scanner(new File(filename)).useDelimiter("\\Z").next();
    }

    // print a letter with styling
    private void printLetter(Letter letter) {
        printLine();
        System.out.println("                  " + letter.subject);
        System.out.println(letter.body);
    }
    // print letters in Letters ArrayList
    private void printLetters() {
        System.out.println("              --- inbox ---      ");
        int index = 0;
        for(Letter l : letters) {
            printLine();
            System.out.println(index + " " + l);
            index++;
        }
        printLine();
    }

    // print a line for styling
    private void printLine() {
        System.out.println("------------------------------");
    }
}
