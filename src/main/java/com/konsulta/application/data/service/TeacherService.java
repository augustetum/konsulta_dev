package com.konsulta.application.data.service;

import com.konsulta.application.data.entity.*;
import com.konsulta.application.data.repository.ConsultationRepository;
import com.konsulta.application.data.repository.TeacherRepository;
import com.konsulta.application.data.repository.TimeslotRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.konsulta.application.data.repository.ConsultationRepository;

import javax.mail.MessagingException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TeacherService {

    private final TeacherRepository repository;
    private final TimeslotRepository timeslotRepository;

    @Lazy
    @Autowired
    private  ConsultationService consultationService;

    private final ConsultationRepository consultationRepository;

    public TeacherService(TeacherRepository repository, ConsultationRepository consultationRepository, TimeslotRepository timeslotRepository){
        this.repository = repository;
        this.consultationRepository = consultationRepository;
        this.timeslotRepository = timeslotRepository;
    }

    public Optional<Teacher> get(Long id) {
        return repository.findById(id);
    }

    public Teacher update(Teacher entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Teacher> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Teacher> list(Pageable pageable, Specification<Teacher> filter) {
        return repository.findAll(filter, pageable);
    }

    public Teacher findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public int count() {
        return (int) repository.count();
    }

    public boolean isValidTeacherLogin(String email, String password) {
        Teacher teacher = repository.findByEmail(email);
        return teacher != null && teacher.getPassword().equals(password);
    }

    public List<Teacher> getAllTeachers() {
        return repository.findAll();
    }

    public void addTimeslotsToTeacher(Long teacherId, List<Timeslot> timeslots) {
        Optional<Teacher> teacherOptional = repository.findById(teacherId);
        if (teacherOptional.isPresent()) {
            Teacher teacher = teacherOptional.get();
            // Add the new timeslots to the existing list
            teacher.getTimeslots().addAll(timeslots);
            repository.save(teacher);
        }
    }

    public void addTimeslotsToTeacher(Teacher teacher, Timeslot timeslot) {
        this.addTimeslotsToTeacher(teacher.getId(), Collections.singletonList(timeslot));
    }

/*
    @Transactional
    public List<Timeslot> getAvailableTimeslots(Teacher teacher) {
        // Retrieve all timeslots for the teacher
        List<Timeslot> allTimeslots = teacher.getTimeslots();

        // Retrieve all consultations associated with the teacher
        List<Consultation> teacherConsultations = consultationService.getConsultationsByTeacher(teacher);

        // Create a set to store the timeslot IDs associated with consultations
        Set<Long> consultationTimeslotIds = teacherConsultations.stream()
                .map(Consultation::getTimeslot)
                .map(Timeslot::getId)
                .collect(Collectors.toSet());

        // Filter out timeslots that have existing appointments with non-cancelled consultations
        List<Timeslot> availableTimeslots = allTimeslots.stream()
                .filter(timeslot -> {
                    // Check if the timeslot is not associated with any consultation
                    boolean isTimeslotFree = !consultationTimeslotIds.contains(timeslot.getId());
                    // Or if it is associated, ensure the consultation is cancelled by parent
                    boolean isConsultationCancelled = teacherConsultations.stream()
                            .filter(consultation -> consultation.getTimeslot().equals(timeslot))
                            .allMatch(consultation -> consultation.getStatus() == ConsultationStatus.CANCELLED_BY_PARENT);
                    return isTimeslotFree || isConsultationCancelled;
                })
                .collect(Collectors.toList());

        return availableTimeslots;
    }
*/

    @Transactional
    public List<Timeslot> getAvailableTimeslots(Teacher teacher) {
        List<Timeslot> allTimeslots = teacher.getTimeslots();
        List<Consultation> teacherConsultations = consultationService.getConsultationsByTeacher(teacher);

        // Identifying timeslots tied to cancelled consultations
        List<Timeslot> cancelledTimeslots = teacherConsultations.stream()
                .filter(consultation -> consultation.getStatus() == ConsultationStatus.CANCELLED_BY_PARENT)
                .map(Consultation::getTimeslot)
                .collect(Collectors.toList());

        // Cloning and persisting new timeslots for each cancelled one
        List<Timeslot> clonedTimeslots = new ArrayList<>();
        for (Timeslot cancelledTimeslot : cancelledTimeslots) {
            Timeslot newTimeslot = cloneTimeslot(cancelledTimeslot);
            // Assuming there's a repository or service to save timeslots
            timeslotRepository.save(newTimeslot);
            clonedTimeslots.add(newTimeslot);
        }

        // Combining allTimeslots with clonedTimeslots, excluding original cancelled ones
        Set<Long> cancelledTimeslotIds = cancelledTimeslots.stream()
                .map(Timeslot::getId)
                .collect(Collectors.toSet());

        List<Timeslot> availableTimeslots = Stream.concat(
                        allTimeslots.stream()
                                .filter(timeslot -> !cancelledTimeslotIds.contains(timeslot.getId())), // Excluding cancelled
                        clonedTimeslots.stream()) // Including cloned
                .collect(Collectors.toList());

        return availableTimeslots;
    }

    private Timeslot cloneTimeslot(Timeslot original) {
        Timeslot clone = new Timeslot();
        // Copy properties from original to clone, except ID
        clone.setStart(original.getStart());
        clone.setEnd(original.getEnd());
        // Copy any other necessary properties here
        return clone;
    }


    public void sendNotificationEmailToTeacher(Teacher selectedTeacher, String scheduledTime) {
        String teacherEmail = selectedTeacher.getEmail();
        String teacherSubject = "New Consultation Request";
        String teacherContent = "A new consultation has been scheduled with you for " + scheduledTime;

        try {
            EmailSender.sendEmail(teacherEmail, teacherSubject, teacherContent);
        } catch (MessagingException e) {
            e.printStackTrace(); // Handle email sending errors
        }
    }

    public void sendCancellationEmailToTeacher(Teacher selectedTeacher, String scheduledTime) {
        String teacherEmail = selectedTeacher.getEmail();
        String teacherSubject = "Consultation has been cancelled";
        String teacherContent = "A consultation has been cancelled at " + scheduledTime + "by a parent. ";

        try {
            EmailSender.sendEmail(teacherEmail, teacherSubject, teacherContent);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }



}




