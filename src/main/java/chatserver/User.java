package chatserver;

import java.net.Socket;

public class User implements Comparable<User> {
    private final String password;
    private final String name;

    private boolean isOnline;
    private String privateAddress;
    private Socket tcpConnection;

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

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
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

    public void setTcpConnection(Socket tcpConnection) {
        this.tcpConnection = tcpConnection;
    }

    public Socket getTcpConnection() {
        return tcpConnection;
    }

    @Override
    public int compareTo(User o) {
        if (o == null) {
            return 1;
        }
        return name.compareTo(o.name);
    }
}
