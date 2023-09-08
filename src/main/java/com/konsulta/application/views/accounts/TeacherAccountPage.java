package com.konsulta.application.views.accounts;

import com.konsulta.application.data.entity.Teacher;
import com.konsulta.application.data.service.TeacherService;
import com.konsulta.application.data.service.TimeslotGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Route(value = "teacher-account")
public class TeacherAccountPage extends Div {
    Teacher teacher = (Teacher) VaadinSession.getCurrent().getAttribute("teacher");
    private final TimeslotGenerator timeslotGenerator;
    private final TeacherService teacherService;

    private ComboBox<DayOfWeek> dayComboBox = new ComboBox<>("Select a day:");
    private TimePicker startTimePicker = new TimePicker("Start time:");
    private TimePicker endTimePicker = new TimePicker("End time:");
    private Button addButton = new Button("Add Timeslot");
    H3 header = new H3("Konsulta");
    H2 name = new H2(teacher.getName());
    H2 surname = new H2(" " + teacher.getSurname());

    public TeacherAccountPage( TeacherService teacherService, TimeslotGenerator timeslotGenerator) {
        this.timeslotGenerator = timeslotGenerator;
        this.teacherService = teacherService;

        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
        MenuItem myAccountButton = menuBar.addItem("dashboard");
        MenuItem logOutButton = menuBar.addItem("log out");

        myAccountButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("teacher-dashboard")));

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        headerLayout.add(header, menuBar);

        HorizontalLayout contentLayout = new HorizontalLayout();
        contentLayout.setSizeFull();

        add(headerLayout, contentLayout);

        dayComboBox.setItems(DayOfWeek.values());
        addButton.addClickListener(event -> addTimeslot());
        add(new HorizontalLayout(name, surname));
        add(dayComboBox, startTimePicker, endTimePicker, addButton);
    }

    private void addTimeslot() {
        DayOfWeek selectedDay = dayComboBox.getValue();
        LocalTime startTime = startTimePicker.getValue();
        LocalTime endTime = endTimePicker.getValue();

        if (selectedDay != null && startTime != null && endTime != null) {
            timeslotGenerator.generateTimeslots(teacher.getId(), selectedDay, startTime, endTime);
        } else {
            Notification.show("Please fill in all the fields.");
        }
    }

}

