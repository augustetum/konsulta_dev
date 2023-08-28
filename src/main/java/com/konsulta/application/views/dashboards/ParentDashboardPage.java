package com.konsulta.application.views.dashboards;

import com.konsulta.application.data.entity.Parent;
import com.konsulta.application.data.entity.Teacher;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
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

import java.time.LocalDateTime;

@Route("parent-dashboard")
public class ParentDashboardPage extends VerticalLayout implements BeforeEnterObserver {
    Parent parent = VaadinSession.getCurrent().getAttribute(Parent.class);
    private H1 greeting = new H1();


    ComboBox<Teacher> teacherComboBox = new ComboBox<>("Select a Teacher");
    ComboBox<LocalDateTime> timeslotComboBox = new ComboBox<>("Select a Timeslot");
    Button registerButton = new Button("Register");

    H3 header = new H3("Konsulta | Dashboard");
    VerticalLayout upcomingColumn = new VerticalLayout();

    public ParentDashboardPage() {
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

        add(headerLayout, contentLayout);

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // if (parent != null) {
        //   String parentName = parent.getName();
        //  greeting.setText("Hello, " + parentName + "!");
        // } else {
        //    // greeting.setText("Error: Parent object not found");
        // }

        //if (parent.getConsultations().isEmpty()) {
        //displays message when there are no consultations yet
        // H3 upcomingConsultationsHeading = new H3("No consultations yet... register for one!");
        // upcomingColumn.add(upcomingConsultationsHeading);

        // } else {
        //displays message when there are consultations and show them
        //  Div noConsultationsMessage = new Div();
        //  noConsultationsMessage.setText("Upcoming consultations");
        //  upcomingColumn.add(noConsultationsMessage);
        // }
        // }
    }
}

