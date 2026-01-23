package mvp.streamy.controller;

import lombok.AllArgsConstructor;
import mvp.streamy.services.StreamPipelineEngineService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/stream")
@AllArgsConstructor
public class StreamController {

    private final StreamPipelineEngineService streamPipelineEngineService;


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
}
