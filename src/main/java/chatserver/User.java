package chatserver;

import chatserver.protocol.ISendMessage;

public class User implements Comparable<User> {
    private final String password;
    private final String name;

    private boolean isOnline;
    private String privateAddress;
    private ISendMessage protocol;

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
        protocol = null;
        privateAddress = null;
    }

    public boolean login(String password) {
        if (this.password.equals(password)) {
            isOnline = true;
            return true;
        }

        return false;
    }

    public void setPrivateAddress(String address) {
        this.privateAddress = address;
    }

    public String getPrivateAddress() {
        return this.privateAddress;
    }

    public boolean getIsRegistered() {
        return !(privateAddress == null || privateAddress.isEmpty());
    }

    @Override
    public int compareTo(User o) {
        if (o == null) {
            return 1;
        }
        return name.compareTo(o.name);
    }

    public void setProtocol(ISendMessage protocol) {
        this.protocol = protocol;
    }

    public ISendMessage getProtocol() {
        return protocol;
    }

}
