package demo.sms;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;


public class SMSSubmitRequestBuilder implements SMSRequestBuilder {

  private String modelName;
  private String operation;
  private String objectName;
  private String qualifier;
  private List<String> modelFields = new ArrayList<String>();

  /**
   * Gets the {@code modelName} property.
   */
  public String getModelName() {
    return modelName;
  }

  /**
   * Sets the {@code modelName} property.
   */
  public void setModelName(String modelName) {
    this.modelName = modelName;
  }

  /**
   * Gets the {@code operation} property.
   */
  public String getOperation() {
    return operation;
  }

  /**
   * Sets the {@code operation} property.
   */
  public void setOperation(String operation) {
    this.operation = operation;
  }

  /**
   * Gets the {@code objectName} property.
   */
  public String getObjectName() {
    return objectName;
  }

  /**
   * Sets the {@code objectName} property.
   */
  public void setObjectName(String objectName) {
    this.objectName = objectName;
  }

  /**
   * Gets the {@code qualifier} property.
   */
  public String getQualifier() {
    return qualifier;
  }

  /**
   * Sets the {@code qualifier} property.
   */
  public void setQualifier(String qualifier) {
    this.qualifier = qualifier;
  }

  /**
   * Gets the {@code modelFields} property.
   */
  public List<String> getModelFields() {
    return modelFields;
  }

  /**
   * Sets the {@code modelFields} property.
   */
  public void setModelFields(List<String> modelFields) {
    this.modelFields = modelFields;
  }

  public void addModelField(String modelField) {
    modelFields.add(modelField);
  }
  
  public void removeModelField(String modelField) {
    modelFields.remove(modelField);
  }
  
  public Document createRequest() {
    Element request = new Element(Constants.SUBMIT_REQUEST_ELEMENT,
        Namespace.getNamespace(Constants.SMS_NS));
    request.addContent(createModelFields());
    request.addContent(createOperation());
    request.addContent(createObjectName());
    request.addContent(createQualifier());
    return new Document(request);
  }
  
  private Element createModelFields() {
    Element element = new Element(Constants.MODEL_FIELDS_ELEMENT);
    element.addContent(createModel());
    return element;
  }
  
  private Element createModel() {
    Element element = new Element(modelName);
    for (String modelField : modelFields) {
      element.addContent(new Element(modelField));
    }
    return element;
  }
  
  private Element createOperation() {
    Element element = new Element(Constants.OPERATION_ELEMENT);
    element.setText(operation);
    return element;    
  }

  private Element createObjectName() {
    Element element = new Element(Constants.OBJECT_NAME_ELEMENT);
    if (objectName != null) {
      element.setText(objectName);
    }
    return element;    
  }
  
  private Element createQualifier() {
    Element element = new Element(Constants.QUALIFIER_ELEMENT);
    if (qualifier != null) {
      element.setText(qualifier);
    }
    return element;    
  }

}
