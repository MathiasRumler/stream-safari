package mvp.streamy.Repository;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mvp.streamy.models.Riddle;
import mvp.streamy.models.RiddleDefinition;
import mvp.streamy.models.SafariAnimal;
import mvp.streamy.services.StreamPipelineEngineServiceV2;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Repository
public class RiddleRepository {

  private final Map<String, Riddle> riddles = new HashMap<>();
  private final ObjectMapper objectMapper;
  private final StreamPipelineEngineServiceV2 engine;

  public RiddleRepository(ObjectMapper objectMapper, StreamPipelineEngineServiceV2 engine) {
    this.objectMapper = objectMapper;
    this.engine = engine;
  }

  @PostConstruct
  public void init() {
    try (InputStream is = getClass().getResourceAsStream("/riddles.json")) {
      List<RiddleDefinition> definitions = objectMapper.readValue(is, new TypeReference<List<RiddleDefinition>>() {});
      for (RiddleDefinition def : definitions) {
        List<SafariAnimal> input = "WITH_DUPLICATES".equals(def.dataset())
            ? SafariScenarios.WITH_DUPLICATES
            : SafariScenarios.BASE_ANIMALS;

        // Compute expected output dynamically
        Object expectedOutput = engine.execute(input, def.solution(), SafariAnimal.class);

        riddles.put(def.id(), new Riddle(
            def.id(),
            def.description(),
            SafariAnimal.class,
            input,
            expectedOutput
        ));
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to load riddles", e);
    }
  }

  public List<Riddle> findAll() {
    return new ArrayList<>(riddles.values());
  }

  public Riddle findById(String id) {
    return riddles.get(id);
  }
}