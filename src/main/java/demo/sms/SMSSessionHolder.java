package demo.sms;

import java.util.Iterator;


import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;


public class SMSSessionHolder {

  private String sessionId;

  /**
   * Gets the {@code sessionId} property.
   */
  public String getSessionId() {
    return sessionId;
  }

  /**
   * Sets the {@code sessionId} property.
   */
  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }
  
  public void addSessionIdToSoapMessage(SoapMessage message) {
    if (sessionId == null) return;
    SoapHeader header = message.getEnvelope().getHeader();
    SoapHeaderElement element = 
        header.addHeaderElement(Constants.SESSION_ID_QNAME);
    element.setText(sessionId);
  }

  public void extractSessionIdFromSoapMessage(SoapMessage message) {
    SoapHeader header = message.getEnvelope().getHeader();
    Iterator<SoapHeaderElement> i = header.examineHeaderElements(Constants.SESSION_ID_QNAME);
    while (i.hasNext()) {
      SoapHeaderElement element = i.next();
      setSessionId(element.getText());
    }
  }
  
}
