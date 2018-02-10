package com.utcluj;

import java.math.BigDecimal;
import java.util.List;


public class Similarity {
  /**
   * Method to calculate cosine similarity of vectors
   * 1 - exactly similar (angle between them is 0)
   * 0 - orthogonal vectors (angle between them is 90)
   *
   * @param vector1 - vector in the form [a1, a2, a3, ..... an]
   * @param vector2 - vector in the form [b1, b2, b3, ..... bn]
   * @return - the cosine similarity of vectors (ranges from 0 to 1)
   */
  public static float calculateCosineSimilarity(List<BigDecimal> vector1, List<BigDecimal> vector2) {
    double dotProduct = 0.0;
    double normA = 0.0;
    double normB = 0.0;
    for (int i = 0; i < vector1.size(); i++) {
      dotProduct += vector1.get(i).intValue() * vector2.get(i).intValue();
      normA += Math.pow(vector1.get(i).intValue(), 2);
      normB += Math.pow(vector2.get(i).intValue(), 2);
    }
    return (float) (dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)));
  }


  /**
   *  Calculates the log likelihood ratio for thwo events
   *
   * @param k11 The number of times the two events occurred together
   * @param k12 The number of times the second event occurred WITHOUT the first event
   * @param k21 The number of times the first event occurred WITHOUT the second event
   * @param k22 The number of times something else occurred (i.e. was neither of these events
   * @return the log likelihood ratio
   */
  public static double logLikelihoodRatio(long k11, long k12, long k21, long k22) {
    checkArgument(k11 >= 0 && k12 >= 0 && k21 >= 0 && k22 >= 0);
    // note that we have counts here, not probabilities, and that the entropy is not normalized.
    double rowEntropy = entropy(k11, k12) + entropy(k21, k22);
    double columnEntropy = entropy(k11, k21) + entropy(k12, k22);
    double matrixEntropy = entropy(k11, k12, k21, k22);
    if (rowEntropy + columnEntropy > matrixEntropy) {
      // round off error
      return 0.0;
    }
    return 2.0 * (matrixEntropy - rowEntropy - columnEntropy);
  }

  /**
   * Calculates the un-normalized Shannon entropy.
   * This is -sum x_i log x_i / N = -N sum x_i/N log x_i/N where N = sum x_i If the x's sum to 1, then this is the same as the normal expression.
   * Leaving this un-normalized makes working with counts and computing the LLR easier.
   *
   * Returns:
   * The entropy value for the elements
   **/
  public static double entropy(long... elements) {
    long sum = 0;
    double result = 0.0;
    for (long element : elements) {
      checkArgument(element >= 0);
      result += xLogX(element);
      sum += element;
    }
    return xLogX(sum) - result;
  }

  private static double entropy(long a, long b) {
    return xLogX(a + b) - xLogX(a) - xLogX(b);
  }

  private static double xLogX(long x) {
    return x == 0 ? 0.0 : x * Math.log(x);
  }

  public static void checkArgument(boolean expression) {
    if (!expression) {
      throw new IllegalArgumentException("The element can not be negative !");
    }
  }

}
