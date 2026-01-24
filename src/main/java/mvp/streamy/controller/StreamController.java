package mvp.streamy.controller;

import lombok.AllArgsConstructor;
import mvp.streamy.models.GameResult;
import mvp.streamy.models.Riddle;
import mvp.streamy.models.SubmitAnswerRequestDTO;
import mvp.streamy.services.StreamGameService;
import mvp.streamy.services.StreamPipelineEngineService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/stream")
@AllArgsConstructor
public class StreamController {

    private final StreamPipelineEngineService streamPipelineEngineService;
    private final StreamGameService streamGameService;


    @GetMapping("/")
    public String renderStream(String userInput) {
        return userInput;
    }

    @GetMapping("/test")
    public String testEngine (){
        List<Integer> input =
                Arrays.asList(7, 2, 5, 2, 9, 1);
        String userPipeline =
                """
                .stream()
                .map(a->a*3)
                .toList()
                """;
      List<Integer> result =  streamPipelineEngineService.execute(input, userPipeline);

        System.out.println(result);
        return "Test rocks";
    }

    @GetMapping("/riddles")
    public List<Riddle> getRiddles() {
        return streamGameService.getAllRiddles();
    }

    @PostMapping("/submit")
    public GameResult submitAnswer(
            @RequestBody SubmitAnswerRequestDTO request
    ) {
        return streamGameService.submitAnswer(
                request.riddleId(),
                request.pipeline()
        );
    }
}
