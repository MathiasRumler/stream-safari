package mvp.streamy.services;

import lombok.AllArgsConstructor;
import mvp.streamy.Repository.RiddleRepository;
import mvp.streamy.models.GameResult;
import mvp.streamy.models.Riddle;
import mvp.streamy.services.StreamPipelineEngineService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class StreamGameService {

    private final RiddleRepository riddleRepository;
    private final StreamPipelineEngineService engine;
    private final StreamPipelineEngineServiceV2 engine2;

    public List<Riddle> getAllRiddles() {
        return riddleRepository.findAll();
    }

    public GameResult submitAnswer(
            String riddleId,
            String pipeline
    ) {
        Riddle<?> riddle = riddleRepository.findById(riddleId);

        List<?> result =
                engine2.execute(riddle.input(), pipeline, riddle.dataType());

        boolean success =
                result.equals(riddle.expectedOutput());

        String message =
                success
                        ? "Correct solution 🎉"
                        : "Incorrect result, try again";

        return new GameResult<>(success, result, message);
    }


//    private void validateAgainstRiddle(
//            Riddle riddle,
//            String pipeline
//    ) {
//        for (String forbidden : riddle.forbiddenOperations()) {
//            if (pipeline.contains(forbidden)) {
//                throw new IllegalArgumentException(
//                        "This riddle forbids using " + forbidden
//                );
//            }
//        }
//
//    }
}
