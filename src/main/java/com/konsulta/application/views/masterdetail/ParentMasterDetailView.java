package com.konsulta.application.views.masterdetail;

import com.konsulta.application.data.entity.Parent;
import com.konsulta.application.data.service.ParentService;
import com.konsulta.application.views.registration.LoginPage;
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

@PageTitle("Parent CRUD view")
@Route(value = "parent-master-detail/:parentID?/:action?(edit)")
@RouteAlias(value = "parent-crud")
@Uses(Icon.class)
public class ParentMasterDetailView extends Div implements BeforeEnterObserver {

    private final String PARENT_ID = "parentID";
    private final String PARENT_EDIT_ROUTE_TEMPLATE = "parent-master-detail/%s/edit";

    private final Grid<Parent> grid = new Grid<>(Parent.class, false);

    private TextField email;
    private TextField password;
    private TextField name;
    private TextField phoneNumber;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Parent> binder;

    private Parent parent;

    private final ParentService parentService;

    public ParentMasterDetailView(ParentService parentService) {
        this.parentService = parentService;
        addClassNames("parent-master-detail-view");

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
        grid.addColumn("phoneNumber").setAutoWidth(true);

        grid.setItems(query -> parentService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(PARENT_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(ParentMasterDetailView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Parent.class);

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.parent == null) {
                    this.parent = new Parent();
                }
                binder.writeBean(this.parent);
                parentService.update(this.parent);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(ParentMasterDetailView.class);
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

        //Role-based access control (RBAC)
        String userType = (String) UI.getCurrent().getSession().getAttribute("userType"); //retrieve userType from login session

        if (!"admin".equals(userType)) {
            Notification.show("Access denied. Only admin can access this page.", 3000, Notification.Position.MIDDLE);
            event.rerouteTo(LoginPage.class); //does not let anyone other than the admin to access this page
        }

        Optional<Long> parentId = event.getRouteParameters().get(PARENT_ID).map(Long::parseLong);
        if (parentId.isPresent()) {
            Optional<Parent> parentFromBackend = parentService.get(parentId.get());
            if (parentFromBackend.isPresent()) {
                populateForm(parentFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested parent was not found, ID = %s", parentId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // refresh grid
                refreshGrid();
                event.forwardTo(ParentMasterDetailView.class);
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
        phoneNumber = new TextField("Phone Number");
        formLayout.add(email, password, name, phoneNumber);

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

    private void populateForm(Parent value) {
        this.parent = value;
        binder.readBean(this.parent);
    }
}

