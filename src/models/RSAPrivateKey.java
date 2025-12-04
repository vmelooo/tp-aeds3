package models;

import java.math.BigInteger;

public class RSAPrivateKey {
  private final BigInteger privExp; // private exponent
  private final BigInteger mod;

  public RSAPrivateKey(BigInteger d, BigInteger n) {
    this.privExp = d;
    this.mod = n;
  }

  public BigInteger getPrivExp() {
    return privExp;
  }

  public BigInteger getMod() {
    return mod;
  }

  @Override
  public String toString() {
    return "PrivateKey{d=" + privExp + ", n=" + mod + "}";
  }
}
