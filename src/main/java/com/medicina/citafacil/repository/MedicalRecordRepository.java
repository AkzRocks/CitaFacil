package com.medicina.citafacil.repository;

import com.medicina.citafacil.model.MedicalRecord;
import com.medicina.citafacil.model.Patient;
import com.medicina.citafacil.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientOrderByDateDesc(Patient patient);

    Optional<MedicalRecord> findByAppointment(Appointment appointment);
}
