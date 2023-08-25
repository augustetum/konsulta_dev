package com.konsulta.application.data.entity;


import com.konsulta.application.data.entity.*;
import jakarta.persistence.*;

@Entity
@Table(name = "kon_consultations") // Name of the table in the database
public class Consultation extends AbstractEntity {

    @ManyToOne
    private Parent parent;

    @ManyToOne
    private Teacher teacher;

    @Enumerated(EnumType.STRING) // Enum should be persisted as a string
    private ConsultationStatus status;

    @Embedded // Embed the Timeslot class
    private Timeslot timeslot;

}

