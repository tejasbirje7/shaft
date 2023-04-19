package org.shaft.administration.obligatory.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

public class SavingTrustManager implements X509TrustManager {
  private final X509TrustManager tm;

  private X509Certificate[] chain;

  SavingTrustManager(X509TrustManager tm) {
    this.tm = tm;
  }

  public X509Certificate[] getAcceptedIssuers() {
    throw new UnsupportedOperationException();
  }

  public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    throw new UnsupportedOperationException();
  }

  public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    this.chain = chain;
    this.tm.checkServerTrusted(chain, authType);
  }

  public X509Certificate[] getChain() {
    return this.chain;
  }
}
