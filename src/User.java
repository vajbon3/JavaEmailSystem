public class User implements UserInterface{
    public final String email;
    public final String password;
    private final String key;

    public User(String email, String password, String key) {
        this.email = email;
        this.password = password;
        this.key = key;
    }

    public String getPassword(String key) {
        if(key.equals(this.key)) return password;
        return null;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                '}';
    }
}
