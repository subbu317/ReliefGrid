package dev.reliefgrid.model;
import jakarta.persistence.*; import java.time.Instant;
@Entity public class SyncRecord { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @Column(nullable=false,unique=true) public String syncKey; @Column(nullable=false) public String payloadHash; @ManyToOne(optional=false) public CitizenRequest request; public Instant createdAt;
 public SyncRecord(){} public SyncRecord(String key,String hash,CitizenRequest r){syncKey=key;payloadHash=hash;request=r;createdAt=Instant.now();} }
