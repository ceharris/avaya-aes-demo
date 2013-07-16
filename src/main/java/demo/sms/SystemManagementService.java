package demo.sms;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.jdom2.Document;
import org.jdom2.transform.JDOMResult;
import org.jdom2.transform.JDOMSource;
import org.springframework.util.Assert;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessage;


public class SystemManagementService {

  private final SMSSessionHolder sessionHolder = new SMSSessionHolder();
  
  private URL stylesheetLocation;
  private WebServiceTemplate template;

  private Transformer requestTransformer;
  private Transformer responseTransformer;
  
  /**
   * Gets the {@code stylesheetLocation} property.
   */
  public URL getStylesheetLocation() {
    return stylesheetLocation;
  }

  /**
   * Sets the {@code stylesheetLocation} property.
   */
  public void setStylesheetLocation(URL templatesLocation) {
    this.stylesheetLocation = templatesLocation;
  }

  /**
   * Gets the {@code template} property.
   */
  public WebServiceTemplate getTemplate() {
    return template;
  }

  /**
   * Sets the {@code template} property.
   */
  public void setTemplate(WebServiceTemplate template) {
    this.template = template;
  }

  public void init() throws Exception {
    TransformerFactory tf = TransformerFactory.newInstance();
    requestTransformer = tf.newTransformer();
    responseTransformer = loadTemplates(tf).newTransformer();
  }
  
  private Templates loadTemplates(TransformerFactory tf) throws Exception {
    Assert.notNull(getStylesheetLocation(), "stylesheetLocation is required");
    InputStream inputStream = getStylesheetLocation().openStream();
    try {
      return tf.newTemplates(new StreamSource(inputStream));
    }
    finally {
      try {
        inputStream.close();
      }
      catch (IOException ex) {
        ex.printStackTrace(System.err);
      }
    }
  }

  public Document sendRequest(Document request) {
    RequestCallback requestCallback = new RequestCallback(request,requestTransformer);
    ResponseCallback responseCallback = new ResponseCallback(responseTransformer);
    template.sendAndReceive(requestCallback, responseCallback);
    return responseCallback.getResponse();    
  }
  
  private class RequestCallback implements WebServiceMessageCallback {
    
    private final Document request;
    private final Transformer transformer;
    
    public RequestCallback(Document request, Transformer transformer) {
      this.request = request;
      this.transformer = transformer;
    }

    @Override
    public void doWithMessage(WebServiceMessage message) throws IOException,
        TransformerException {
      sessionHolder.addSessionIdToSoapMessage((SoapMessage) message);
      transformer.transform(new JDOMSource(request), 
          message.getPayloadResult());
      
    }
    
  }
  
  private class ResponseCallback implements WebServiceMessageCallback {

    private final JDOMResult result = new JDOMResult();
    private final Transformer transformer;

    /**
     * Constructs a new instance.
     * @param transformer
     */
    public ResponseCallback(Transformer transformer) {
      this.transformer = transformer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doWithMessage(WebServiceMessage message) throws IOException,
        TransformerException {
      sessionHolder.extractSessionIdFromSoapMessage((SoapMessage) message);
      transformer.transform(message.getPayloadSource(), result);
    }

    public Document getResponse() {
      return result.getDocument();
    }
    
  }
  
}
