package com.example.adf.repo;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.adf.dto.Lead;

public interface LeadRepository extends JpaRepository<Lead, Long> {
  

  List<Lead> findById(long id);


}
