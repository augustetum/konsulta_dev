package com.konsulta.application.data.service;

import com.konsulta.application.data.entity.Consultation;
import com.konsulta.application.data.entity.Parent;
import com.konsulta.application.data.entity.Teacher;
import com.konsulta.application.data.repository.ConsultationRepository;
import com.konsulta.application.data.repository.ParentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultationService {

        private final ConsultationRepository consultationRepository;

        public ConsultationService(ConsultationRepository consultationRepository) {
            this.consultationRepository = consultationRepository;
        }

    @Transactional
    public List<Consultation> getConsultationsByTeacher(Teacher teacher) {
        return consultationRepository.findByTeacher(teacher);
    }

    public boolean parentHasConsultations(Parent parent) {
        return consultationRepository.existsByParent(parent);
    }


}
