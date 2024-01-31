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
        //the next occurrence of the selected day of the week
        LocalDate currentDate = LocalDate.now();
        LocalDate nextSelectedDay = currentDate.with(TemporalAdjusters.nextOrSame(selectedDay));

        Duration consultationDuration = Duration.ofMinutes(15); //duration of each consultation

        Duration breakDuration = Duration.ofMinutes(5);

        List<Timeslot> timeslots = new ArrayList<>(); //generated timeslots list

        // Fetch the teacher from the database using the teacherId
        Optional<Teacher> teacherOptional = teacherService.get(teacherId);

        if (teacherOptional.isPresent()) {
            Teacher teacher = teacherOptional.get();

            for (int i = 0; i < 3; i++) {
                LocalDateTime slotStart = nextSelectedDay.atTime(startTime);

                for (int j = 0; j < 3; j++) {

                    LocalDateTime slotEnd = slotStart.plus(consultationDuration); //calculates the end time of the consultation

                    Timeslot timeslot = new Timeslot(slotStart, slotEnd);
                    timeslot.setTeacher(teacher);
                    timeslotRepository.save(timeslot);
                    timeslots.add(timeslot);

                    slotStart = slotEnd.plus(breakDuration); //calculates the start of the next timeslot w 5 minute break
                }

                nextSelectedDay = nextSelectedDay.plusWeeks(1);
            }

            teacherService.addTimeslotsToTeacher(teacher.getId(), timeslots);
        }
    }

    @Transactional
    public void handleUnavailability(Long teacherId, LocalDate startDate, LocalDate endDate) {
        // Logic to handle unavailability:
        // 1. Delete or mark as unavailable existing timeslots within the range
        // 2. Adjust the timeslot generation logic to skip the unavailable period
    }
}





