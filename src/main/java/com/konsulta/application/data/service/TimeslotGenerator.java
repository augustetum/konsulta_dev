package com.konsulta.application.data.service;

import com.konsulta.application.data.entity.Teacher;
import com.konsulta.application.data.entity.Timeslot;
import com.konsulta.application.data.repository.TeacherRepository;
import com.konsulta.application.data.repository.TimeslotRepository;
import com.konsulta.application.data.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TimeslotGenerator {

    private final TeacherService teacherService;
    private final TimeslotRepository timeslotRepository; // Autowire the TimeslotRepository
    private final TeacherRepository teacherRepository;

    @Autowired
    public TimeslotGenerator(TeacherService teacherService, TimeslotRepository timeslotRepository, TeacherRepository teacherRepository) {
        this.teacherService = teacherService;
        this.timeslotRepository = timeslotRepository;
        this.teacherRepository = teacherRepository;
    }

    @Transactional
    public void generateTimeslots(Long teacherId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime endDate = currentDate.plusDays(14).withHour(endTime.getHour()).withMinute(endTime.getMinute());

        List<Timeslot> generatedTimeslots = new ArrayList<>();

        int slotDuration = 15; // minutes
        int breakDuration = 5; // minutes

        // Calculate the start and end times for slots within the specified range
        LocalTime slotStartTime = startTime;
        LocalTime slotEndTime = startTime.plusMinutes(slotDuration);

        while (slotEndTime.isBefore(endTime) || (slotEndTime.equals(endTime))) {
            LocalDateTime slotStart = LocalDateTime.of(currentDate.getYear(), currentDate.getMonthValue(), currentDate.getDayOfMonth(), slotStartTime.getHour(), slotStartTime.getMinute());
            LocalDateTime slotEnd = LocalDateTime.of(currentDate.getYear(), currentDate.getMonthValue(), currentDate.getDayOfMonth(), slotEndTime.getHour(), slotEndTime.getMinute());

            // Create timeslots on the specified day of the week
            if (slotStart.getDayOfWeek() == dayOfWeek) {
                Timeslot timeslot = new Timeslot(slotStart, slotEnd);

                // Check if the timeslot overlaps with existing ones
               // if (!overlapsExistingTimeslots(teacherId, timeslot)) {
                    // Save the Timeslot entity to the database
                  //  timeslotRepository.save(timeslot);
                   // generatedTimeslots.add(timeslot);
              //  }
            }

            // Move to the next slot
            slotStartTime = slotEndTime.plusMinutes(breakDuration);
            slotEndTime = slotStartTime.plusMinutes(slotDuration);
        }

        // Load the teacher entity within a transactional context
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);

        if (teacher != null) {
            // Add the generated timeslots to the teacher's list
            teacherService.addTimeslotsToTeacher(teacherId, generatedTimeslots);
        }
    }

   // private boolean overlapsExistingTimeslots(Long teacherId, Timeslot newTimeslot) {
     //   List<Timeslot> existingTimeslots = timeslotRepository.findAllByTeacherIdAndEndTimeAfterAndStartTimeBefore(teacherId, newTimeslot.getStartTime(), newTimeslot.getEndTime());
      //  return !existingTimeslots.isEmpty();
   // }

}


