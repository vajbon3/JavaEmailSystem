import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class UserSystem<T1> {
    // number of tries to login
    private int tries = 0;
    // logged in user, by default null
    public T1 active = null;

    public String server;
    // user array for registered users
    public ArrayList<User> users;

    // secret key for password changes and registrations
    protected String key = "";

    // array for threads and executor service
    protected ArrayList<CustomThread> threads;
    protected ExecutorService exec;

    // for database
    ResultSet rs;
    Connection conn;
    Statement stmt;

    public UserSystem(String server) throws SQLException {
        this.server = server;

        // instantiate users array
        this.users = new ArrayList<>();

        // instantiate thread array and ExecutorService
        threads = new ArrayList<>();
        exec = Executors.newFixedThreadPool(10);

        // generate 10 digit key
        Random rand = new Random();
        for(int i=0;i<10;i++) this.key += String.valueOf(rand.nextInt(10));

        // connect to database
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/project","root","");
        stmt = conn.createStatement();
        System.out.println("Connected to Database");

        // fetch users
        fetchUsers();
    }

    // helper method for executing a SQL query
    protected void query(String q) throws SQLException {
        rs = stmt.executeQuery(q);
    }

    // fetch all registered users from database
    protected void fetchUsers() throws SQLException {
        query("SELECT * FROM users");

        // add to users ArrayList
        while(rs.next()) {
            users.add(new User(rs.getString("email"),rs.getString("password"),key));
        }

        System.out.println("Users fetched");
    }

    // login function : returns true if successful
    public boolean login(String email,String password) {
        // check if email and password matches
        for(User u : users) {
            if (email.equalsIgnoreCase(u.email)) {
                if (u.getPassword(key).equals(password)) {
                    active = (T1) u;
                    return true;
                } else break;
            }
        }

        tries++;
        return false;
    }

    // register user
    public void register(String username, String password) {
        // make a new RegisterThread
        User u = new User(username.toLowerCase()+'@'+server,password,key);
        CustomThread t = new CustomThread(u,stmt,key);

        // add to thread array
        threads.add(t);
    }

    // run registration threads using ExecutorService
    public void startRegistration() throws InterruptedException, SQLException {
        for(CustomThread t : threads) {
            exec.execute(t);
        }

        // wait for threads
        exec.shutdown();
        while (!exec.isTerminated()) {
            Thread.sleep(100);
        }
        System.out.println("Finished all registrations");
        // clear users array, Threads array
        // and Executor and fetch again for new users
        users.clear();
        threads.clear();
        exec = Executors.newFixedThreadPool(10);
        fetchUsers();
    }

    // logs client in and starts program loop
    public void start() throws SQLException, FileNotFoundException {
        // instantiate scanner
        Scanner sc = new Scanner(System.in);
        // login screen
        System.out.println("Welcome to " + server);
        System.out.println("You need to be logged in");

        while(true) {
            // too many tries check
            if(tries == 3) {
                System.out.println("too many tries");
                // terminate java program
                System.exit(1);
            } else if(tries > 0) System.out.println("try again");

            // input credentials
            System.out.println("input your email: ");
            String email = sc.nextLine();

            System.out.println("input your password: ");
            String password = sc.nextLine();

            // try to login
            if(login(email,password)) {
                System.out.println("log in successful");
                break;
            } else System.out.println("Incorrect credentials");
        }

        // fetch and start loop
        fetch();
        run();
    }

    // abstract fetch and run functions to override for any child classes
    protected abstract void fetch() throws SQLException;

    protected abstract void run() throws FileNotFoundException;
}
