package com.example.adf.repo;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.adf.dto.Lead;
import com.example.adf.dto.LeadBidStatus;
import com.example.adf.dto.LeadListingRequest;

public interface LeadListingRequestRepo extends JpaRepository<LeadListingRequest, Long> {
  

  List<Lead> findByLeadId(long id);


}
