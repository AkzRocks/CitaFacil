package com.medicina.citafacil.repository;

import com.medicina.citafacil.model.MedicalRecord;
import com.medicina.citafacil.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientOrderByDateDesc(Patient patient);
}
