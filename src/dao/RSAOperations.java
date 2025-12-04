package dao;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import models.ModularInverse;
import models.RSAKeyPair;
import models.RSAPrivateKey;
import models.RSAPublicKey;

public class RSAOperations {
  private static final SecureRandom random = new SecureRandom();
  private static final BigInteger PUBLIC_EXPONENT = BigInteger.valueOf(65537);

  /**
   * Extended Euclidean Algorithm - finds modular inverse
   */
  public static ModularInverse modularInverse(BigInteger a, BigInteger n) {
    if (a.compareTo(BigInteger.ZERO) < 0 || n.compareTo(BigInteger.ZERO) < 0) {
      return new ModularInverse(BigInteger.ZERO, false);
    }

    // r = s · a + t · n
    BigInteger[] remainder = { n, a };
    BigInteger[] s = { BigInteger.ZERO, BigInteger.ONE };

    while (!remainder[1].equals(BigInteger.ZERO)) {
      BigInteger q = remainder[0].divide(remainder[1]);

      BigInteger temp = remainder[0].subtract(remainder[1].multiply(q));
      remainder[0] = remainder[1];
      remainder[1] = temp;

      temp = s[0].subtract(s[1].multiply(q));
      s[0] = s[1];
      s[1] = temp;
    }

    BigInteger gcd = remainder[0];
    BigInteger val = s[0];

    if (gcd.equals(BigInteger.ONE)) {
      BigInteger inverse = val.mod(n);
      if (inverse.compareTo(BigInteger.ZERO) < 0) {
        inverse = inverse.add(n);
      }
      return new ModularInverse(inverse, true);
    }

    return new ModularInverse(BigInteger.ZERO, false);
  }

  /**
   * Miller-Rabin primality test
   */
  public static boolean isProbablePrime(BigInteger n, int iterations) {
    if (n.compareTo(BigInteger.TWO) < 0)
      return false;
    if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3)))
      return true;
    if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO))
      return false;

    // n-1 = 2^r * d
    BigInteger d = n.subtract(BigInteger.ONE);
    int r = 0;
    while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
      d = d.divide(BigInteger.TWO);
      r++;
    }

    for (int i = 0; i < iterations; i++) {
      BigInteger a = new BigInteger(n.bitLength(), random);
      a = a.mod(n.subtract(BigInteger.valueOf(3))).add(BigInteger.TWO);

      BigInteger x = modPow(a, d, n);

      if (x.equals(BigInteger.ONE) || x.equals(n.subtract(BigInteger.ONE))) {
        continue;
      }

      boolean composite = true;
      for (int j = 0; j < r - 1; j++) {
        x = modPow(x, BigInteger.TWO, n);
        if (x.equals(n.subtract(BigInteger.ONE))) {
          composite = false;
          break;
        }
      }

      if (composite)
        return false;
    }

    return true;
  }

  /**
   * Generate a random prime number of specified bit length
   */
  public static BigInteger generatePrime(int bitLength) {
    BigInteger prime;
    do {
      prime = new BigInteger(bitLength, random);
      if (prime.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
        prime = prime.add(BigInteger.ONE);
      }
    } while (!isProbablePrime(prime, 40));

    return prime;
  }

  /**
   * Modular exponentiation: (base^exp) mod m
   */
  public static BigInteger modPow(BigInteger base, BigInteger exp, BigInteger mod) {
    BigInteger result = BigInteger.ONE;
    base = base.mod(mod);

    while (exp.compareTo(BigInteger.ZERO) > 0) {
      if (exp.testBit(0)) {
        result = result.multiply(base).mod(mod);
      }
      exp = exp.shiftRight(1);
      base = base.multiply(base).mod(mod);
    }

    return result;
  }

  /**
   * Generate RSA key pair
   */
  public static RSAKeyPair generateKeyPair(int keySize) {
    // Generate two distinct primes
    BigInteger p = generatePrime(keySize / 2);
    BigInteger q = generatePrime(keySize / 2);

    while (p.equals(q)) {
      q = generatePrime(keySize / 2);
    }

    // n = p * q
    BigInteger n = p.multiply(q);

    // phi(n) = (p-1)(q-1)
    BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

    // FIX: maybe not use constant one but random?
    BigInteger e = PUBLIC_EXPONENT;

    // private exponent d
    ModularInverse result = modularInverse(e, phi);
    if (!result.isInversible()) {
      throw new RuntimeException("Failed to calculate modular inverse");
    }
    BigInteger d = result.getInverse();

    RSAPublicKey publicKey = new RSAPublicKey(e, n);
    RSAPrivateKey privateKey = new RSAPrivateKey(d, n);

    return new RSAKeyPair(publicKey, privateKey);
  }

  /**
   * Encrypt a message using public key
   */
  public static BigInteger encrypt(String message, RSAPublicKey publicKey) {
    byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
    BigInteger m = new BigInteger(1, bytes);

    if (m.compareTo(publicKey.getMod()) >= 0) {
      throw new IllegalArgumentException("Message too long for key size");
    }

    return modPow(m, publicKey.getPubExp(), publicKey.getMod());
  }

  /**
   * Decrypt a ciphertext using private key
   */
  public static String decrypt(BigInteger ciphertext, RSAPrivateKey privateKey) {
    BigInteger m = modPow(ciphertext, privateKey.getPrivExp(), privateKey.getMod());
    byte[] bytes = m.toByteArray();
    return new String(bytes, StandardCharsets.UTF_8);
  }
}
