package cartago.infrastructure.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

import com.google.gson.Gson;

import cartago.CartagoEvent;
import cartago.ICartagoListener;
import io.vertx.core.json.JsonObject;

public class AgentIRIListener implements ICartagoListener {

  private final static Logger LOGGER = Logger.getLogger(AgentIRIListener.class.getCanonicalName()); 
  
  private String agentCallbackIRI;
  
  public AgentIRIListener(String agentCallbackIRI) {
    this.agentCallbackIRI = agentCallbackIRI;
  }
  
  @Override
  public boolean notifyCartagoEvent(CartagoEvent ev) {
    LOGGER.info("Received notification for event: " + ev);
    
    try {
      HttpClient client = HttpClientBuilder.create().build();
      HttpPost request = new HttpPost(agentCallbackIRI);
      
      String event = new Gson().toJson(ev);
      
      String notification = new JsonObject()
            .put("className", ev.getClass().getCanonicalName())
            .put("event", event).encode();
      
      LOGGER.info("Sending notification: " + notification);
      
      request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
      request.setEntity(new StringEntity(notification));
      
      HttpResponse response = client.execute(request);
      
      if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        LOGGER.info("Notification sent successfully to: " + agentCallbackIRI);
      } else {
        LOGGER.info("Request failed with status code: " + response.getStatusLine().getStatusCode());
      }
      
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return true;
  }

}
