package demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.springframework.ws.client.core.WebServiceTemplate;

import demo.sms.SMSReleaseRequestBuilder;
import demo.sms.SMSSubmitRequestBuilder;
import demo.sms.SystemManagementService;
import edu.vt.cns.kestrel.common.ssl.KeyStoreConfiguration;
import edu.vt.cns.kestrel.common.ssl.SSLContextFactory;

public class Main {

  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {
    Properties properties = loadProperties(
        Main.class.getClassLoader().getResource("sms.properties"));
    KeyStoreConfiguration trustStoreConfig = new KeyStoreConfiguration(
        Main.class.getClassLoader().getResource("truststore.jks"), "changeit");
    
    SSLContextFactory contextFactory = new SSLContextFactory();
    contextFactory.setTrustStore(trustStoreConfig);
    SSLContext context = contextFactory.createContext();

    AuthenticatingUrlConnectionMessageSender messageSender =
        new AuthenticatingUrlConnectionMessageSender();
    messageSender.setSslSocketFactory(context.getSocketFactory());
    messageSender.setHostnameVerifier(new NoOpHostnameVerifier());
    messageSender.setUsername(properties.getProperty("username"));
    messageSender.setPassword(properties.getProperty("password"));
    messageSender.afterPropertiesSet();

    String script = properties.getProperty("nextQualifierScript");
    URL scriptResource = Main.class.getClassLoader().getResource(script);
    Invocable invocable = scriptResource != null ?
        loadScript(scriptResource) : loadScript(script);
    
    WebServiceTemplate wsTemplate = new WebServiceTemplate();
    wsTemplate.setMessageSender(messageSender);
    wsTemplate.setDefaultUri(properties.getProperty("webServiceAuthority")
        + properties.getProperty("webServicePath"));
    
    SMSSubmitRequestBuilder submitRequestBuilder = new SMSSubmitRequestBuilder();
    
    SMSReleaseRequestBuilder releaseRequestBuilder = new SMSReleaseRequestBuilder();

    SystemManagementService service = new SystemManagementService();
    service.setTemplate(wsTemplate);
    service.setStylesheetLocation(
        Main.class.getClassLoader().getResource(
            properties.getProperty("stylesheet")));
    service.init();
    
    String qualifier = properties.getProperty("qualifier");
    String keyAttribute = properties.getProperty("keyAttribute");

    submitRequestBuilder.setModelName(properties.getProperty("modelName"));
    submitRequestBuilder.setModelFields(
        Arrays.asList(properties.getProperty("modelFields").split("\\s*,\\s*")));
    submitRequestBuilder.setOperation(properties.getProperty("operation"));
    submitRequestBuilder.setQualifier(qualifier);
    System.out.println("qualifier " + qualifier);
    
    XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
    Document response = service.sendRequest(submitRequestBuilder.createRequest());
    out.output(response, System.out);
    String nextQualifier = nextQualifier(response, keyAttribute, qualifier,
        invocable);
    while (nextQualifier != null) {
      System.out.println("qualifier " + nextQualifier);
      submitRequestBuilder.setQualifier(nextQualifier);
      response = service.sendRequest(submitRequestBuilder.createRequest());
      out.output(response, System.out);
      nextQualifier = nextQualifier(response, keyAttribute, qualifier,
          invocable);
    }

    service.sendRequest(releaseRequestBuilder.createRequest());
  }
  
  private static String nextQualifier(Document document, String keyAttribute,
      String qualifier, Invocable invocable)  {
    List<Element> registrations = document.getRootElement().getChildren();

    if (registrations.isEmpty()) return null;
    int i = registrations.size() - 1;
    String key = registrations.get(i).getAttributeValue(keyAttribute);
    return nextQualifier(key, qualifier, invocable);
  }

  private static String nextQualifier(String key, String qualifier,
      Invocable invocable) {
    try {
      return (String) invocable.invokeFunction("nextQualifier", key, qualifier);
    }
    catch (ScriptException ex) {
      throw new RuntimeException(ex);
    }
    catch (NoSuchMethodException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  private static Properties loadProperties(URL url) throws IOException {
    InputStream inputStream = url.openStream();
    try {
      Properties properties = new Properties();
      properties.load(inputStream);
      return properties;
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
  
  private static Invocable loadScript(URL url) 
      throws ScriptException, IOException {
    Reader reader = new InputStreamReader(url.openStream());
    try {
      ScriptEngine engine = newScriptEngine();
      engine.eval(reader);
      return (Invocable) engine;
    }
    finally {
      try {
        reader.close();
      }
      catch (IOException ex) {
        ex.printStackTrace(System.err);
      }
    }
  }

  private static Invocable loadScript(String text) 
      throws ScriptException {
     ScriptEngine engine = newScriptEngine();
     engine.eval(text);
     return (Invocable) engine;
  }

  private static ScriptEngine newScriptEngine() {
    ScriptEngineManager factory = new ScriptEngineManager();
    ScriptEngine engine = factory.getEngineByName("JavaScript");
    return engine;
  }
  
}
