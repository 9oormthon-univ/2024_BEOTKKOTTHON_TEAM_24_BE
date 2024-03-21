package goorm.reinput.insight.service;

import goorm.reinput.insight.domain.dto.InsightRequestDto;
import goorm.reinput.insight.repository.InsightRepository;
import goorm.reinput.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsightService {
    private final InsightRepository insightRepository;
    private final UserRepository userRepository;

    public InsightRequestDto insightStore(InsightRequestDto dto){
        return dto;
    }
}
