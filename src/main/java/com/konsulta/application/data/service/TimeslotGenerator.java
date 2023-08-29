package com.konsulta.application.data.service;

import com.konsulta.application.data.entity.Timeslot;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TimeslotGenerator { //this class generates available timeslots accordingly to what day and what start/end times the teacher has chosen
    public static List<Timeslot> generateTimeslots(DayOfWeek day, LocalTime startTime, LocalTime endTime) {
        List<Timeslot> availableTimeslots = new ArrayList<>();

        int slotDuration = 15; //minutes
        int breakDuration = 5; //minutes

        LocalDateTime currentDateTime = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth(), startTime.getHour(), startTime.getMinute());
        LocalDateTime endOfDay = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth(), endTime.getHour(), endTime.getMinute());

        while (currentDateTime.isBefore(endOfDay)) {
            LocalDateTime slotStart = currentDateTime;
            LocalDateTime slotEnd = slotStart.plusMinutes(slotDuration);

            if (slotEnd.isAfter(endOfDay)) {
                break;
            }

            availableTimeslots.add(new Timeslot(slotStart, slotEnd));

            currentDateTime = slotEnd.plusMinutes(breakDuration);
        }

        return availableTimeslots;
    }
}