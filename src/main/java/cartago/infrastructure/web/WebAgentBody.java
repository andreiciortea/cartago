package cartago.infrastructure.web;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import cartago.AgentBody;
import cartago.AgentId;
import cartago.ArtifactId;
import cartago.CartagoException;
import cartago.IAlignmentTest;
import cartago.ICartagoCallback;
import cartago.Op;
import cartago.WorkspaceKernel;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class WebAgentBody extends AgentBody {

  private final static Logger LOGGER = Logger.getLogger(WebAgentBody.class.getCanonicalName());
  
  public WebAgentBody(AgentId id, WorkspaceKernel env, ICartagoCallback agentCallback) {
    super(id, env, agentCallback);
  }
  
  @Override
  public void doAction(long actionId, ArtifactId aid, Op op, IAlignmentTest test, long timeout) throws CartagoException {
    doAction(actionId, aid.getName(), op, test, timeout);
  }
  
  @Override
  public void doAction(long actionId, String artifactName, Op op, IAlignmentTest test, long timeout) throws CartagoException {
    if (!getWorkspaceId().isWebResource()) {
      throw new CartagoException("Web action attepmpt in a non-Web workspace");
    }
    
    String workspaceIRI = getWorkspaceId().getIRI();
    String requestIRI = workspaceIRI + "/operations";
    
    try {
      HttpClient client = HttpClientBuilder.create().build();
      
      HttpPost request = new HttpPost(requestIRI);
      
      JsonObject opFrame = buildOperationFrame(artifactName, op);
      
      request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
      request.setEntity(new StringEntity(opFrame.toString()));
      
      HttpResponse response = client.execute(request);
      
      int statusCode = response.getStatusLine().getStatusCode();
      
      if (statusCode == HttpStatus.SC_OK) {
        String payload = EntityUtils.toString(response.getEntity());
        
        LOGGER.info("Operation successful: " + payload);
        
        long remoteActionId = Integer.parseInt(payload.substring(1, payload.length()-1));
        
        // TODO: agent IRI
        String agentIRI = "http://localhost:8081/notifications/" + getWorkspaceId().getName() + "/" + getAgentId().getGlobalId();
        LOGGER.info("Adding mapping [" + remoteActionId + "," + actionId + "] for agent callback: " + agentIRI);
        AgentCallbackRegistry.getInstance().addActionIdMapping(agentIRI, remoteActionId, actionId);
      } else {
        LOGGER.severe("Operation failed with status code: " + statusCode);
      }
    }
    catch (ClientProtocolException e) {
      LOGGER.severe(e.getMessage());
    }
    catch (IOException e) {
      LOGGER.severe(e.getMessage());
    }
  }
  
  private JsonObject buildOperationFrame(String artifactName, Op op) {
    JsonObject opFrame = new JsonObject();
    
    opFrame.put("agentName", getAgentId().getGlobalId());
    opFrame.put("artifactName", artifactName);
    
    JsonArray params = new JsonArray();
    for (Object value : op.getParamValues()) {
      params.add(value);
    }
    
    opFrame.put("operation", new JsonObject()
                                .put("name", op.getName())
                                .put("params", params)
        );
    
    LOGGER.info("Operation frame: " + opFrame.toString());
    
    return opFrame;
  }
  
}
