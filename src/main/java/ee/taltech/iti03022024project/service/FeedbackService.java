package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.FeedbackDto;
import ee.taltech.iti03022024project.entity.EmployeeEntity;
import ee.taltech.iti03022024project.entity.FeedbackEntity;
import ee.taltech.iti03022024project.mapping.FeedbackMapping;
import ee.taltech.iti03022024project.repository.EmployeeRepository;
import ee.taltech.iti03022024project.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapping feedbackMapping;

    public FeedbackDto createFeedback(FeedbackDto feedbackDto){
        FeedbackEntity entity = new FeedbackEntity(null, feedbackDto.getName(),
                feedbackDto.getDifficulty(), feedbackDto.getStudymaterial_quality(),
                feedbackDto.getInvolvement(), feedbackDto.getCourse_structure(),
                feedbackDto.getTeamwork(), feedbackDto.getOutcome());
        FeedbackEntity savedFeedbackEntity = feedbackRepository.save(entity);
        return feedbackMapping.feedbackToDto(savedFeedbackEntity);
    }
}
