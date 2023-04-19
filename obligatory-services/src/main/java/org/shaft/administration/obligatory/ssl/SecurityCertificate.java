package org.shaft.administration.obligatory.ssl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;

public class SecurityCertificate {
  private String url = null;

  private int port = 0;

  private String downloadPath = null;

  private char[] passphrase = null;

  private String jdkpath = null;

  private String certificatePath = null;

  private String commonName = null;

  private String orgnistionUnit = null;

  private String orgnisation = null;

  private String province = null;

  private String state = null;

  private String country = null;

  private String keystorepath = null;

  private int validityInDays = 0;

  private String CERTIFICATE_ALIAS = null;

  private String CERTIFICATE_ALGORITHM = "RSA";

  private String CERTIFICATE_DN = null;

  private int CERTIFICATE_BITS = 1024;

  ObjectMapper objectMapper = new ObjectMapper();

  public SecurityCertificate(String commonName, String orgnistionUnit, String orgnisation, String province, String state, String country, String keystorepath, int validityInDays, String keystorePassword) {
    this.commonName = commonName;
    this.orgnistionUnit = orgnistionUnit;
    this.orgnisation = orgnisation;
    this.province = province;
    this.state = state;
    this.country = country;
    this.keystorepath = keystorepath;
    this.validityInDays = validityInDays;
    if (keystorePassword != null && keystorePassword.length() > 0)
      this.passphrase = keystorePassword.toCharArray();
  }

  public SecurityCertificate(String jdkPath, String certificatePath, String keystorePassword) {
    this.jdkpath = jdkPath;
    this.certificatePath = certificatePath;
    if (keystorePassword != null && keystorePassword.length() > 0)
      this.passphrase = keystorePassword.toCharArray();
  }

  public SecurityCertificate(String url, int port, String downloadPath) {
    this.downloadPath = downloadPath;
    this.url = url;
    this.port = port;
  }

  public String[] DownloadCertificate() throws Exception {
    String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";
    String END_CERT = "-----END CERTIFICATE-----";
    String LINE_SEPARATOR = System.getProperty("line.separator");
    Base64.Encoder encoder = Base64.getMimeEncoder(64, LINE_SEPARATOR.getBytes());
    String Certificatename = null;
    try {
      if (this.port == 0)
        this.port = 443;
      KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
      ks.load(null, null);
      SSLContext context = SSLContext.getInstance("TLS");
      TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(ks);
      X509TrustManager defaultTrustManager = (X509TrustManager)tmf.getTrustManagers()[0];
      SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
      context.init(null, new TrustManager[] { tm }, null);
      SSLSocketFactory factory = context.getSocketFactory();
      SSLSocket socket = (SSLSocket)factory.createSocket(this.url, this.port);
      socket.setSoTimeout(10000);
      try {
        socket.startHandshake();
        socket.close();
      } catch (SSLException sSLException) {}
      X509Certificate[] chain = tm.getChain();
      if (chain == null)
        return null;
      String[] certArray = new String[chain.length];
      for (int i = 0; i < chain.length; i++) {
        X509Certificate cert = chain[i];
        byte[] rawCrtText = cert.getEncoded();
        String encodedCertText = new String(encoder.encode(rawCrtText));
        String prettified_cert = "-----BEGIN CERTIFICATE-----" + LINE_SEPARATOR + encodedCertText + LINE_SEPARATOR + "-----END CERTIFICATE-----";
        Certificatename = this.url + "-" + (1 + i) + ".cer";
        certArray[i] = SaveSSLCertificateLocally(this.downloadPath, Certificatename, prettified_cert);
      }
      return certArray;
    } catch (UnknownHostException u) {
      throw new Exception(" Invalid HostName or Port provied plese first verify the hostname or portnumber ");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new Exception(" Invalid  Path Provided" + e.getMessage());
    } catch (IOException e) {
      throw new Exception(" Invalid  Path Provided");
    } catch (Exception e) {
      throw new Exception(e.getCause());
    }
  }

  public String SaveSSLCertificateLocally(String path, String name, String data) throws IOException, FileNotFoundException {
    String StripSpancesInName = name.replaceAll("\"", "").replaceAll("\\s", "");
    FileWriter fileWriter = new FileWriter(path + StripSpancesInName);
    PrintWriter printWriter = new PrintWriter(fileWriter);
    printWriter.print(data);
    printWriter.close();
    return path + StripSpancesInName;
  }

  public String getJavaKeystorepath(String jdkPath) {
    return jdkPath + "/jre/lib/security/cacerts";
  }

