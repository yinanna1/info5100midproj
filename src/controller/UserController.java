package controller;

import model.User;
import model.UserModel;

import java.util.List;

public class UserController {

    private final UserModel model;

    public UserController(UserModel model) {
        this.model = model;
    }

    // ---------------------------------------------------------------
    // CREATE USER  (return new userId or -1 on failure)
    // ---------------------------------------------------------------
    public int createUser(String name, String email, String password, String role) {
        return model.createUser(name, email, password, role);
    }

    // ---------------------------------------------------------------
    // READ USERS
    // ---------------------------------------------------------------
    public List<User> getAllUsers() {
        return model.getAllUsers();
    }

    // ---------------------------------------------------------------
    // UPDATE USER
    // ---------------------------------------------------------------
    public boolean updateUser(int id, String email, String password) {
        return model.updateUser(id, email, password);
    }

    // ---------------------------------------------------------------
    // DELETE USER
    // ---------------------------------------------------------------
    public boolean deleteUser(int id) {
        return model.deleteUser(id);
    }
}
