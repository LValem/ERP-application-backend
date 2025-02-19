package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.FeedbackDto;
import ee.taltech.iti03022024project.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RequiredArgsConstructor
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<FeedbackDto> createFeedback(@RequestBody FeedbackDto feedbackDto) {
        FeedbackDto createdFeedback = feedbackService.createFeedback(feedbackDto);
        return ResponseEntity.ok(createdFeedback);
    }
}
