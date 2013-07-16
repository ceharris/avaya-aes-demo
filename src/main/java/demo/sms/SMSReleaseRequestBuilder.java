package demo.sms;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;



public class SMSReleaseRequestBuilder implements SMSRequestBuilder {
  
  public Document createRequest() {
    Element element = new Element(Constants.RELEASE_REQUEST_ELEMENT, 
        Namespace.getNamespace(Constants.SMS_NS)); 
    Document document = new Document(element);
    return document;
  }

}
