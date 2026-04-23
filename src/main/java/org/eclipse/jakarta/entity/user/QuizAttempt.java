package org.eclipse.jakarta.entity.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Plain column — quiz lives in content_db
    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    private Double score;

    @Column(name = "max_score")
    private Double maxScore;

    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber;

    @Column(name = "time_spent")
    private Integer timeSpent;

    @Column(name = "attempted_at")
    private LocalDateTime attemptedAt;

    @OneToMany(mappedBy = "quizAttempt", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuestionAttempt> questionAttempts;

    @PrePersist
    private void prePersist() {
        attemptedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public Double getMaxScore() { return maxScore; }
    public void setMaxScore(Double maxScore) { this.maxScore = maxScore; }

    public Integer getAttemptNumber() { return attemptNumber; }
    public void setAttemptNumber(Integer attemptNumber) { this.attemptNumber = attemptNumber; }

    public Integer getTimeSpent() { return timeSpent; }
    public void setTimeSpent(Integer timeSpent) { this.timeSpent = timeSpent; }

    public LocalDateTime getAttemptedAt() { return attemptedAt; }
    public void setAttemptedAt(LocalDateTime attemptedAt) { this.attemptedAt = attemptedAt; }

    public List<QuestionAttempt> getQuestionAttempts() { return questionAttempts; }
    public void setQuestionAttempts(List<QuestionAttempt> questionAttempts) { this.questionAttempts = questionAttempts; }
}
