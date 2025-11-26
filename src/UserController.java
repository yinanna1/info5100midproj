import model.User;

import java.awt.event.*;
import java.util.List;

public class UserController {

    private final UserModel model;
    private final UserView view;

    public UserController(UserModel model, UserView view) {
        this.model = model;
        this.view = view;

        view.createBtn.addActionListener(new CreateListener());
        view.readBtn.addActionListener(new ReadListener());
        view.updateBtn.addActionListener(new UpdateListener());
        view.deleteBtn.addActionListener(new DeleteListener());
    }

    class CreateListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String name = view.nameField.getText();
            String email = view.emailField.getText();
            String password = view.passwordField.getText();
            String role = (String) view.roleDropdown.getSelectedItem();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                view.showMessage("⚠ All fields required.");
                return;
            }

            // NEW: Prevent duplicate names
            if (model.userNameExists(name)) {
                view.showMessage("❌ Username already exists. Choose another name.");
                return;
            }

            // INSERT USER → returns an integer, NOT a model.User object
            int userId = model.insertUser(new User(name, email, password, role));

            if (userId <= 0) {
                view.showMessage("❌ Failed to create user.");
                return;
            }

            // INSERT into Student / Instructor / Admin
            String roleId = model.insertRoleTable(userId, role);

            view.showMessage(
                    "✅ model.User created!\n" +
                            "model.User ID: " + userId + "\n" +
                            role.toUpperCase() + " ID: " + roleId
            );
        }
    }


    // READ
    class ReadListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            List<User> users = model.getAllUsers();
            view.readArea.setText("");

            for (User u : users) view.readArea.append(u.toString() + "\n");
        }
    }

    // UPDATE
    class UpdateListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                int id = Integer.parseInt(view.updateIdField.getText());
                boolean ok =
                        model.updateUser(id, view.updateEmailField.getText(), view.updatePasswordField.getText());

                view.showMessage(ok ? "✏ Updated" : "❌ model.User not found");

            } catch (Exception ex) {
                view.showMessage("⚠ Invalid ID");
            }
        }
    }

    // DELETE
    class DeleteListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                int id = Integer.parseInt(view.deleteIdField.getText());
                boolean ok = model.deleteUser(id);

                view.showMessage(ok ? "🗑 Deleted" : "❌ model.User not found");

            } catch (Exception ex) {
                view.showMessage("⚠ Invalid ID");
            }
        }
    }
}
