package com.konsulta.application.views.dashboards;

import com.konsulta.application.data.entity.Consultation;
import com.konsulta.application.data.entity.Teacher;
import com.konsulta.application.data.service.ConsultationService;
import com.konsulta.application.views.accounts.TeacherAccountPage;
import com.konsulta.application.views.registration.LoginPage;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route("teacher-dashboard")
public class TeacherDashboardPage extends Div implements BeforeEnterObserver {
    Teacher teacher = (Teacher) VaadinSession.getCurrent().getAttribute("teacher");
    private final ConsultationService consultationService;

    H2 greeting = new H2("Hi:)" + teacher.getName());
    H3 header = new H3("Konsulta | dashboard");
    VerticalLayout upcomingColumn = new VerticalLayout();

    public TeacherDashboardPage(ConsultationService consultationService) {
        this.consultationService = consultationService;

        MenuBar menuBar = new MenuBar();

        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
        MenuItem myAccountButton = menuBar.addItem("my account");
        MenuItem logOutButton = menuBar.addItem("log out");

        logOutButton.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate(""));
        });

        myAccountButton.addClickListener(e -> UI.getCurrent().navigate(TeacherAccountPage.class));

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        headerLayout.add(header, menuBar);
        add(headerLayout, greeting);
        add(upcomingColumn);
    }


    public void beforeEnter(BeforeEnterEvent event) {
        if (teacher != null) {
            String parentName = teacher.getName();
            greeting.setText("Hello, " + parentName + "!");

            boolean hasConsultations = consultationService.teacherHasConsultations(teacher);

            if (!hasConsultations) {
                H3 upcomingConsultationsHeading = new H3("No consultations yet... wait until someone registers!");
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

    private void populateConsultations() {
        // Fetch the parent's consultations
        List<Consultation> teacherConsultations = consultationService.getConsultationsByTeacher(teacher);

        // Iterate through the consultations and create display components
        for (Consultation consultation : teacherConsultations) {
            // Create a container for the consultation details
            Div consultationContainer = new Div();
            consultationContainer.addClassName("consultation-container"); // You can define CSS styles for this class

            // Display teacher name
            H3 teacherLabel = new H3("Parent: " + consultation.getParent().getName() + consultation.getParent().getSurname());

            // Display scheduled time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String scheduledTime = consultation.getTimeslot().getStart().format(formatter) +
                    " - " +
                    consultation.getTimeslot().getEnd().format(formatter);
            H3 timeLabel = new H3("Scheduled Time: " + scheduledTime);

            // Add "cancel" and "join" buttons (not functional yet)
            Button cancelBtn = new Button("Cancel");
            Button joinBtn = new Button("Join");

            consultationContainer.add(teacherLabel, timeLabel, cancelBtn, joinBtn);
            upcomingColumn.add(consultationContainer);
        }
    }

}
