package com.konsulta.application.views.registration;

import com.konsulta.application.data.entity.Parent;
import com.konsulta.application.data.entity.Student;
import com.konsulta.application.data.service.StudentService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.konsulta.application.data.service.ParentService;

@Transactional
@Route("sign-up")
public class SignUpPage extends VerticalLayout {

    @Autowired
    private EntityManager entityManager;

    private final Binder<Parent> parentBinder = new Binder<>(Parent.class);
    private final Binder<Student> studentBinder = new Binder<>(Student.class);

    @Autowired
    private ParentService parentService;

    @Autowired
    private StudentService studentService;


    TextField nameField = new TextField("Name");
    TextField surnameField = new TextField("Surname");
    TextField phoneNumberField = new TextField("Phone Number");
    PasswordField passwordField = new PasswordField("Password");
    PasswordField passwordField2 = new PasswordField("Repeat password");
    TextField emailField = new TextField("Email");
    TextField studentNameField = new TextField("Student name");
    TextField studentSurnameField = new TextField("Student Surname");
    ComboBox<String> classComboBox = new ComboBox<>("Student class");

    public SignUpPage() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H1 title = new H1("SIGN UP");
        Button signUpButton = new Button("Sign Up");

        // Create combobox options
        classComboBox.setItems("5A", "5B", "5C", "5D");

        add(title, new HorizontalLayout(nameField, surnameField),
                new HorizontalLayout(phoneNumberField, emailField),
                new HorizontalLayout(passwordField, passwordField2),
                new HorizontalLayout(studentNameField, studentSurnameField),
                classComboBox,
                signUpButton);

        signUpButton.addClickListener(event -> signUp());
    }

    @PostConstruct
    private void init() {
        parentBinder.forField(nameField)
                .asRequired("Name cannot be empty")
                .bind(Parent::getName, Parent::setName);

        parentBinder.forField(surnameField)
                .asRequired("Surname cannot be empty")
                .bind(Parent::getSurname, Parent::setSurname);

        parentBinder.forField(phoneNumberField)
                .asRequired("Phone Number cannot be empty")
                .bind(Parent::getPhoneNumber, Parent::setPhoneNumber);

        parentBinder.forField(emailField)
                .asRequired("Email cannot be empty")
                .withValidator(new EmailValidator("Invalid email address"))
                .bind(Parent::getEmail, Parent::setEmail);

        parentBinder.forField(passwordField)
                .asRequired("Password cannot be empty")
                .withValidator(new StringLengthValidator(
                        "Password must be at least 6 characters",
                        6, null))
                .bind(Parent::getPassword, Parent::setPassword);

        parentBinder.forField(passwordField2)
                .asRequired("Please repeat the password")
                .withValidator(value -> value.equals(passwordField.getValue()), "Passwords do not match")
                .bind(parent -> "", (parent, value) -> {});

        studentBinder.forField(studentNameField)
                .asRequired("Student name cannot be empty")
                .bind(Student::getStudentName, Student::setStudentName);

        studentBinder.forField(studentSurnameField)
                .asRequired("Student surname cannot be empty")
                .bind(Student::getStudentSurname, Student::setStudentSurname);

        studentBinder.forField(classComboBox)
                .asRequired("Please select a class")
                .bind(Student::getStudentClass, Student::setStudentClass);
    }

    private void signUp() {
        if (parentBinder.validate().isOk() && studentBinder.validate().isOk()) {
            Parent parent = new Parent();
            parentBinder.writeBeanIfValid(parent);

            Student student = new Student();
            studentBinder.writeBeanIfValid(student);

            try {
                // Save the Parent and Student using the services
                parent = parentService.saveParent(parent);
                student = studentService.saveStudent(student);

                // Display a success message
                Notification.show("Sign up successful!");
            } catch (Exception e) {
                // Handle database error
                Notification.show("Error signing up. Please try again.", 3000, Notification.Position.TOP_CENTER);
            }
        } else {
            // Display validation errors
            Notification.show("Please fix the validation errors", 3000, Notification.Position.TOP_CENTER);
        }
    }

}

