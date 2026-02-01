package mvp.streamy.services;

import lombok.AllArgsConstructor;
import mvp.streamy.Repository.RiddleRepository;
import mvp.streamy.models.RiddleResult;
import mvp.streamy.models.ResultValue;
import mvp.streamy.models.Riddle;
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

    public RiddleResult submitAnswer(
            String riddleId,
            String pipeline
    ) {
        Riddle riddle = riddleRepository.findById(riddleId);

        Object rawActual =
                engine2.execute(riddle.input(), pipeline, riddle.dataType());

        ResultValue actual =
                ResultValueFactory.from(rawActual);

        ResultValue expected =
                ResultValueFactory.from(riddle.expectedOutput());

        boolean success =
                actual.equals(expected);

        String message =
                success
                        ? "Correct solution 🎉"
                        : "Incorrect result, try again";

        return new RiddleResult(success, actual, message);
    }


}
