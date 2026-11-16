package dev.reliefgrid.api;
import dev.reliefgrid.model.*;import dev.reliefgrid.service.*;import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;import org.springframework.http.*;import org.springframework.web.bind.annotation.*;import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.*;
@RestController @RequestMapping("/api") public class ApiController {
 private final ResourceService resources;private final AllocationService allocation;private final SyncBatchService sync;private final Events events;
 @Value("${app.admin-key}") private String adminKey;
 public ApiController(ResourceService r,AllocationService a,SyncBatchService s,Events e){resources=r;allocation=a;sync=s;events=e;}
 private void admin(String key){if(!adminKey.equals(key))throw new Unauthorized();}
 @ResponseStatus(HttpStatus.FORBIDDEN) static class Unauthorized extends RuntimeException{}
 @GetMapping("/shelters") public List<Shelter> shelters(){return resources.shelters();}
 @GetMapping("/stocks") public List<ResourceStock> stocks(){return resources.stocks();}
 @GetMapping("/requests") public List<CitizenRequest> requests(){return resources.requests();}
 @GetMapping("/volunteers") public List<Volunteer> volunteers(){return resources.volunteers();}
 @GetMapping("/plans") public List<AllocationPlan> plans(){return allocation.plans();}
 @GetMapping("/assignments") public List<Assignment> assignments(){return allocation.assignments();}
 @GetMapping("/audits") public List<AuditLog> audits(){return allocation.audits();}
 @GetMapping("/comparison") public Map<String,Object> compare(){return allocation.compare();}
 @GetMapping("/events") public SseEmitter events(){return events.subscribe();}
 @PostMapping("/requests") public CitizenRequest request(@Valid @RequestBody Dto.RequestCreate d){return resources.addRequest(d,null);}
 @PostMapping("/sync") public List<Dto.SyncResult> sync(@RequestBody List<Dto.SyncItem> items){return sync.batch(items);}
 @PostMapping("/stocks") public ResourceStock stock(@RequestHeader(value="X-Admin-Key",required=false) String key,@Valid @RequestBody Dto.StockCreate d){admin(key);return resources.addStock(d);}
 @PostMapping("/volunteers") public Volunteer volunteer(@RequestHeader(value="X-Admin-Key",required=false) String key,@Valid @RequestBody Dto.VolunteerCreate d){admin(key);return resources.addVolunteer(d);}
 @PostMapping("/plans") public AllocationPlan propose(@RequestHeader(value="X-Admin-Key",required=false) String key){admin(key);return allocation.propose();}
 @PostMapping("/plans/{id}/approve") public AllocationPlan approve(@RequestHeader(value="X-Admin-Key",required=false) String key,@PathVariable long id){admin(key);return allocation.approve(id);}
 @PostMapping("/plans/{id}/reject") public AllocationPlan reject(@RequestHeader(value="X-Admin-Key",required=false) String key,@PathVariable long id){admin(key);return allocation.reject(id);}
 @PostMapping("/assignments/{id}/deliver") public Assignment delivered(@RequestHeader(value="X-Admin-Key",required=false) String key,@PathVariable long id){admin(key);return allocation.deliver(id);}
}
