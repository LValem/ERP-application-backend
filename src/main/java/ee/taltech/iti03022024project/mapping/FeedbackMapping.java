package ee.taltech.iti03022024project.mapping;

import ee.taltech.iti03022024project.dto.FeedbackDto;
import ee.taltech.iti03022024project.dto.employee.EmployeeDto;
import ee.taltech.iti03022024project.entity.EmployeeEntity;
import ee.taltech.iti03022024project.entity.FeedbackEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FeedbackMapping {

    FeedbackDto feedbackToDto(FeedbackEntity feedbackEntity);
    FeedbackEntity feedbackToEntity(FeedbackDto feedbackDto);
}
