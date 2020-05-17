package api;

import server.User;

@ServiceName("server.UserServiceImpl")
public interface UserServiceApi {

    public User getUser();
}