  public String InstallSSLCertificateLocally() throws Exception {
    try {
      String certfile = this.certificatePath;
      String keystorename = getJavaKeystorepath(this.jdkpath);
      char[] password = this.passphrase;
      String alias = "";
      FileInputStream is = new FileInputStream(keystorename);
      KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
      keystore.load(is, password);
      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      InputStream certstream = fullStream(certfile);
      Certificate certs = cf.generateCertificate(certstream);
      X509Certificate cert = (X509Certificate)certs;
      Enumeration<String> enumeration = keystore.aliases();
      Boolean flag = Boolean.TRUE;
      boolean match = Boolean.FALSE;
      while (flag) {
        alias = (cert.getSubjectDN() + "" + Math.random()).replaceAll("\"", "").replaceAll("\\s+", "");
        while (enumeration.hasMoreElements()) {
          String storedAlias = enumeration.nextElement();
          if (alias.equalsIgnoreCase(storedAlias)) {
            match = Boolean.TRUE;
            break;
          }
        }
        if (match) {
          enumeration = keystore.aliases();
          continue;
        }
        flag = Boolean.FALSE;
      }
      keystore.setCertificateEntry(alias, cert);
      File keystoreFile = new File(keystorename);
      FileOutputStream out = new FileOutputStream(keystoreFile);
      keystore.store(out, password);
      out.close();
      return alias;
    } catch (FileNotFoundException u) {
      throw new Exception(" certificate path does not found / jdk is not accessable  ");
    } catch (KeyStoreException e) {
      throw new Exception(" keystore is not valid");
    } catch (IOException e) {
      throw new Exception(" Invalid  Path Provided");
    } catch (CertificateException e) {
      throw new Exception(" invalid certificate is providedd");
    } catch (Exception e) {
      throw new Exception(e.getCause());
    }
  }

  public InputStream fullStream(String fname) throws IOException {
    FileInputStream fis = new FileInputStream(fname);
    DataInputStream dis = new DataInputStream(fis);
    byte[] bytes = new byte[dis.available()];
    dis.readFully(bytes);
    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
    return bais;
  }

  public String GenerateSelfSignedCertificate() throws Exception {
    if (this.validityInDays <= 0)
      throw new Exception("Please enter 1 more days of expiry");
    try {
      Security.addProvider((Provider)new BouncyCastleProvider());
      X509Certificate cert = null;
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(this.CERTIFICATE_ALGORITHM);
      keyPairGenerator.initialize(this.CERTIFICATE_BITS, new SecureRandom());
      KeyPair keyPair = keyPairGenerator.generateKeyPair();
      X509V3CertificateGenerator v3CertGen = new X509V3CertificateGenerator();
      v3CertGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
      this.CERTIFICATE_DN = generateDN();
      Date expiryTime = new Date(System.currentTimeMillis() + 86400000L * this.validityInDays);
      v3CertGen.setIssuerDN((X509Name)new X509Principal(this.CERTIFICATE_DN));
      v3CertGen.setNotBefore(new Date(System.currentTimeMillis() - 86400000L));
      v3CertGen.setNotAfter(expiryTime);
      v3CertGen.setSubjectDN((X509Name)new X509Principal(this.CERTIFICATE_DN));
      v3CertGen.setPublicKey(keyPair.getPublic());
      v3CertGen.setSignatureAlgorithm("SHA256WithRSAEncryption");
      cert = v3CertGen.generateX509Certificate(keyPair.getPrivate());
      saveCert(cert, keyPair.getPrivate());
      ObjectNode sucessresponse = this.objectMapper.createObjectNode();
      sucessresponse.put("IssuserName", this.CERTIFICATE_DN);
      sucessresponse.put("ExpiryTime", expiryTime.toString());
      sucessresponse.put("KeystorePath", this.keystorepath);
      sucessresponse.put("KeystoreName", this.commonName + ".jks");
      return sucessresponse.toString();
    } catch (FileNotFoundException fe) {
      throw new Exception("Path to store 'KEYSTORE' is not valid ");
    } catch (Exception e) {
      throw new Exception(" if  Certificate path does not found / jdk is not accessable (permission  ");
    }
  }

  public String generateDN() {
    StringBuilder dn = new StringBuilder();
    if (this.commonName != null && !this.commonName.equals(""))
      dn.append("CN=").append(this.commonName);
    if (this.orgnistionUnit != null && !this.orgnistionUnit.equals(""))
      dn.append(", OU=").append(this.orgnistionUnit);
    if (this.orgnisation != null && !this.orgnisation.equals(""))
      dn.append(", O=").append(this.orgnisation);
    if (this.province != null && !this.province.equals(""))
      dn.append(",L=").append(this.province);
    if (this.state != null && !this.state.equals(""))
      dn.append(", ST=").append(this.state);
    if (this.country != null && !this.country.equals(""))
      dn.append(", C=").append(this.country);
    return new String(dn);
  }

  public void saveCert(X509Certificate cert, PrivateKey key) throws Exception {
    KeyStore keyStore = KeyStore.getInstance("JKS");
    keyStore.load(null, null);
    this.CERTIFICATE_ALIAS = this.orgnisation + Math.random();
    keyStore.setKeyEntry(this.CERTIFICATE_ALIAS, key, this.passphrase, new Certificate[] { cert });
    File file = new File(this.keystorepath, (this.commonName + ".jks").replaceAll("\\s+", ""));
    keyStore.store(Files.newOutputStream(file.toPath()), this.passphrase);
  }
}
