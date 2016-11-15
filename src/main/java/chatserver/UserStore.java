package chatserver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import util.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserStore {
    private Log log = LogFactory.getLog(UserStore.class);

    private final Config config;
    private Map<String, User> users = new ConcurrentHashMap<>();

    public UserStore(){
        this.config = new Config("user");
    }

    public void load() {
        for (String key : config.listKeys()) {
            String user = userFromKey(key);
            String property = propertyFromKey(key);

            if ("password".equals(property)) {
                String password = config.getString(key);

                if (!users.containsKey(user)) {
                    users.put(user, new User(user, password));
                }
            }
        }
    }

    public User getUser(String user){
        return users.containsKey(user) ? users.get(user) : null;
    }

    public User[] getUsers() {

        User[] userList = new User[users.size()];

        if (userList.length > 0) {
            userList = users.values().toArray(userList);
        }

        return userList;
    }

    public User[] getUsersSorted() {
        User[] users = getUsers();
        Arrays.sort(users);
        return users;
    }

    private String propertyFromKey(String key) {
        int ind = key.lastIndexOf('.');
        return key.substring(ind+1);
    }

    private String userFromKey(String key) {
        int ind = key.lastIndexOf('.');
        return key.substring(0,ind);
    }

    public User[] getOnlineUsers() {
        ArrayList<User> onlineUsers = new ArrayList<>();

        for (User user : getUsers()) {
            if (user.isOnline()) {
                onlineUsers.add(user);
            }
        }

        return onlineUsers.toArray(new User[onlineUsers.size()]);
    }
}
