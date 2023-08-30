package com.konsulta.application.views.registration;

import com.konsulta.application.data.entity.Teacher;
import com.konsulta.application.data.service.AdminService;
import com.konsulta.application.data.service.ParentService;
import com.konsulta.application.data.service.TeacherService;
import com.konsulta.application.views.dashboards.ParentDashboardPage;
import com.konsulta.application.views.dashboards.TeacherDashboardPage;
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
    private final AdminService adminService;
    private final TeacherService teacherService;
    private final ParentService parentService;

    public LoginPage(AdminService adminService, TeacherService teacherService, ParentService parentService) {
        this.adminService = adminService;
        this.teacherService = teacherService;
        this.parentService = parentService;

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
            String userType = null; //is used to create different mainlayouts to show different navigation bars based on the role (RBAC)

            //check what type of acc it is (admin, teacher or parent)
            if (adminService.isValidAdminLogin(email, password)) {
                //UI.getCurrent().navigate(AdminDashboardPage.class);
                userType = "admin";
                Notification.show("You are an admin!", 3000, Notification.Position.TOP_CENTER);
            }

            else if (teacherService.isValidTeacherLogin(email, password)) {
                Teacher teacher = teacherService.findByEmail(email); // Fetch the Teacher entity
                UI.getCurrent().getSession().setAttribute("teacher", teacher);
                UI.getCurrent().navigate(TeacherDashboardPage.class);
                userType = "teacher";
                Notification.show("You are a teacher!", 3000, Notification.Position.TOP_CENTER);
            }

            else if (parentService.isValidParentLogin(email, password)) {
                UI.getCurrent().navigate(ParentDashboardPage.class);
                userType = "parent";
                Notification.show("You are a parent!", 3000, Notification.Position.TOP_CENTER);
            }
            else {
                //in case the credentials are invalid
                Notification.show("Invalid email or password", 3000, Notification.Position.TOP_CENTER);
            }

            if (userType != null) {
                UI.getCurrent().getSession().setAttribute("userType", userType); // Store userType in session
            }
        });
        add(title, emailField, passwordField, loginButton);
    }
}