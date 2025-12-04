package models;

import java.math.BigInteger;

public class RSAPublicKey {
  private final BigInteger pubExp; // public exponent
  private final BigInteger mod; // modulus

  public RSAPublicKey(BigInteger e, BigInteger n) {
    this.pubExp = e;
    this.mod = n;
  }

  public BigInteger getPubExp() {
    return pubExp;
  }

  public BigInteger getMod() {
    return mod;
  }

  @Override
  public String toString() {
    return "PublicKey{e=" + pubExp + ", n=" + mod + "}";
  }
}
