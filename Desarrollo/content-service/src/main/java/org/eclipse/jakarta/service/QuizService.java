package org.eclipse.jakarta.service;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.eclipse.jakarta.repository.QuizRepository;
import org.eclipse.jakarta.dto.CorrectAnswerDto;
import org.eclipse.jakarta.dto.QuizDto;
import java.util.Optional;

@Stateless
public class QuizService {

    @EJB private QuizRepository quizRepository;

    public Optional<QuizDto> findQuizByContentId(Long contentId) {
        return quizRepository.findByContentIdWithQuestions(contentId).map(QuizDto::from);
    }

    public Optional<CorrectAnswerDto> findCorrectAnswer(Long questionId) {
        return quizRepository.findCorrectAnswerByQuestionId(questionId)
                .map(a -> new CorrectAnswerDto(questionId, a.getId()));
    }
}
