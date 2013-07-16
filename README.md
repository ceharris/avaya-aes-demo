avaya-aes-demo
==============

Demo of using the Avaya AES to control the System Management Service (SMS).

The central class in the demo is the `demo.SystemManagementService`.  This class acts as a facade over the SOAP-based web services API provided by AES, implemented using Spring's `WebServiceTemplate`.  The facade allows you to send an XML document representing an SMS request and to receive an XML document that represents a transformation of the SMS reply. You configure the `SystemManagementService` with a URL for a XSL stylesheet that will be used to transform the reply, and with a reference to the `WebServiceTemplate` to use for communicating with the underlying web service.

You can create the request document using an instance of `SMSRequestBuilder`.  Two implementations of this interface are provided, corresponding to the two types of requests understood by the AES SMS service.  The `SMSSubmitRequestBuilder` is a builder that produces the  document for an SMS Submit request.  The submit request is used to specify and qualify an SMS command.  The `SMSReleaseRequestBuilder` is a builder that produces the document for an SMS Release request.  The release request is used to tear down the resources that were allocated for a prior submit request.  At a low level, the SMS Submit request opens a terminal-like connection to the system manager, sends commands and parses command output to produce the response.  The release request closes the connection to the system manager.  The `SMSSessionHolder` is used by the `SystemManagementService` to keep track of the session identifier needed in the release request.

The `Main` class demonstrates the use of these objects to query the Registered IP Stations model from SMS.

* `src/main/resources/sms.properties` contains the properties needed to connect to the SMS service, as well as the names of the desired model, model fields, operation, and qualifier.  It also contains a relative URL for an XSL stylesheet (`src/main/resources/registrations.xsl`) that will be used to transform the raw SMS response document into a more palatable form.
* In `main` the `sms.properties` file is loaded from the classpath.  A truststore containing X.509 trusted root certificates is also loaded as a classpath resource.
* An `SSLContext` is created that uses the custom truststore.  This context is then used to construct a socket factory that is used by a custom Spring `MessageSender` implementation (`AuthenticatingUrlConnectionMessageSender`).  This message sender is configured with the credentials needed to access the AES service.
* A small JavaScript function is loaded next.  This function is used to generate an appropriate qualifier for subsequent requests for the same model, and is discussed further, below.
* The WebServiceTemplate is constructed and configured with the URL for the SMS web service endpoint and the message sender implementation.
* Builders are created for producing the SMS Submit and Release requests.
* The `SystemManagementService` object is created and configured.
* The submit request builder is configured with the model name, model fields, operation, and initial qualifier.
* The initial request is submitted and the response is obtained.  The transformed XML for the response is output to the console
* The next qualifier is generated and injected into the submit request builder.
* Subsequent requests are sent and the responses displayed until the end of the model data is reached.
* The release request is sent and its response is ignored.

In a model with many elements, it is necessary to use a qualifier to specify the starting offset within the model and the number of elements desired.  The first qualifier specifies only the number of elements desired; subsequent requests specify a model field and a value to match for the first element.  For the Registered IP Stations data, the `ext` (extension) model field is used as the qualifier in each request after the initial request.  The value specified for the `ext` qualifier is the last extension number returned in the previous response, plus one; i.e. if the last response ended with extension 14319, the qualifier used in the next request would be `ext 14320`.

The demo illustrates the use of the Java Scripting API to evaluate a JavaScript function specified in the `sms.properties` file in order to generate the next qualifier.  This allows the Java code to be completely independent of the SMS model that is being requested -- the `nextQualifier` function is effectively just additional configuration.  By simply modifying the `sms.properties`, any combination of model, model fields, operation, and qualifier can be incrementally queried by the example.

