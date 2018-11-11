package cartago.infrastructure.web.models;

import java.util.ArrayList;
import java.util.Collection;

public class Workspace {
  
  private Collection<String> agents;
  private Collection<String> artifacts;
  
  public Workspace() {
    agents = new ArrayList<String>();
    artifacts = new ArrayList<String>();
  }
  
  public Collection<String> getAgents() {
    return agents;
  }
  
  public Collection<String> getArtifacts() {
    return artifacts;
  }
}
