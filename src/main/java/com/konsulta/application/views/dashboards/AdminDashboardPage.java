package com.konsulta.application.views.dashboards;

import com.konsulta.application.views.masterdetail.ParentMasterDetailView;
import com.konsulta.application.views.masterdetail.TeacherMasterDetailView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "admin-dashboard")
@PageTitle("Admin Dashboard | Konsulta")
public class AdminDashboardPage extends VerticalLayout {
    Button parentViewButton = new Button("CRUD parent table");
    Button teacherViewButton = new Button("CRUD teacher table");


    public AdminDashboardPage() {
        add(new H2("Admin Dashboard"));

        parentViewButton.addClickListener(event -> {
            UI.getCurrent().navigate(ParentMasterDetailView.class);
        });

        teacherViewButton.addClickListener(event -> {
            UI.getCurrent().navigate(TeacherMasterDetailView.class);
        });

        add(parentViewButton, teacherViewButton);
    }
}

