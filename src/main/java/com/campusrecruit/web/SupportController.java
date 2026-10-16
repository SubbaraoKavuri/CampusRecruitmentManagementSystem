package com.campusrecruit.web;

import com.campusrecruit.entity.SupportTicket;
import com.campusrecruit.repository.SupportTicketRepository;
import com.campusrecruit.service.EmailService;
import com.campusrecruit.web.dto.SupportRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController @RequestMapping("/api/support")
public class SupportController {
    private final SupportTicketRepository tickets; private final EmailService email;
    public SupportController(SupportTicketRepository tickets,EmailService email){this.tickets=tickets;this.email=email;}
    @GetMapping("/contact") public Map<String,String> contact(){return Map.of("email",email.supportAddress());}
    @PostMapping public Map<String,Object> create(@Valid @RequestBody SupportRequest r){
        SupportTicket ticket=tickets.save(new SupportTicket(r.name(),r.email().toLowerCase(),r.subject(),r.message()));
        boolean sent=email.sendSupport(r.name(),r.email(),r.subject(),r.message());
        return Map.of("id",ticket.getId(),"message",sent?"Your request was sent to support.":"Your request was saved. Email delivery is not configured yet.","supportEmail",email.supportAddress());
    }
    @GetMapping @PreAuthorize("hasRole('ADMIN')") public List<SupportTicket> list(){return tickets.findAllByOrderByCreatedAtDesc();}
    @PatchMapping("/{id}/status") @PreAuthorize("hasRole('ADMIN')") public SupportTicket status(@PathVariable Long id,@RequestBody Map<String,String> body){
        String next=body.getOrDefault("status","").toUpperCase(); if(!List.of("OPEN","IN_PROGRESS","RESOLVED").contains(next)) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"Invalid support status");
        SupportTicket ticket=tickets.findById(id).orElseThrow();ticket.setStatus(next);return tickets.save(ticket);
    }
}
