package demo.sms;

import javax.xml.namespace.QName;

interface Constants {

  public static final String SESSION_ID_NS = "http://xml.avaya.com/ws/session";
  public static final String SESSION_ID_ELEMENT = "sessionID";
  public static final QName SESSION_ID_QNAME = 
      new QName(SESSION_ID_NS, SESSION_ID_ELEMENT);
  public static final String SMS_NS = "http://xml.avaya.com/ws/SystemManagementService/2008/07/01";
  public static final String SUBMIT_REQUEST_ELEMENT = "submitRequest";
  public static final String RELEASE_REQUEST_ELEMENT = "release";
  public static final String MODEL_FIELDS_ELEMENT = "modelFields";
  public static final String OPERATION_ELEMENT = "operation";
  public static final String OBJECT_NAME_ELEMENT = "objectname";
  public static final String QUALIFIER_ELEMENT = "qualifier";
  public static final String ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";

}
