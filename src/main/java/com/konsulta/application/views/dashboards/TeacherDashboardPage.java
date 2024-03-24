package com.konsulta.application.views.dashboards;

import com.konsulta.application.data.entity.Consultation;
import com.konsulta.application.data.entity.Teacher;
import com.konsulta.application.data.service.ConsultationService;
import com.konsulta.application.data.service.ParentService;
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
    private final ParentService parentService;

    H2 greeting = new H2("Hi:)" + teacher.getName());
    H3 header = new H3("Konsulta | dashboard");
    VerticalLayout upcomingColumn = new VerticalLayout();

    public TeacherDashboardPage(ConsultationService consultationService, ParentService parentService) {
        this.consultationService = consultationService;
        this.parentService = parentService;

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
        List<Consultation> teacherConsultations = consultationService.getConsultationsByTeacher(teacher); //fetches the parents' consultations

        for (Consultation consultation : teacherConsultations) {
            Div consultationContainer = new Div();
            consultationContainer.addClassName("consultation-container");

            H3 teacherLabel = new H3("Parent: " + consultation.getParent().getName() + consultation.getParent().getSurname());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String scheduledTime = consultation.getTimeslot().getStart().format(formatter) +
                    " - " +
                    consultation.getTimeslot().getEnd().format(formatter);
            H3 timeLabel = new H3("Scheduled Time: " + scheduledTime);

            //cancellation is here
            Button cancelButton = new Button("Cancel");
            cancelButton.addClickListener(e -> {
                consultationService.cancelConsultationByTeacher(consultation.getId());
                parentService.sendCancellationEmailToParent(consultation.getParent(), consultation.getTeacher(), scheduledTime);
                populateConsultations();
            });

            consultationContainer.add(teacherLabel, timeLabel, cancelButton);
            upcomingColumn.add(consultationContainer);
        }
    }

}
