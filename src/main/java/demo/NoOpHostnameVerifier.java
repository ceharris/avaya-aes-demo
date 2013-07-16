package demo;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class NoOpHostnameVerifier implements HostnameVerifier {

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean verify(String hostname, SSLSession session) {
    return true;
  }

}
