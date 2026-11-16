package dev.reliefgrid.model;
import jakarta.persistence.*;
@Entity public class ResourceStock { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @ManyToOne(optional=false) public Shelter shelter; @Enumerated(EnumType.STRING) @Column(nullable=false) public Category category; public int available,reserved; @Version public long version;
 public ResourceStock(){} public ResourceStock(Shelter s,Category c,int q){shelter=s;category=c;available=q;} }
