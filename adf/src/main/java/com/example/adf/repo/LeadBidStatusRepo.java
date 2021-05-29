package com.example.adf.repo;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.adf.dto.Lead;
import com.example.adf.dto.LeadBidStatus;
import com.example.adf.dto.LeadListingRequest;

public interface LeadBidStatusRepo extends JpaRepository<LeadBidStatus, Long> {
  

  List<Lead> findByLeadId(long id);

}
