package server;

import api.UserServiceApi;

public class UserServiceImpl implements UserServiceApi {

    @Override
    public User getUser() {
        User user = new User(1, "李磊");
        return user;
    }
}
