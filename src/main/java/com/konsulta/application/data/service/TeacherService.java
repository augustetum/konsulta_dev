package com.konsulta.application.data.service;

import com.konsulta.application.data.entity.Consultation;
import com.konsulta.application.data.entity.ConsultationStatus;
import com.konsulta.application.data.entity.Teacher;
import com.konsulta.application.data.entity.Timeslot;
import com.konsulta.application.data.repository.TeacherRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    private final TeacherRepository repository;

    private final ConsultationService consultationService;

    public TeacherService(TeacherRepository repository, ConsultationService consultationService) {
        this.repository = repository;
        this.consultationService = consultationService;
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
                .filter(timeslot -> !consultationTimeslotIds.contains(timeslot.getId()) ||
                        consultationTimeslotIds.stream().noneMatch(id -> isConsultationCancelled(teacherConsultations, id)))
                .collect(Collectors.toList());

        return availableTimeslots;
    }

    private boolean isConsultationCancelled(List<Consultation> consultations, Long timeslotId) {
        return consultations.stream()
                .anyMatch(consultation -> consultation.getTimeslot().getId().equals(timeslotId)
                        && consultation.getStatus() != ConsultationStatus.CANCELLED_BY_TEACHER
                        && consultation.getStatus() != ConsultationStatus.CANCELLED_BY_PARENT);
    }

    @Transactional
    public void removeScheduledTimeslot(Teacher teacher, Timeslot timeslot) {
        if (teacher != null && timeslot != null) {
            List<Timeslot> availableTimeslots = teacher.getTimeslots();
            availableTimeslots.remove(timeslot);
            repository.save(teacher);
        }
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


}




