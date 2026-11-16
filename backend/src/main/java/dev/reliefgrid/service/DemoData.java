package dev.reliefgrid.service;
import dev.reliefgrid.model.*;import dev.reliefgrid.repo.*;import org.springframework.boot.CommandLineRunner;import org.springframework.stereotype.Component;
import java.util.*;
@Component public class DemoData implements CommandLineRunner {
 private final ShelterRepository shelters;private final ResourceStockRepository stocks;private final CitizenRequestRepository requests;private final VolunteerRepository volunteers;
 public DemoData(ShelterRepository a,ResourceStockRepository b,CitizenRequestRepository c,VolunteerRepository d){shelters=a;stocks=b;requests=c;volunteers=d;}
 @Override public void run(String...args){if(shelters.count()>0)return;seed();}
 public void seed(){if(shelters.count()>0)return;List<Shelter> sh=shelters.saveAll(List.of(new Shelter("Riverside Shelter",16.5151,80.6320,true),new Shelter("North Community Hall",16.5420,80.6100,false),new Shelter("Central Relief Hub",16.5080,80.6470,false),new Shelter("East School Shelter",16.5270,80.6840,true),new Shelter("South Volunteer Center",16.4810,80.6350,true)));
  Category[] categories=Category.values();int[][] supplies={{14,10,6,5},{8,11,7,6},{12,8,5,4},{5,8,6,3},{7,5,4,6}};
  for(int i=0;i<sh.size();i++)for(int j=0;j<categories.length;j++)stocks.save(new ResourceStock(sh.get(i),categories[j],supplies[i][j]));
  Random random=new Random(317);for(int i=0;i<20;i++)requests.save(new CitizenRequest(sh.get(i%5),categories[(i*3+i/5)%4],3+random.nextInt(8),1+random.nextInt(5),null));
  for(int i=0;i<10;i++)volunteers.save(new Volunteer("Volunteer "+String.format("%02d",i+1),sh.get((i*2)%5),7+random.nextInt(9)));
 }
}
