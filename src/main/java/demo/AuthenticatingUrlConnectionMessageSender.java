package demo;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.apache.commons.codec.binary.Base64;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;

public class AuthenticatingUrlConnectionMessageSender
    extends HttpsUrlConnectionMessageSender {

  private String username;
  private String password;
  private String authorization;

  /**
   * Gets the {@code username} property.
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the {@code username} property.
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Gets the {@code password} property.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the {@code password} property.
   */
  public void setPassword(String password) {
    this.password = password;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    StringBuilder sb = new StringBuilder();
    sb.append("Basic ");
    sb.append(credential());
    authorization = sb.toString();
    super.afterPropertiesSet();
  }

  private String credential() throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append(getUsername());
    sb.append(':');
    sb.append(getPassword());
    Base64 codec = new Base64();
    return codec.encodeAsString(sb.toString().getBytes("US-ASCII"));
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected void prepareConnection(HttpURLConnection connection)
      throws IOException {
    super.prepareConnection(connection);
    connection.setRequestProperty("Authorization", authorization);
  }

}
