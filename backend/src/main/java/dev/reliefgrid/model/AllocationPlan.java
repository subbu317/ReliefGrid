package dev.reliefgrid.model;
import jakarta.persistence.*; import java.time.Instant;
@Entity public class AllocationPlan { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; public String status; public Instant createdAt; public AllocationPlan(){} public AllocationPlan(String st){status=st;createdAt=Instant.now();} }
