package goorm.reinput.reminder.util;

import goorm.reinput.reminder.domain.Question;
import goorm.reinput.reminder.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestionInitializer implements CommandLineRunner {
    private final QuestionRepository questionRepository;

    @Override
    public void run(String... args) throws Exception {
        List<String> questions = Arrays.asList(
                "이 인사이트를 어떤 사람에게 추천해주고 싶나요?",
                "이 인사이트를 처음 발견했을 때 무엇이 가장 인상 깊었나요?",
                "이 인사이트를 실제로 적용해 본 경험이 있나요?",
                "이 인사이트를 통해 배운 것을 다른 사람에게 어떻게 설명하고 싶나요?",
                "이 인사이트를 통해 어떤 새로운 관점이나 방법을 배웠나요?"
        );

        questions.forEach(question -> questionRepository.save(new Question(question)));
    }
}
