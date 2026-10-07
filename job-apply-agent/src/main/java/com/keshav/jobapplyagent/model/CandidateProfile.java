package com.keshav.jobapplyagent.model;

import lombok.Data;

import java.util.List;

@Data
public class CandidateProfile {
    private String name="Keshav Meena";
    private String email="keshavmeena2510@gmail.com";
    private String phone="+91 6263058239";
    private String github="https://github.com/keshavmeena2510";
    private String linkedin = "https://www.linkedin.com/in/keshav-meena-1b200f2a7";
    private String noticePeriod="Immediate Joiner (0 days)";
    private int experienceYears=2;
    private List<String> skills = List.of(
            "Java 17", "Spring Boot", "Rest APIs","Microservices",
           "PostgreSQL","Hibernate/JPA","Docker","Redis","Kafka"
    );
    private String highlightProject = "Enterprise HRMS & Payroll Service Engine: Architected microservices with Spring Boot & PostgreSQL, automated statutory payroll logic, multi-tenancy, and idempotent transaction flows.";
}
