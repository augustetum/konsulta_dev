package com.konsulta.application.data.service;

import com.konsulta.application.data.entity.Teacher;
import com.konsulta.application.data.entity.Timeslot;
import com.konsulta.application.data.repository.TimeslotRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TimeslotGenerator {

    @Autowired
    private TimeslotRepository timeslotRepository;

    @Autowired
    private TeacherService teacherService;

    @Transactional
    public void generateTimeslots(Long teacherId, DayOfWeek selectedDay, LocalTime startTime, LocalTime endTime) {
        // Find the next occurrence of the selected day of the week
        LocalDate currentDate = LocalDate.now();
        LocalDate nextSelectedDay = currentDate.with(TemporalAdjusters.nextOrSame(selectedDay));

        // Calculate the duration of each consultation
        Duration consultationDuration = Duration.ofMinutes(15);

        // Calculate the duration of each break
        Duration breakDuration = Duration.ofMinutes(5);

        // Create a list to store the generated timeslots
        List<Timeslot> timeslots = new ArrayList<>();

        // Fetch the teacher from the database using the teacherId
        Optional<Teacher> teacherOptional = teacherService.get(teacherId);

        if (teacherOptional.isPresent()) {
            Teacher teacher = teacherOptional.get();

            // Generate 3 timeslots for the upcoming month
            for (int i = 0; i < 3; i++) {
                LocalDateTime slotStart = nextSelectedDay.atTime(startTime);

                for (int j = 0; j < 3; j++) {
                    // Calculate the end time of the consultation
                    LocalDateTime slotEnd = slotStart.plus(consultationDuration);

                    // Create a new Timeslot object
                    Timeslot timeslot = new Timeslot(slotStart, slotEnd);

                    // Associate the timeslot with the teacher
                    timeslot.setTeacher(teacher);

                    // Save the timeslot to the database
                    timeslotRepository.save(timeslot);

                    // Add the timeslot to the list
                    timeslots.add(timeslot);

                    // Calculate the start time of the next consultation
                    slotStart = slotEnd.plus(breakDuration);
                }

                // Move to the next occurrence of the selected day for the next week
                nextSelectedDay = nextSelectedDay.plusWeeks(1);
            }

            // Add the generated timeslots to the teacher's timeslots
            teacherService.addTimeslotsToTeacher(teacher.getId(), timeslots);
        }
    }
}





