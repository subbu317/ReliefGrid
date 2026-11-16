package dev.reliefgrid.model;
import jakarta.persistence.*; import java.time.Instant;
@Entity public class AuditLog { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; public String action; @Column(length=1500) public String details; public Instant atTime;
 public AuditLog(){} public AuditLog(String a,String d){action=a;details=d;atTime=Instant.now();} }
