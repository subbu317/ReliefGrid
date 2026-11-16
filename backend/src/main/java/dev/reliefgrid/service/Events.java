package dev.reliefgrid.service;
import org.springframework.stereotype.Component; import org.springframework.web.servlet.mvc.method.annotation.SseEmitter; import java.util.concurrent.CopyOnWriteArrayList;
@Component public class Events {
 private final CopyOnWriteArrayList<SseEmitter> clients=new CopyOnWriteArrayList<>();
 public SseEmitter subscribe(){SseEmitter e=new SseEmitter(0L);clients.add(e);e.onCompletion(()->clients.remove(e));e.onError(x->clients.remove(e));try{e.send(SseEmitter.event().name("connected").data("ready"));}catch(Exception x){clients.remove(e);}return e;}
 public void changed(String event){for(SseEmitter e:clients){try{e.send(SseEmitter.event().name("change").data(event));}catch(Exception x){clients.remove(e);e.complete();}}}
}
