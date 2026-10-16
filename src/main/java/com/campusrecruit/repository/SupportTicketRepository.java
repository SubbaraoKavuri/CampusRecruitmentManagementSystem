package com.campusrecruit.repository;

import com.campusrecruit.entity.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    java.util.List<SupportTicket> findAllByOrderByCreatedAtDesc();
}
