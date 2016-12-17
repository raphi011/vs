package chatserver;

import nameserver.INameserverForChatserver;
import nameserver.exceptions.AlreadyRegisteredException;
import nameserver.exceptions.InvalidDomainException;

import java.rmi.RemoteException;

public class AddressStore {
    private final INameserverForChatserver nameServer;

    public AddressStore(INameserverForChatserver nameServer) {
        this.nameServer = nameServer;
    }

    public void setPrivateAddress(String name, String address) throws InvalidDomainException, AlreadyRegisteredException {
        try {
            nameServer.registerUser(name,address);
        } catch (RemoteException ex) {

        }
    }

    public String getPrivateAddress(String name) {
        String [] nameParts = name.split(".");
        INameserverForChatserver currentNameServer = nameServer;
        String address = "";

        try {
            for (int i = nameParts.length - 1; i > 0; i--) {
                String subdomain = nameParts[i];
                currentNameServer = currentNameServer.getNameserver(subdomain);

                if (i == 1) {
                    address = currentNameServer.lookup(name);
                }
            }
        } catch (RemoteException ex) {

        }

        return address;
    }
}
