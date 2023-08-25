package com.konsulta.application.views.registration;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@Route(value = "login")
@PageTitle("Login page")
public class LoginPage extends VerticalLayout {
    private Button loginButton;

    public LoginPage() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setHeightFull();

        H1 title = new H1("Login");

        PasswordField passwordField = new PasswordField("Password");
        EmailField emailField = new EmailField("Email");
        emailField.setPlaceholder("Enter your email");
        passwordField.setPlaceholder("Enter your password");

        loginButton = new Button("Log in");
        loginButton.addClickListener(event -> {
            String email = emailField.getValue();
            String password = passwordField.getValue();

            //checks what type of acc is logging in
            if (isAdmin(email, password)) {
           //     UI.getCurrent().navigate(AdminDashboardPage.class);
            }

            else if (isTeacher(email, password)) {
               // UI.getCurrent().navigate(TeacherDashboardPage.class);
            }

           // else if (isParent(email, password)) {
               // UI.getCurrent().navigate(ParentDashboardPage.class);
          //  }

            else {
                //displays an error message for invalid credentials
                Notification.show("Invalid email or password", 3000, Notification.Position.TOP_CENTER);
            }
        });

        add(title, emailField, passwordField, loginButton);
    }

    private boolean isAdmin(String email, String password) {
        // Check admin credentials
        // Return true if the admin account exists and the password matches
        // Replace this with your actual admin authentication logic
        return false;
    }

    private boolean isTeacher(String email, String password) {
        // Check teacher credentials
        // Return true if a teacher account exists and the password matches
        // Replace this with your actual teacher authentication logic
        return false;
    }

    //private boolean isParent(String email, String password) {
        // Check parent credentials
        // Return true if a parent account exists and the password matches
        // Replace this with your actual parent authentication logicreturn Parent.getParents().stream()
   // }
}