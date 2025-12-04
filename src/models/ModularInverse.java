package models;

import java.math.BigInteger;

public class ModularInverse {
  private final BigInteger inverse;
  private final boolean inversible;

  public ModularInverse(BigInteger inverse, boolean inversible) {
    this.inverse = inverse;
    this.inversible = inversible;
  }

  public BigInteger getInverse() {
    return inverse;
  }

  public boolean isInversible() {
    return inversible;
  }
}
