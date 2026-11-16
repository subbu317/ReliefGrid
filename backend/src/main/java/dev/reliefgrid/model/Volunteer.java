package dev.reliefgrid.model;
import jakarta.persistence.*;
@Entity public class Volunteer { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; public String displayName; @ManyToOne(optional=false) public Shelter shelter; public int capacity,committed; public boolean active; @Version public long version;
 public Volunteer(){} public Volunteer(String name,Shelter s,int cap){displayName=name;shelter=s;capacity=cap;active=true;} }
