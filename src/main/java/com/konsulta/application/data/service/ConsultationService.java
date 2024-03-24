package com.konsulta.application.data.service;

import com.konsulta.application.data.entity.*;
import com.konsulta.application.data.repository.ConsultationRepository;
import com.konsulta.application.data.repository.ParentRepository;
import com.konsulta.application.data.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultationService {

        private final ConsultationRepository consultationRepository;
        private final TeacherService teacherService;


        public ConsultationService(ConsultationRepository consultationRepository, TeacherService teacherService) {
            this.consultationRepository = consultationRepository;
            this.teacherService = teacherService;
        }

    @Transactional
    public List<Consultation> getConsultationsByTeacher(Teacher teacher) {
        return consultationRepository.findByTeacher(teacher);
    }

    @Transactional
    public List<Consultation> getConsultationsByParent(Parent parent) {
        return consultationRepository.findByParent(parent);
    }

    @Transactional
    public void cancelConsultationByParent(Long consultationId) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new EntityNotFoundException("Consultation not found"));
        consultation.setStatus(ConsultationStatus.CANCELLED_BY_PARENT);
        consultationRepository.save(consultation);
    }

    @Transactional
    public void cancelConsultationByTeacher(Long consultationId) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new EntityNotFoundException("Consultation not found"));
        consultation.setStatus(ConsultationStatus.CANCELLED_BY_TEACHER);
        consultationRepository.save(consultation);
    }

    public boolean parentHasConsultations(Parent parent) {
        return consultationRepository.existsByParent(parent);
    }
    public boolean teacherHasConsultations(Teacher teacher) {
        return consultationRepository.existsByTeacher(teacher);
    }


}
