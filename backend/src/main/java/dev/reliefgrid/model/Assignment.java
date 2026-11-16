package dev.reliefgrid.model;
import jakarta.persistence.*;
@Entity public class Assignment { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @ManyToOne(optional=false) public AllocationPlan plan; @ManyToOne(optional=false) public CitizenRequest request; @ManyToOne(optional=false) public ResourceStock stock; @ManyToOne(optional=false) public Volunteer volunteer; public int quantity; public double distanceKm; @Column(length=1000) public String explanation; public String status;
 public Assignment(){} public Assignment(AllocationPlan p,CitizenRequest r,ResourceStock s,Volunteer v,int q,double d,String e){plan=p;request=r;stock=s;volunteer=v;quantity=q;distanceKm=d;explanation=e;status="PROPOSED";} }
