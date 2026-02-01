package mvp.streamy.controller;

import lombok.AllArgsConstructor;
import mvp.streamy.models.RiddleResult;
import mvp.streamy.models.Riddle;
import mvp.streamy.models.SubmitAnswerRequestDTO;
import mvp.streamy.services.StreamGameService;
import mvp.streamy.services.StreamPipelineEngineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stream")
@AllArgsConstructor
public class StreamController {

    private final StreamPipelineEngineService streamPipelineEngineService;
    private final StreamGameService streamGameService;


    @GetMapping("/riddles")
    public List<Riddle> getRiddles() {
        return streamGameService.getAllRiddles();
    }

    @PostMapping("/submit")
    public ResponseEntity<RiddleResult> submitAnswer(
            @RequestBody SubmitAnswerRequestDTO request
    ) {
        return new ResponseEntity<>(streamGameService.submitAnswer(
                request.riddleId(),
                request.pipeline()), HttpStatus.OK
        );
    }
}
