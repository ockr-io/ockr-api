package io.ockr.ecosystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.ockr.ecosystem.entity.Model;
import io.ockr.ecosystem.entity.TextPosition;
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
                .ocrResults(List.of(
                        TextPosition.builder()
                                .x(0.0)
                                .y(0.0)
                                .width(100.0)
                                .height(100.0)
                                .text("Hello World")
                                .page(0)
                                .build(),
                        TextPosition.builder()
                                .x(20.0)
                                .y(20.0)
                                .width(40.0)
                                .height(100.0)
                                .text("Pizza")
                                .page(0)
                                .build()
                ))
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
        Assertions.assertThat(inferenceResponse.getOcrResults()).isNotNull();
        Assertions.assertThat(inferenceResponse.getOcrResults().size()).isEqualTo(2);
        Assertions.assertThat(inferenceResponse.getOcrResults().get(0).getText()).isEqualTo("Hello World");
        Assertions.assertThat(inferenceResponse.getOcrResults().get(1).getText()).isEqualTo("Pizza");
    }
}
