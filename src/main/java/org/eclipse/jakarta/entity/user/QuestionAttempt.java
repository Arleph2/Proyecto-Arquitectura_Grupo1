package org.eclipse.jakarta.entity.user;

import jakarta.persistence.*;

@Entity
@Table(name = "question_attempts")
public class QuestionAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_attempt_id", nullable = false)
    private QuizAttempt quizAttempt;

    // Plain columns — question and answer live in content_db
    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "selected_answer_id")
    private Long selectedAnswerId;

    @Column(name = "is_correct")
    private Boolean correct;

    @Column(name = "time_spent")
    private Integer timeSpent;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public QuizAttempt getQuizAttempt() { return quizAttempt; }
    public void setQuizAttempt(QuizAttempt quizAttempt) { this.quizAttempt = quizAttempt; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public Long getSelectedAnswerId() { return selectedAnswerId; }
    public void setSelectedAnswerId(Long selectedAnswerId) { this.selectedAnswerId = selectedAnswerId; }

    public Boolean getCorrect() { return correct; }
    public void setCorrect(Boolean correct) { this.correct = correct; }

    public Integer getTimeSpent() { return timeSpent; }
    public void setTimeSpent(Integer timeSpent) { this.timeSpent = timeSpent; }
}
