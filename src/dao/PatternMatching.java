package dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatternMatching {
  /**
   * Searches for occurrences of 'pattern' in 'text' using KMP
   * 
   * @return List of starting indices where pattern was found.
   */
  public static List<Integer> searchKMP(String txt, String pattern) {
    List<Integer> foundIndices = new ArrayList<>();
    if (pattern.length() == 0)
      return foundIndices;

    int patLen = pattern.length();
    int txtLen = txt.length();

    // Preprocess pattern
    int[] lps = computeLPSArray(pattern);

    int i = 0; // guides txt[]
    int j = 0; // guides pat[]

    while (i < txtLen) {
      if (pattern.charAt(j) == txt.charAt(i)) {
        j++;
        i++;
      }
      if (j == patLen) {
        // found at index (i - j)
        foundIndices.add(i - j);
        j = lps[j - 1];
      } else if (i < txtLen && pattern.charAt(j) != txt.charAt(i)) {
        // mismatch after j matches
        if (j != 0) {
          j = lps[j - 1];
        } else {
          i = i + 1;
        }
      }
    }
    return foundIndices;
  }

  /**
   * Computes LPS array for KMP
   */
  private static int[] computeLPSArray(String pattern) {
    int patLen = pattern.length();
    int[] lps = new int[patLen];

    int len = 0;
    int i = 1;

    lps[0] = 0;

    while (i < patLen) {
      if (pattern.charAt(i) == pattern.charAt(len)) {
        len++;
        lps[i] = len;
        i++;
      } else {
        if (len != 0) {
          len = lps[len - 1];
        } else {
          lps[i] = len;
          i++;
        }
      }
    }
    return lps;
  }

  /**
   * Boyer-Moore with hashmap for unicode
   * 
   * @return List of starting indices where pattern was found
   */
  public static List<Integer> searchBoyerMoore(String txt, String pattern) {
    List<Integer> foundIndices = new ArrayList<>();
    int patLen = pattern.length();
    int txtLen = txt.length();

    if (patLen == 0 || txtLen == 0)
      return foundIndices;

    Map<Character, Integer> badChar = badCharHeuristic(pattern);
    int[] goodSuffix = goodSuffixHeuristic(pattern);

    int shift = 0;

    while (shift <= (txtLen - patLen)) {
      int j = patLen - 1;

      // matches pattern from right to left
      while (j >= 0 && pattern.charAt(j) == txt.charAt(shift + j)) {
        j--;
      }

      if (j < 0) {
        foundIndices.add(shift);

        // good suffix rule for shifting
        shift += goodSuffix[0];
      } else {
        char mismatchChar = txt.charAt(shift + j);
        int badCharShift = j - badChar.getOrDefault(mismatchChar, -1);
        int goodSuffixShift = goodSuffix[j + 1];

        // maximum shift
        shift += Math.max(badCharShift, goodSuffixShift);
      }
    }

    return foundIndices;
  }

  /**
   * Bad Character Heuristic using HashMap
   */
  private static Map<Character, Integer> badCharHeuristic(String pattern) {
    Map<Character, Integer> badChar = new HashMap<>();

    for (int i = 0; i < pattern.length(); i++) {
      badChar.put(pattern.charAt(i), i);
    }

    return badChar;
  }

  /**
   * Good Suffix Heuristic
   * 
   * For each position in the pattern, determines how far to shift
   * when a mismatch occurs at that position
   */
  private static int[] goodSuffixHeuristic(String pattern) {
    int patLen = pattern.length();
    int[] shift = new int[patLen + 1];
    int[] borderPos = new int[patLen + 1];

    // Initialize
    for (int i = 0; i < shift.length; i++) {
      shift[i] = 0;
    }
    for (int i = 0; i < borderPos.length; i++) {
      borderPos[i] = 0;
    }

    // Preprocessing for case - suffix that appears elsewhere
    int i = patLen;
    int j = patLen + 1;
    borderPos[i] = j;

    while (i > 0) {
      // border of pattern[i..patLen-1]
      while (j <= patLen && pattern.charAt(i - 1) != pattern.charAt(j - 1)) {
        if (shift[j] == 0) {
          shift[j] = j - i;
        }
        j = borderPos[j];
      }
      i--;
      j--;
      borderPos[i] = j;
    }

    // Preprocessing for case - prefix that matches a suffix
    j = borderPos[0];
    for (i = 0; i <= patLen; i++) {
      if (shift[i] == 0) {
        shift[i] = j;
      }
      if (i == j) {
        j = borderPos[j];
      }
    }

    return shift;
  }

}
