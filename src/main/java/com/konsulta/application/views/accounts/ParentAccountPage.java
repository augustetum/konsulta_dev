package com.konsulta.application.views.accounts;
import com.konsulta.application.data.entity.Parent;
import com.konsulta.application.data.entity.Student;
import com.konsulta.application.data.service.ParentService;
import com.konsulta.application.data.service.StudentService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import java.util.HashSet;
import java.util.Optional;

@Route("parent-account")
public class ParentAccountPage extends VerticalLayout {
    Parent parent = (Parent) VaadinSession.getCurrent().getAttribute("parent");
    private final Binder<Student> studentBinder = new Binder<>(Student.class);
    private final StudentService studentService;
    private final ParentService parentService;

    public ParentAccountPage(StudentService studentService, ParentService parentService) {
        this.studentService = studentService;
        this.parentService = parentService;

        parent = parentService.initializeParent(parent);

        H3 header = new H3("Konsulta | My account");

        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
        MenuItem myAccountButton = menuBar.addItem("dashboard");
        MenuItem logOutButton = menuBar.addItem("log out");

        logOutButton.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate(""));
        });

        myAccountButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("parent-dashboard")));

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setJustifyContentMode(JustifyContentMode.START);
        headerLayout.add(header, menuBar);

        add(headerLayout);

        //ADD CHILD FUNCTIONALITY - if a parent has one than more child in the school, they can be added here
        TextField studentNameField = new TextField("Child's Name");
        TextField studentSurnameField = new TextField("Child's Surname");
        ComboBox<String> classComboBox = new ComboBox<>("Student class");
        classComboBox.setItems("5A", "5B", "5C", "5D");
        studentBinder.forField(studentNameField)
                .asRequired("Student name cannot be empty")
                .bind(Student::getStudentName, Student::setStudentName);

        studentBinder.forField(studentSurnameField)
                .asRequired("Student surname cannot be empty")
                .bind(Student::getStudentSurname, Student::setStudentSurname);

        studentBinder.forField(classComboBox)
                .asRequired("Please select a class")
                .bind(Student::getStudentClass, Student::setStudentClass);
        Button addChildButton = new Button("Add Child", event -> {

            if (studentBinder.validate().isOk()) {
                Student student = new Student();
                studentBinder.writeBeanIfValid(student);
                parent.getChildren().size();

                try {

                    student = studentService.saveStudent(student);

                    if (parent.getChildren() == null) {
                        parent.setChildren(new HashSet<>());
                    }

                    parent.getChildren().add(student);

                    parent = parentService.saveParent(parent);

                    Notification.show("A child added successfully.");
                } catch (Exception e) {
                    e.printStackTrace();
                    Notification.show("Error adding the student. Please try again.", 3000, Notification.Position.TOP_CENTER);
                }
            } else {
                Notification.show("Please fix the validation errors", 3000, Notification.Position.TOP_CENTER);
            }
        });

        //CHANGE PASSWORD FUNCTIONALITY
        PasswordField newPasswordField = new PasswordField("New Password");
        Button changePasswordButton = new Button("Change Password", event -> {
            String newPassword = newPasswordField.getValue();
            if (!newPassword.isEmpty()) {
                parent.setPassword(newPassword);
                Notification.show("Password changed successfully.");
            }
        });


        //CHANGE PHONE NUMBER FUNCTIONALITY
        TextField newPhoneNumberField = new TextField("New Phone Number");
        Button changePhoneNumberButton = new Button("Change Phone Number", event -> {
            String newPhoneNumber = newPhoneNumberField.getValue();
            if (!newPhoneNumber.isEmpty()) {
                parent.setPhoneNumber(newPhoneNumber);
                Notification.show("Phone number changed to: " + newPhoneNumber);
            }
        });
        //DELETE ACCOUNT FUNCTIONALITY
        Button deleteAccountButton = new Button("Delete Account");
        deleteAccountButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteAccountButton.addClickListener(e -> {
            Dialog confirmationDialog = new Dialog();
            confirmationDialog.setCloseOnOutsideClick(false);
            H3 confirmationLabel = new H3("Are you sure you want to delete this account?");

            Button confirmButton = new Button("Yes", confirmEvent -> {
           // parentService.deleteParent(parent);

                VaadinSession.getCurrent().setAttribute(Parent.class, null);
                confirmationDialog.close();
                Notification.show("Account deleted.");
                VaadinSession.getCurrent().close(); //clear the session

            });

            Button cancelButton = new Button("Cancel", cancelEvent -> confirmationDialog.close());

            confirmationDialog.add(confirmationLabel, new HorizontalLayout(confirmButton, cancelButton));
            confirmationDialog.open();

        });

        //FORM LAYOUT
        add(new HorizontalLayout(studentNameField, studentSurnameField, classComboBox), addChildButton, newPasswordField, changePasswordButton,
                deleteAccountButton, newPhoneNumberField, changePhoneNumberButton);
    }

}
