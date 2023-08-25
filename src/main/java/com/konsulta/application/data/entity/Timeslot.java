package com.konsulta.application.data.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Embeddable
public enum Timeslot {
    ;
    private LocalDateTime start;
    private LocalDateTime end;

    Timeslot() {
    }

    Timeslot(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return start.toString() + " - " + end.toString();
    }
}
