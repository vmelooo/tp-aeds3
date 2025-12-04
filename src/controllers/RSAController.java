package controllers;

import java.math.BigInteger;
import java.io.*;

import dao.RSAOperations;
import models.RSAKeyPair;
import models.RSAPrivateKey;
import models.RSAPublicKey;

public class RSAController {
  private RSAKeyPair keyPair;
  private static final String KEY_FILE = "data/rsa_keys.db";

  /**
   * Initialize keys: Load from file or generate new ones if they don't exist.
   */
  public void initialize() {
    File f = new File(KEY_FILE);
    if (f.exists()) {
      loadKeys();
    } else {
      System.out.println("Gerando novas chaves RSA (pode demorar um pouco)...");
      generateKeys(1024);
      saveKeys();
    }
  }

  private void saveKeys() {
    try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(KEY_FILE))) {
      // Save Public Key (e, n)
      RSAPublicKey pub = keyPair.getPublicKey();
      writeBigInteger(dos, pub.getE());
      writeBigInteger(dos, pub.getN());

      // Save Private Key (d, n)
      RSAPrivateKey priv = keyPair.getPrivateKey();
      writeBigInteger(dos, priv.getD());
      writeBigInteger(dos, priv.getN());

    } catch (IOException e) {
      System.err.println("Erro ao salvar chaves RSA: " + e.getMessage());
    }
  }

  private void loadKeys() {
    try (DataInputStream dis = new DataInputStream(new FileInputStream(KEY_FILE))) {
      // Load Public Key
      BigInteger e = readBigInteger(dis);
      BigInteger nPub = readBigInteger(dis);
      RSAPublicKey pub = new RSAPublicKey(e, nPub);

      // Load Private Key
      BigInteger d = readBigInteger(dis);
      BigInteger nPriv = readBigInteger(dis);
      RSAPrivateKey priv = new RSAPrivateKey(d, nPriv);

      this.keyPair = new RSAKeyPair(pub, priv);

    } catch (IOException e) {
      System.err.println("Erro ao carregar chaves RSA: " + e.getMessage());
    }
  }

  // Helper to write BigInteger
  private void writeBigInteger(DataOutputStream dos, BigInteger val) throws IOException {
    byte[] bytes = val.toByteArray();
    dos.writeInt(bytes.length);
    dos.write(bytes);
  }

  // Helper to read BigInteger
  private BigInteger readBigInteger(DataInputStream dis) throws IOException {
    int len = dis.readInt();
    byte[] bytes = new byte[len];
    dis.readFully(bytes);
    return new BigInteger(bytes);
  }

  /**
   * Generate a new RSA key pair
   */
  public void generateKeys(int keySize) {
    if (keySize < 512) {
      throw new IllegalArgumentException("Key size must be at least 512 bits");
    }
    this.keyPair = RSAOperations.generateKeyPair(keySize);
  }

  // public BigInteger encryptPassword(String password) {
  //   if (keyPair == null) {
  //     throw new IllegalStateException("Keys not generated. Call generateKeys() first.");
  //   }
  //   return RSAOperations.encrypt(password, keyPair.getPublicKey());
  // }

  // public String decryptPassword(BigInteger ciphertext) {
  //   if (keyPair == null) {
  //     throw new IllegalStateException("Keys not generated. Call generateKeys() first.");
  //   }
  //   return RSAOperations.decrypt(ciphertext, keyPair.getPrivateKey());
  // }

  /**
   * Encrypt a password using the public key
   * Returns a Base64 string for easy storage in existing String fields
   */
  public String encryptPassword(String password) {
    if (keyPair == null)
      initialize();
    BigInteger encrypted = RSAOperations.encrypt(password, keyPair.getPublicKey());
    // Convert BigInteger to Base64 String for storage
    return java.util.Base64.getEncoder().encodeToString(encrypted.toByteArray());
  }

  /**
   * Decrypt a ciphertext string (Base64) using the private key
   */
  public String decryptPassword(String ciphertextBase64) {
    if (keyPair == null)
      initialize();
    try {
      byte[] bytes = java.util.Base64.getDecoder().decode(ciphertextBase64);
      BigInteger encrypted = new BigInteger(bytes);
      return RSAOperations.decrypt(encrypted, keyPair.getPrivateKey());
    } catch (IllegalArgumentException e) {
      return "Erro: Senha inválida ou corrompida";
    }
  }

  /**
   * Encrypt with a specific public key (e.g., from another user)
   */
  public BigInteger encryptWithPublicKey(String password, RSAPublicKey publicKey) {
    return RSAOperations.encrypt(password, publicKey);
  }

  /**
   * Decrypt with a specific private key
   */
  public String decryptWithPrivateKey(BigInteger ciphertext, RSAPrivateKey privateKey) {
    return RSAOperations.decrypt(ciphertext, privateKey);
  }

  /**
   * Get the current public key
   */
  public RSAPublicKey getPublicKey() {
    if (keyPair == null) {
      throw new IllegalStateException("Keys not generated. Call generateKeys() first.");
    }
    return keyPair.getPublicKey();
  }

  /**
   * Get the current private key
   */
  public RSAPrivateKey getPrivateKey() {
    if (keyPair == null) {
      throw new IllegalStateException("Keys not generated. Call generateKeys() first.");
    }
    return keyPair.getPrivateKey();
  }

  /**
   * Export public key as string for sharing
   */
  public String exportPublicKey() {
    RSAPublicKey pubKey = getPublicKey();
    return pubKey.getE() + ":" + pubKey.getN();
  }

  /**
   * Export private key as string for persistence (KEEP SECURE!)
   */
  public String exportPrivateKey() {
    RSAPrivateKey privKey = getPrivateKey();
    return privKey.getD() + ":" + privKey.getN();
  }

  /**
   * Import a public key from string
   */
  public static RSAPublicKey importPublicKey(String keyString) {
    String[] parts = keyString.split(":");
    BigInteger e = new BigInteger(parts[0]);
    BigInteger n = new BigInteger(parts[1]);
    return new RSAPublicKey(e, n);
  }

  /**
   * Import a private key from string
   */
  public static RSAPrivateKey importPrivateKey(String keyString) {
    String[] parts = keyString.split(":");
    BigInteger d = new BigInteger(parts[0]);
    BigInteger n = new BigInteger(parts[1]);
    return new RSAPrivateKey(d, n);
  }
}
