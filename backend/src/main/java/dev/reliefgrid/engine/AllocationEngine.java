package dev.reliefgrid.engine;
import dev.reliefgrid.model.Category;
import java.util.*;
import java.util.stream.*;
/** Standalone deterministic solver. Per-stock successive-shortest-augmenting-path min-cost flow;
 * each invocation is a category-pure graph and volunteer capacities are shared between invocations.
 * Unit demand arcs encode marginal priority/fairness utility. */
public final class AllocationEngine {
 public record Place(long id,double lat,double lon,boolean underserved){}
 public record Stock(long id,long shelterId,Category category,int quantity){}
 public record Demand(long id,long shelterId,Category category,int quantity,int urgency,long sequence){}
 public record Carrier(long id,long shelterId,int capacity,boolean active){}
 public record Input(List<Place> places,List<Stock> stocks,List<Demand> demands,List<Carrier> carriers){}
 public record Pick(long stockId,long demandId,long carrierId,int quantity,double distanceKm,double score,String explanation){}
 public record Result(List<Pick> picks,long nanos) {
  public int allocated(){return picks.stream().mapToInt(Pick::quantity).sum();}
  public double distance(){return picks.stream().mapToDouble(x->x.distanceKm()*x.quantity()).sum();}
 }
 private record Key(long s,long d,long v){}
 private static final class Edge {int to,rev,cap,original;double cost;Edge(int t,int r,int c,double co){to=t;rev=r;cap=c;original=c;cost=co;}}
 private static final class Graph {List<List<Edge>> g=new ArrayList<>(); Graph(int n){for(int i=0;i<n;i++)g.add(new ArrayList<>());} Edge add(int a,int b,int c,double cost){Edge f=new Edge(b,g.get(b).size(),c,cost),r=new Edge(a,g.get(a).size(),0,-cost);g.get(a).add(f);g.get(b).add(r);return f;}}
 private static double km(Place a,Place b){double x=Math.toRadians(b.lat()-a.lat()),y=Math.toRadians(b.lon()-a.lon());double z=Math.sin(x/2)*Math.sin(x/2)+Math.cos(Math.toRadians(a.lat()))*Math.cos(Math.toRadians(b.lat()))*Math.sin(y/2)*Math.sin(y/2);return 12742*Math.asin(Math.min(1,Math.sqrt(z)));}
 private static double benefit(Demand d,Place place,int alreadyAllocated){return 100.0*d.urgency()+ (place.underserved()?35:0)+30.0*(1-(double)alreadyAllocated/Math.max(1,d.quantity()));}
 private static Map<Long,Place> places(Input in){return in.places().stream().collect(Collectors.toMap(Place::id,x->x));}
 public static Result baseline(Input in){return solve(in,false);}
 public static Result optimized(Input in){return solve(in,true);}
 private static Result solve(Input in,boolean optimize){long started=System.nanoTime();Map<Long,Place> loc=places(in);Map<Long,Integer> remain=new HashMap<>(),carry=new HashMap<>();
  for(Demand d:in.demands())remain.put(d.id(),d.quantity());for(Carrier c:in.carriers())carry.put(c.id(),c.active()?c.capacity():0);
  List<Pick> out=new ArrayList<>(); List<Stock> stocks=new ArrayList<>(in.stocks());
  if(optimize) stocks.sort(Comparator.comparingInt((Stock s)->(int)in.demands().stream().filter(d->d.category()==s.category()).mapToInt(Demand::urgency).max().orElse(0)).reversed().thenComparingLong(Stock::id));
  else stocks.sort(Comparator.comparingLong(Stock::id));
  if(!optimize){ List<Demand> ordered=new ArrayList<>(in.demands());ordered.sort(Comparator.comparingLong(Demand::sequence).thenComparingLong(Demand::id));
   for(Demand d:ordered) for(Stock s:stocks){if(s.category()!=d.category())continue;int stockRemaining=s.quantity()-out.stream().filter(x->x.stockId()==s.id()).mapToInt(Pick::quantity).sum();while(stockRemaining>0&&remain.get(d.id())>0){Carrier chosen=null;double best=Double.POSITIVE_INFINITY;
    for(Carrier c:in.carriers())if(carry.get(c.id())>0){double dist=km(loc.get(s.shelterId()),loc.get(c.shelterId()))+km(loc.get(c.shelterId()),loc.get(d.shelterId()));if(dist<best){best=dist;chosen=c;}}
    if(chosen==null)break;int q=Math.min(Math.min(stockRemaining,remain.get(d.id())),carry.get(chosen.id()));out.add(new Pick(s.id(),d.id(),chosen.id(),q,best,benefit(d,loc.get(d.shelterId()),d.quantity()-remain.get(d.id())),"FCFS baseline: earlier request served first"));stockRemaining-=q;remain.merge(d.id(),-q,Integer::sum);carry.merge(chosen.id(),-q,Integer::sum);
   }}return new Result(List.copyOf(out),System.nanoTime()-started);}
  for(Stock stock:stocks){if(stock.quantity()<=0)continue;List<Demand> ds=in.demands().stream().filter(d->d.category()==stock.category()&&remain.get(d.id())>0).toList();List<Carrier> cs=in.carriers().stream().filter(c->carry.get(c.id())>0).toList();if(ds.isEmpty()||cs.isEmpty())continue;
   int n=2+cs.size()*2+ds.size();int source=0,sink=n-1,volStart=1,demStart=1+2*cs.size();Graph g=new Graph(n);
   Map<Key,Edge> arcs=new LinkedHashMap<>();Map<Key,Double> distances=new HashMap<>();
   for(int i=0;i<cs.size();i++){Carrier c=cs.get(i);int vin=volStart+i*2,vout=vin+1;g.add(source,vin,stock.quantity(),0);g.add(vin,vout,carry.get(c.id()),0);
    for(int j=0;j<ds.size();j++){Demand d=ds.get(j);double dist=km(loc.get(stock.shelterId()),loc.get(c.shelterId()))+km(loc.get(c.shelterId()),loc.get(d.shelterId()));Key k=new Key(stock.id(),d.id(),c.id());arcs.put(k,g.add(vout,demStart+j,Math.min(stock.quantity(),remain.get(d.id())),2*dist));distances.put(k,dist);}}
   for(int j=0;j<ds.size();j++){Demand d=ds.get(j);int already=d.quantity()-remain.get(d.id());for(int unit=0;unit<remain.get(d.id());unit++)g.add(demStart+j,sink,1,-benefit(d,loc.get(d.shelterId()),already+unit));}
   int flow=0;while(flow<stock.quantity()){
    double[] distance=new double[n];Arrays.fill(distance,Double.POSITIVE_INFINITY);distance[source]=0;int[] prevNode=new int[n],prevEdge=new int[n];Arrays.fill(prevNode,-1);
    // Bellman-Ford supports negative marginal utility and reverse residual arcs.
    for(int iter=0;iter<n-1;iter++){boolean changed=false;for(int u=0;u<n;u++)if(Double.isFinite(distance[u]))for(int ei=0;ei<g.g.get(u).size();ei++){Edge e=g.g.get(u).get(ei);double cand=distance[u]+e.cost;if(e.cap>0&&cand<distance[e.to]-1e-8){distance[e.to]=cand;prevNode[e.to]=u;prevEdge[e.to]=ei;changed=true;}}if(!changed)break;}
    if(prevNode[sink]<0||distance[sink]>=-1e-8)break;int f=stock.quantity()-flow;
    for(int v=sink;v!=source;v=prevNode[v]){if(prevNode[v]<0)throw new IllegalStateException("Residual path broken");f=Math.min(f,g.g.get(prevNode[v]).get(prevEdge[v]).cap);}
    for(int v=sink;v!=source;v=prevNode[v]){Edge e=g.g.get(prevNode[v]).get(prevEdge[v]);e.cap-=f;g.g.get(v).get(e.rev).cap+=f;}flow+=f;
   }
   Map<Long,Integer> demandUsed=new HashMap<>();Map<Long,Integer> carrierUsed=new HashMap<>();
   for(var entry:arcs.entrySet()){Key k=entry.getKey();int q=entry.getValue().original-entry.getValue().cap;if(q<=0)continue;Demand d=ds.stream().filter(x->x.id()==k.d()).findFirst().orElseThrow();double dist=distances.get(k);double util=benefit(d,loc.get(d.shelterId()),d.quantity()-remain.get(d.id()));String explanation="Urgency "+d.urgency()+"/5; "+(loc.get(d.shelterId()).underserved()?"underserved shelter fairness bonus; ":"")+"marginal priority utility "+String.format(Locale.ROOT,"%.1f",util)+"; route "+String.format(Locale.ROOT,"%.1f",dist)+" km; quantity "+q+"; min-cost flow recommendation (not committed)";
    out.add(new Pick(k.s(),k.d(),k.v(),q,dist,util,explanation));demandUsed.merge(k.d(),q,Integer::sum);carrierUsed.merge(k.v(),q,Integer::sum);
   }
   demandUsed.forEach((id,q)->remain.merge(id,-q,Integer::sum));carrierUsed.forEach((id,q)->carry.merge(id,-q,Integer::sum));
  }
  return new Result(List.copyOf(out),System.nanoTime()-started);
 }
}
