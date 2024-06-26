package chatserver;

import chatserver.protocol.ChatProtocol;
import chatserver.protocol.ISendMessage;

public class User implements Comparable<User> {
    private final String password;
    private final String name;

    private boolean isOnline;
    private boolean isRegistered;

    private ChatProtocol protocol;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void logout() {
        isOnline = false;
        isRegistered = false;
        protocol = null;
    }

    public boolean login(String password) {
        if (this.password.equals(password)) {
            isOnline = true;
            return true;
        }

        return false;
    }

    public void setOnline() {
        isOnline = true;
    }

    @Override
    public int compareTo(User o) {
        if (o == null) {
            return 1;
        }
        return name.compareTo(o.name);
    }

    public void setProtocol(ChatProtocol protocol) {
        this.protocol = protocol;
    }

    public ChatProtocol getProtocol() {
        return protocol;
    }

    public void setIsRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    public boolean getIsRegistered() {
        return isRegistered;
    }
}
