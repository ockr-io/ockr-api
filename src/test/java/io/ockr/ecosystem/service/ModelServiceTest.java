package io.ockr.ecosystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.ockr.ecosystem.entity.Model;
import io.ockr.ecosystem.entity.api.InferenceResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@DataJpaTest
@ComponentScan
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ModelServiceTest {

    @Autowired
    private ModelService modelService;

    @BeforeAll
    public void setup() throws JsonProcessingException {
        WireMockServer wireMockServer = new WireMockServer(5001);
        wireMockServer.start();

        InferenceResponse inferenceResponse = InferenceResponse.builder()
                .model("PP-OCRv3")
                .hash("21b4f4bd9e64ed355c3eb676a28ebedaf6d8f17bdc365995b319097153044080516bd083bfcce66121a3072646994c8430cc382b8dc543e84880183bf856cff5")
                .subHashes(List.of(
                        "848b0779ff415f0af4ea14df9dd1d3c29ac41d836c7808896c4eba19c51ac40a439caf5e61ec88c307c7d619195229412eaa73fb2a5ea20d23cc86a9d8f86a0f",
                        "3d637ae63d59522dd3cb1b81c1ad67e56d46185b0971e0bc7dd2d8ad3b26090acb634c252fc6a63b3766934314ea1a6e59fa0c8c2bc027a7b6a460b291cd4dfb",
                        "2ac968752f624be3e3df46764b51b7831feb70d40307df5d587d4793bffeaf8b4042a1fd6d465df2aacc3304328d431ef10e083baf690b8cc535480a4fef092f",
                        "99c426cc0bf3c465f9c4a49e5a6c3d09a43c4f95631776b354be0727f3f65567ae9357c5d55015e2cfbb27ead98e1bb54f2a0f26042ad267a279f0e3a8c5b2e9"))
                .algorithm("square-puzzle")
                .algorithmParameters(Map.of("xTiles", "2", "yTiles", "2"))
                .build();

        ObjectMapper mapper = new ObjectMapper();

        wireMockServer.stubFor(post(urlEqualTo("/inference"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mapper.writeValueAsString(inferenceResponse))));

        modelService.deleteAllModels();
        List<Model> models = new ArrayList<>(
                List.of(
                        new Model("PP-OCRv4", "http://127.0.0.1", 5000),
                        new Model("PP-OCRv3", "http://127.0.0.1", 5001)
                )
        );
        modelService.saveAll(models);
    }

    @Test
    public void testGetAllModels() {
        List<Model> models = modelService.getAllModels();
        assert models.size() == 2;
    }

    @Test
    public void testGetModelByName() {
        Model model = modelService.getModelByName("PP-OCRv4");
        Assertions.assertThat(model).isNotNull();
        Assertions.assertThat(model.getName()).isEqualTo("PP-OCRv4");
        Assertions.assertThat(model.getUrl()).isEqualTo("http://127.0.0.1");
    }

    @Test
    public void testSaveModel() {
        Model model = new Model("Custom-OCR", "localhost", 5002);
        modelService.saveModel(model);
        Model savedModel = modelService.getModelByName("Custom-OCR");
        Assertions.assertThat(savedModel).isNotNull();
        Assertions.assertThat(savedModel.getName()).isEqualTo("Custom-OCR");
        Assertions.assertThat(savedModel.getUrl()).isEqualTo("localhost");
    }

    @Test
    public void testInference() throws IOException {
        InferenceResponse inferenceResponse = modelService.inference("PP-OCRv3", "base64Image");
        Assertions.assertThat(inferenceResponse).isNotNull();
        Assertions.assertThat(inferenceResponse.getModel()).isEqualTo("PP-OCRv3");
        Assertions.assertThat(inferenceResponse.getHash()).isEqualTo("21b4f4bd9e64ed355c3eb676a28ebedaf6d8f17bdc365995b319097153044080516bd083bfcce66121a3072646994c8430cc382b8dc543e84880183bf856cff5");
        Assertions.assertThat(inferenceResponse.getSubHashes()).isEqualTo(List.of(
                "848b0779ff415f0af4ea14df9dd1d3c29ac41d836c7808896c4eba19c51ac40a439caf5e61ec88c307c7d619195229412eaa73fb2a5ea20d23cc86a9d8f86a0f",
                "3d637ae63d59522dd3cb1b81c1ad67e56d46185b0971e0bc7dd2d8ad3b26090acb634c252fc6a63b3766934314ea1a6e59fa0c8c2bc027a7b6a460b291cd4dfb",
                "2ac968752f624be3e3df46764b51b7831feb70d40307df5d587d4793bffeaf8b4042a1fd6d465df2aacc3304328d431ef10e083baf690b8cc535480a4fef092f",
                "99c426cc0bf3c465f9c4a49e5a6c3d09a43c4f95631776b354be0727f3f65567ae9357c5d55015e2cfbb27ead98e1bb54f2a0f26042ad267a279f0e3a8c5b2e9"));
        Assertions.assertThat(inferenceResponse.getAlgorithm()).isEqualTo("square-puzzle");
        Assertions.assertThat(inferenceResponse.getAlgorithmParameters()).isEqualTo(
                Map.of("xTiles", "2", "yTiles", "2")
        );
    }
}
