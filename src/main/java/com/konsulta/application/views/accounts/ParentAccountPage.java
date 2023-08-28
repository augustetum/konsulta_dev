package com.konsulta.application.views.accounts;

import com.konsulta.application.data.entity.Parent;
import com.konsulta.application.data.entity.Student;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("parent-account")
public class ParentAccountPage extends VerticalLayout {
    private Parent parent = VaadinSession.getCurrent().getAttribute(Parent.class);

    public ParentAccountPage() {
        H3 header = new H3("Konsulta | My account");

        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
        MenuItem myAccountButton = menuBar.addItem("dashboard");
        MenuItem logOutButton = menuBar.addItem("log out");

        myAccountButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("parent-dashboard")));

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setJustifyContentMode(JustifyContentMode.START);
        headerLayout.add(header, menuBar);

        add(headerLayout);

        // Add Child
        TextField childNameField = new TextField("Child's Name");
        ComboBox<String> classComboBox = new ComboBox<>("Student class");
        Button addChildButton = new Button("Add Child", event -> {
            String childName = childNameField.getValue();
            String childClass = classComboBox.getValue();
            if (!childName.isEmpty() && !classComboBox.isEmpty()) {
                //Student child = new Student(childName, childClass);
                //parent.getKids().add(child);
                Notification.show("Child added: " + childName);
            }
        });

        // Change Password
        PasswordField newPasswordField = new PasswordField("New Password");
        Button changePasswordButton = new Button("Change Password", event -> {
            String newPassword = newPasswordField.getValue();
            if (!newPassword.isEmpty()) {
                parent.setPassword(newPassword);
                Notification.show("Password changed successfully.");
            }
        });


        // Change Phone Number
        TextField newPhoneNumberField = new TextField("New Phone Number");
        Button changePhoneNumberButton = new Button("Change Phone Number", event -> {
            String newPhoneNumber = newPhoneNumberField.getValue();
            if (!newPhoneNumber.isEmpty()) {
                parent.setPhoneNumber(newPhoneNumber);
                Notification.show("Phone number changed to: " + newPhoneNumber);
            }
        });

        Button deleteAccountButton = new Button("Delete Account");
        deleteAccountButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteAccountButton.addClickListener(e -> {
            Dialog confirmationDialog = new Dialog();
            confirmationDialog.setCloseOnOutsideClick(false);
            H3 confirmationLabel = new H3("Are you sure you want to delete this account?");

            Button confirmButton = new Button("Yes", confirmEvent -> {
                //parent.getParents().remove(parent);
                VaadinSession.getCurrent().setAttribute(Parent.class, null);
                confirmationDialog.close();
                Notification.show("Account deleted.");
                VaadinSession.getCurrent().close(); //clear the session

            });

            Button cancelButton = new Button("Cancel", cancelEvent -> confirmationDialog.close());

            confirmationDialog.add(confirmationLabel, new HorizontalLayout(confirmButton, cancelButton));
            confirmationDialog.open();

        });

        // Adding components to form layout
        add(new HorizontalLayout(childNameField, classComboBox), addChildButton, newPasswordField, changePasswordButton,
                deleteAccountButton, newPhoneNumberField, changePhoneNumberButton);
    }

}
