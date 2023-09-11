package com.konsulta.application.views.dashboards;

import com.konsulta.application.data.entity.*;
import com.konsulta.application.data.repository.ConsultationRepository;
import com.konsulta.application.data.service.ConsultationService;
import com.konsulta.application.data.service.EmailSender;
import com.konsulta.application.data.service.ParentService;
import com.konsulta.application.data.service.TeacherService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import javax.mail.MessagingException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Route("parent-dashboard")
public class ParentDashboardPage extends VerticalLayout implements BeforeEnterObserver {
    private final TeacherService teacherService;
    private final ParentService parentService;
    private final ConsultationRepository consultationRepository;
    private final ConsultationService consultationService;
    Parent parent = (Parent) VaadinSession.getCurrent().getAttribute("parent");
    private H1 greeting = new H1();


    ComboBox<Teacher> teacherComboBox = new ComboBox<>("Select a Teacher");
    ComboBox<Timeslot> timeslotComboBox = new ComboBox<>("Select a Timeslot");
    Button registerButton = new Button("Register");

    H3 header = new H3("Konsulta | Dashboard");
    VerticalLayout upcomingColumn = new VerticalLayout();

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (parent != null) {
            String parentName = parent.getName();
            greeting.setText("Hello, " + parentName + "!");

            boolean hasConsultations = consultationService.parentHasConsultations(parent);

            if (!hasConsultations) {
                H3 upcomingConsultationsHeading = new H3("No consultations yet... register for one!");
                upcomingColumn.add(upcomingConsultationsHeading);
            } else {
                // Display message when there are consultations
                H3 consultationsMessage = new H3("Upcoming consultations");
                upcomingColumn.add(consultationsMessage);
                populateConsultations();
            }
        } else {
            greeting.setText("Error: Parent object not found");
        }
    }


    public ParentDashboardPage(TeacherService teacherService, ParentService parentService, ConsultationRepository consultationRepository, ConsultationService consultationService) {
        this.teacherService = teacherService;
        this.parentService = parentService;
        this.consultationRepository = consultationRepository;
        this.consultationService = consultationService;

        //menu creation in the header
        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
        MenuItem myAccountButton = menuBar.addItem("my account");
        MenuItem logOutButton = menuBar.addItem("log out");

        myAccountButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("parent-account")));

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setJustifyContentMode(JustifyContentMode.START);
        headerLayout.add(header, menuBar);

        HorizontalLayout contentLayout = new HorizontalLayout();
        contentLayout.setSizeFull();

        VerticalLayout formColumn = new VerticalLayout();
        formColumn.add(greeting, teacherComboBox, timeslotComboBox, registerButton);

        upcomingColumn.setSizeFull();
        upcomingColumn.setAlignItems(FlexComponent.Alignment.END);

        contentLayout.add(formColumn, upcomingColumn);

        // Populate the teacherComboBox with available teachers
        List<Teacher> availableTeachers = teacherService.getAllTeachers();
        teacherComboBox.setItems(availableTeachers);
        teacherComboBox.setItemLabelGenerator(this::generateTeacherLabel);

        // Populate the timeslotComboBox with available timeslots
        teacherComboBox.addValueChangeListener(e -> {
            Teacher selectedTeacher = teacherComboBox.getValue();
            System.out.println(selectedTeacher);
            if (selectedTeacher != null) {
                List<Timeslot> availableTimeslots = teacherService.getAvailableTimeslots(selectedTeacher);
                System.out.println(availableTimeslots);
                availableTimeslots.sort(Comparator.comparing(Timeslot::getStart));

                timeslotComboBox.setItems(availableTimeslots);
                timeslotComboBox.setItemLabelGenerator(timeslot -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    return timeslot.getStart().format(formatter) + " - " + timeslot.getEnd().format(formatter);
                });
            } else {
                timeslotComboBox.setItems();
            }
        });

        registerButton.addClickListener(e -> registerTimeslot());

        add(headerLayout, contentLayout);
    }

    private String generateTeacherLabel(Teacher teacher) {
        return teacher.getName() + " " + teacher.getSurname() + " | " + teacher.getSubject();
    }

    private void registerTimeslot() {
        Teacher selectedTeacher = teacherComboBox.getValue();
        Timeslot selectedTimeslot = timeslotComboBox.getValue();

        if (selectedTeacher != null && selectedTimeslot != null) {
            //implement the registration logic
            Consultation consultation = new Consultation();
            consultation.setParent(parent);
            consultation.setTeacher(selectedTeacher);
            consultation.setTimeslot(selectedTimeslot);
            consultation.setStatus(ConsultationStatus.SCHEDULED);

            // Save the Consultation to the database
            consultation = consultationRepository.save(consultation);

            // Remove the selected timeslot from the teacher's available timeslots
            teacherService.removeScheduledTimeslot(selectedTeacher, selectedTimeslot);

            Notification.show("Timeslot registered!");
            teacherComboBox.setValue(null);
            timeslotComboBox.setValue(null);

            //format the timeslot to String format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String scheduledTime = consultation.getTimeslot().getStart().format(formatter) +
                    " - " +
                    consultation.getTimeslot().getEnd().format(formatter);

            //send emails to teacher and parent
            parentService.sendConfirmationEmailToParent(parent, selectedTeacher, scheduledTime);
            teacherService.sendNotificationEmailToTeacher(selectedTeacher, scheduledTime);

        } else {
            Notification.show("Please select a teacher and a timeslot.");
        }

    }

    private void populateConsultations() {
        //fetch the consultations from the database
        List<Consultation> parentConsultations = consultationService.getConsultationsByParent(parent);

        for (Consultation consultation : parentConsultations) {
            //create a container for each consultation
            Div consultationContainer = new Div();
            consultationContainer.addClassName("consultation-container");

            H3 teacherLabel = new H3("Teacher: " + consultation.getTeacher().getName());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String scheduledTime = consultation.getTimeslot().getStart().format(formatter) +
                    " - " +
                    consultation.getTimeslot().getEnd().format(formatter);
            H3 timeLabel = new H3("Scheduled Time: " + scheduledTime);

            // Add "cancel" and "join" buttons (not functional yet)
            Button cancelButton = new Button("Cancel");
            Button joinButton = new Button("Join");

            consultationContainer.add(teacherLabel, timeLabel, cancelButton, joinButton);
            upcomingColumn.add(consultationContainer);
        }
    }
}


