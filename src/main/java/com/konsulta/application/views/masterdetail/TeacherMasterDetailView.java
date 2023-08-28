package com.konsulta.application.views.masterdetail;

import com.konsulta.application.data.entity.Teacher;
import com.konsulta.application.data.service.TeacherService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;

@PageTitle("Teacher Master-Detail")
@Route(value = "teacher-master-detail/:teacherID?/:action?(edit)")
@RouteAlias(value = "teacher-crud")
@Uses(Icon.class)
public class TeacherMasterDetailView extends Div implements BeforeEnterObserver {

    private final String TEACHER_ID = "teacherID";
    private final String TEACHER_EDIT_ROUTE_TEMPLATE = "teacher-master-detail/%s/edit";

    private final Grid<Teacher> grid = new Grid<>(Teacher.class, false);

    private TextField email;
    private TextField password;
    private TextField name;
    private TextField surname;
    private TextField subject;
    private TextField classroom;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Teacher> binder;

    private Teacher teacher;

    private final TeacherService teacherService;

    public TeacherMasterDetailView(TeacherService teacherService) {
        this.teacherService = teacherService;
        addClassNames("teacher-master-detail-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        splitLayout.setPrimaryStyle("width", "70%");
        splitLayout.setSecondaryStyle("width", "30%");

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("password").setAutoWidth(true);
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("surname").setAutoWidth(true);
        grid.addColumn("subject").setAutoWidth(true);
        grid.addColumn("classroom").setAutoWidth(true);


        grid.setItems(query -> teacherService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(TEACHER_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(TeacherMasterDetailView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Teacher.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.teacher == null) {
                    this.teacher = new Teacher();
                }
                binder.writeBean(this.teacher);
                teacherService.update(this.teacher);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(TeacherMasterDetailView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> teacherId = event.getRouteParameters().get(TEACHER_ID).map(Long::parseLong);
        if (teacherId.isPresent()) {
            Optional<Teacher> teacherFromBackend = teacherService.get(teacherId.get());
            if (teacherFromBackend.isPresent()) {
                populateForm(teacherFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested teacher was not found, ID = %s", teacherId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // refresh grid
                refreshGrid();
                event.forwardTo(TeacherMasterDetailView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        email = new TextField("Email");
        password = new TextField("Password");
        name = new TextField("Name");
        surname = new TextField("Surname");
        subject = new TextField("Subject");
        classroom = new TextField("Classroom");
        formLayout.add(email, password, name, surname, subject, classroom);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }


    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Teacher value) {
        this.teacher = value;
        binder.readBean(this.teacher);
    }
}

