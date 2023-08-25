package com.konsulta.application.views.registration;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.stereotype.Component;

@PageTitle("landing-page")
@Route(value = "")
@Component
public class LandingPage extends VerticalLayout {


    public LandingPage(){
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setHeightFull();

        Image logoImage = new Image("https://cloudt.edupage.org/cloud?z%3AO5lQNHtJfaRJOG7gx0ntLEeClTZ36KdNTYUWEnb2Iy0DXx26glXJlOqXoJD3y%2BKh89mStw2P340ITzdjvAnN%2Bd0rCrnCM%2Fju5tafxcIMpPg%3D", "Logo");
        logoImage.setHeight("250px");

        H1 title = new H1("Welcome to Konsulta");

        Button loginButton = new Button("Login");
        Button signUpButton = new Button("Sign up");

        loginButton.addClickListener(event -> {
            UI.getCurrent().navigate(LoginPage.class);
        });

        signUpButton.addClickListener(event -> {
            UI.getCurrent().navigate(SignUpPage.class);
        });


        add(logoImage, title, (new HorizontalLayout(loginButton, signUpButton)));

    }
}
