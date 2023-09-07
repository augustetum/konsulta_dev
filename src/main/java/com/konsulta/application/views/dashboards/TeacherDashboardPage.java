package com.konsulta.application.views.dashboards;

import com.konsulta.application.data.entity.Teacher;
import com.konsulta.application.views.accounts.TeacherAccountPage;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("teacher-dashboard")
public class TeacherDashboardPage extends Div {
    Teacher teacher = (Teacher) VaadinSession.getCurrent().getAttribute("teacher");

    H2 greeting = new H2("Hi:)" + teacher.getName());
    H3 header = new H3("Konsulta | dashboard");

    public TeacherDashboardPage() {

        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
        MenuItem myAccountButton = menuBar.addItem("my account");
        MenuItem logOutButton = menuBar.addItem("log out");

        myAccountButton.addClickListener(e -> UI.getCurrent().navigate(TeacherAccountPage.class));

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        headerLayout.add(header, menuBar);
        add(headerLayout, greeting);
    }
}
