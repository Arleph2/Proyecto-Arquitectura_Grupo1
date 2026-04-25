package org.eclipse.jakarta.dto;

import java.io.Serializable;

public class QuizAttemptItem implements Serializable {
    private long id;
    private long quizId;
    private double score;
    private double maxScore;

    public QuizAttemptItem(long id, long quizId, double score, double maxScore) {
        this.id = id;
        this.quizId = quizId;
        this.score = score;
        this.maxScore = maxScore;
    }

    public long getId() { return id; }
    public long getQuizId() { return quizId; }
    public double getScore() { return score; }
    public double getMaxScore() { return maxScore; }

    public String getRatio() {
        if (maxScore == 0) return "N/A";
        return String.format("%.0f%%", (score / maxScore) * 100);
    }
}
