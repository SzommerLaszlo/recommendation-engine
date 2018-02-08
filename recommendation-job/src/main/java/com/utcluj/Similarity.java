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

}
