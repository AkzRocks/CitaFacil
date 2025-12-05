package com.medicina.citafacil.repository;

import com.medicina.citafacil.model.Appointment;
import com.medicina.citafacil.model.Doctor;
import com.medicina.citafacil.model.Patient;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctor(Doctor doctor);

    List<Appointment> findByPatient(Patient patient);

    boolean existsByDoctorAndDateAndTimeAndStatusNot(Doctor doctor,
                                                     LocalDate date,
                                                     LocalTime time,
                                                     Appointment.AppointmentStatus statusToExclude);
}
