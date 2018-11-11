package cartago.infrastructure.web;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import cartago.ICartagoContext;

public class AgentCallbackRegistry {

  private static AgentCallbackRegistry registry;
  private Map<String,Set<ICartagoContext>> agentBodies;
  
  private Map<String,Map<Long,Long>> actionIdMaps;

  
  private AgentCallbackRegistry() {
    agentBodies = new Hashtable<String,Set<ICartagoContext>>();
    actionIdMaps = new Hashtable<String,Map<Long,Long>>();
  }
  
  public static synchronized AgentCallbackRegistry getInstance() {
    if (registry == null) {
        registry = new AgentCallbackRegistry();
    }
    
    return registry;
  }
  
  public Set<ICartagoContext> getCartagoCallbacks(String agentIRI) {
    return agentBodies.getOrDefault(agentIRI, new HashSet<ICartagoContext>());
  }
  
  public Map<Long,Long> getActionIdMap(String agentIRI) {
    return actionIdMaps.getOrDefault(agentIRI, new HashMap<Long,Long>());
  }
  
  public void addCallbackIRI(String agentIRI, ICartagoContext agentBody) {
    Set<ICartagoContext> bodies = registry.getCartagoCallbacks(agentIRI);
    bodies.add(agentBody);
    
    agentBodies.put(agentIRI, bodies);
  }
  
  public void addActionIdMapping(String agentIRI, Long remoteActionId, Long localActionId) {
    Map<Long,Long> actionIdMap = getActionIdMap(agentIRI);
    actionIdMap.put(remoteActionId, localActionId);
    
    actionIdMaps.put(agentIRI, actionIdMap);
  }
  
  public void removeCallbackIRI(String agentIRI, ICartagoContext agentBody) {
    Set<ICartagoContext> callbacks = registry.getCartagoCallbacks(agentIRI);
    callbacks.remove(agentBody);
    
    if (callbacks.isEmpty()) {
      agentBodies.remove(agentIRI);
    } else {
      agentBodies.put(agentIRI, callbacks);
    }
  }
  
  public long getLocalActionId(String agentIRI, long remoteActionId) {
    Map<Long,Long> mapping = getActionIdMap(agentIRI);
    long localActionId = mapping.remove(remoteActionId);
    
    actionIdMaps.put(agentIRI, mapping);
    
    return localActionId;
  }
}
