package dev.reliefgrid.model;
import jakarta.persistence.*;
@Entity public class Shelter { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @Column(nullable=false,unique=true) public String name; public double lat,lon; public boolean underserved;
 public Shelter(){} public Shelter(String n,double la,double lo,boolean u){name=n;lat=la;lon=lo;underserved=u;} }
