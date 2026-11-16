package dev.reliefgrid.service;
import dev.reliefgrid.api.Dto; import org.springframework.stereotype.Service; import org.springframework.dao.DataIntegrityViolationException; import java.util.*;
@Service public class SyncBatchService {
 private final SyncService sync; public SyncBatchService(SyncService s){sync=s;}
 public List<Dto.SyncResult> batch(List<Dto.SyncItem> items){if(items.size()>100)throw new IllegalArgumentException("Batch max 100");List<Dto.SyncResult> out=new ArrayList<>();for(Dto.SyncItem item:items)try{out.add(sync.one(item));}catch(DataIntegrityViolationException e){out.add(new Dto.SyncResult(item.key(),"RETRY",null,"Concurrent submission; retry"));}catch(IllegalArgumentException e){out.add(new Dto.SyncResult(item.key(),"INVALID",null,e.getMessage()));}return out;}
}
