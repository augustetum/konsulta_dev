package com.konsulta.application.data.service;
import com.konsulta.application.data.entity.Teacher;
import com.konsulta.application.data.entity.Timeslot;
import com.konsulta.application.data.repository.TimeslotRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class TimeslotGenerator {

    @Autowired
    private TimeslotRepository timeslotRepository;

    @Autowired
    private TeacherService teacherService;

    @Transactional
    public void generateTimeslots(Long teacherId, DayOfWeek selectedDay, LocalTime startTime, LocalTime endTime) {
        Optional<Teacher> teacherOptional = teacherService.get(teacherId); //fetches the teacher
        if (teacherOptional.isPresent()) {
            Teacher teacher = teacherOptional.get();
            LocalDate currentDate = LocalDate.now();
            generateTimeslots(teacher, currentDate, selectedDay, startTime, endTime, 0);
        }
    }

    private void generateTimeslots(Teacher teacher, LocalDate currentDate, DayOfWeek selectedDay, LocalTime startTime, LocalTime endTime, int weekCounter) {
        if (weekCounter == 3) { //generates for 3 weeks, then stops
            return;
        }

        LocalDate nextSelectedDay = currentDate.with(TemporalAdjusters.nextOrSame(selectedDay));
        List<Timeslot> timeslots = new ArrayList<>();

        LocalDateTime slotStart = nextSelectedDay.atTime(startTime);
        while (slotStart.toLocalTime().isBefore(endTime)) { // Generate timeslots for the day
            LocalDateTime slotEnd = slotStart.plusMinutes(15); // End time for a 15-minute consultation

            if (slotEnd.toLocalTime().isAfter(endTime)) {
                break; // end time is reached, doesn't book after it
            }

            Timeslot timeslot = new Timeslot(slotStart, slotEnd);
            timeslot.setTeacher(teacher);
            timeslotRepository.save(timeslot);
            timeslots.add(timeslot);

            slotStart = slotEnd.plusMinutes(5); // 5 minute break implemented
        }

        //adds timeslots for the teacher
        teacherService.addTimeslotsToTeacher(teacher.getId(), timeslots);

        //recursively generates for another week
        generateTimeslots(teacher, nextSelectedDay.plusWeeks(1), selectedDay, startTime, endTime, weekCounter + 1);
    }

}





