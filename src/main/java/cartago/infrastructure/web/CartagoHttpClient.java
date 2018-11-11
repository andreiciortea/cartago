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

import cartago.ICartagoContext;
import cartago.infrastructure.web.models.Workspace;
import io.vertx.core.json.JsonObject;

public class CartagoHttpClient {
  
  private static final Logger LOGGER = Logger.getLogger(CartagoHttpClient.class.getCanonicalName());
  
  public Workspace joinRemoteWorkspace(String workspaceIRI, String agentName, ICartagoContext agentCallback) {
    
    String callbackIRI = generateCallback(workspaceIRI, agentName);
    
    String requestIRI = workspaceIRI + "/agents/";
    
    try {
      HttpClient client = HttpClientBuilder.create().build();
      
      HttpPost request = new HttpPost(requestIRI);
      request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
      request.setEntity(
            new StringEntity(new JsonObject()
                                    .put("agentName", agentName)
                                    .put("agentCallbackIRI", callbackIRI)
                .toString()
              )
          );
      
      HttpResponse response = client.execute(request);
      
      int statusCode = response.getStatusLine().getStatusCode();
      
      if (statusCode == HttpStatus.SC_OK) {
        String payload = EntityUtils.toString(response.getEntity());
        
        AgentCallbackRegistry.getInstance().addCallbackIRI(callbackIRI, agentCallback);
        
        LOGGER.info("Join remote workspace successful, workspace representation:\n" + payload);
        
        // TODO: do something with the workspace representation
        return new Workspace();
      }
      
      LOGGER.severe("Join remote workspace failed with status code: " + statusCode);
      
      return null;
    }
    catch (ClientProtocolException e) {
      LOGGER.severe(e.getMessage());
    }
    catch (IOException e) {
      LOGGER.severe(e.getMessage());
    }
    
    return null;
  }
  
  private String generateCallback(String workspaceIRI, String agentName) {
    return "http://localhost:8081/notifications" + 
            workspaceIRI.substring(workspaceIRI.lastIndexOf("/")) + "/" + agentName;
  }
  
}
