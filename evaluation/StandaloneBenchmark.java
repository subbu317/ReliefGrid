import dev.reliefgrid.engine.AllocationEngine;
import dev.reliefgrid.model.Category;
import java.util.*;
public class StandaloneBenchmark {
 static void assertThat(boolean x,String s){if(!x) throw new RuntimeException(s);}
 public static void main(String[] args){
 Random rnd=new Random(317);List<AllocationEngine.Place> places=new ArrayList<>();for(int i=1;i<=5;i++)places.add(new AllocationEngine.Place(i,16.49+i*.012,80.60+i*.014,i%2==0));List<AllocationEngine.Stock> stocks=new ArrayList<>();long id=1;for(var p:places)for(Category c:Category.values())stocks.add(new AllocationEngine.Stock(id++,p.id(),c,4+rnd.nextInt(8)));List<AllocationEngine.Demand> demands=new ArrayList<>();for(int i=0;i<20;i++)demands.add(new AllocationEngine.Demand(i+1,1+i%5,Category.values()[i%4],4+rnd.nextInt(8),1+rnd.nextInt(5),i));List<AllocationEngine.Carrier> carriers=new ArrayList<>();for(int i=0;i<10;i++)carriers.add(new AllocationEngine.Carrier(i+1,1+i%5,7+rnd.nextInt(8),true));var in=new AllocationEngine.Input(places,stocks,demands,carriers);
 for(var kind:List.of("baseline","optimized")){
 var r=kind.equals("baseline")?AllocationEngine.baseline(in):AllocationEngine.optimized(in);
 Map<Long,Integer> byStock=new HashMap<>(),byVolunteer=new HashMap<>(),byDemand=new HashMap<>();for(var pick:r.picks()){byStock.merge(pick.stockId(),pick.quantity(),Integer::sum);byVolunteer.merge(pick.carrierId(),pick.quantity(),Integer::sum);byDemand.merge(pick.demandId(),pick.quantity(),Integer::sum);}
 for(var s:stocks)assertThat(byStock.getOrDefault(s.id(),0)<=s.quantity(),"stock cap");for(var v:carriers)assertThat(byVolunteer.getOrDefault(v.id(),0)<=v.capacity(),"volunteer cap");for(var d:demands)assertThat(byDemand.getOrDefault(d.id(),0)<=d.quantity(),"demand cap");
 int high=0,highFulfilled=0,total=0;Map<Long,Integer> totalByShelter=new HashMap<>(),servedByShelter=new HashMap<>();for(var d:demands){int served=byDemand.getOrDefault(d.id(),0);if(d.urgency()>=4){high+=d.quantity();highFulfilled+=served;}total+=d.quantity();totalByShelter.merge(d.shelterId(),d.quantity(),Integer::sum);servedByShelter.merge(d.shelterId(),served,Integer::sum);}double mean=0;for(var s:places)mean+=(double)servedByShelter.getOrDefault(s.id(),0)/totalByShelter.get(s.id());mean/=places.size();double var=0;for(var s:places){double delta=(double)servedByShelter.getOrDefault(s.id(),0)/totalByShelter.get(s.id())-mean;var+=delta*delta;}var/=places.size();
 System.out.printf(Locale.ROOT,"%s: units=%d highPriorityPct=%.2f unmet=%d fairnessStdDev=%.4f distanceKmUnits=%.2f violation=0 computeMs=%.3f%n",kind,r.allocated(),100.0*highFulfilled/high,total-r.allocated(),Math.sqrt(var),r.distance(),r.nanos()/1e6);
 }
 }
}
