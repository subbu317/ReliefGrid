package dev.reliefgrid.model;
import jakarta.persistence.*; import java.time.Instant;
@Entity public class CitizenRequest { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @ManyToOne(optional=false) public Shelter shelter; @Enumerated(EnumType.STRING) @Column(nullable=false) public Category category; public int quantity,fulfilled,urgency; @Column(unique=true) public String sourceKey; public Instant createdAt;
 public CitizenRequest(){} public CitizenRequest(Shelter s,Category c,int q,int u,String key){shelter=s;category=c;quantity=q;urgency=u;sourceKey=key;createdAt=Instant.now();} }
