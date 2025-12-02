package com.medicina.citafacil.repository;

import com.medicina.citafacil.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    boolean existsByCmp(String cmp);
}
