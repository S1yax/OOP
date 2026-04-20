package users;

import java.io.Serializable;

public abstract class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String login;
    private String password;
    private String firstName;
    private String lastName;

    public User(String login, String password, String firstName, String lastName) {
        this.login     = login;
        this.password  = password;
        this.firstName = firstName;
        this.lastName  = lastName;
    }

    public String getLogin()    { return login; }
    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }

    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName; }
    public String getFullName()  { return firstName + " " + lastName; }

    public abstract void showMenu();

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + login + "] " + getFullName();
    }
}