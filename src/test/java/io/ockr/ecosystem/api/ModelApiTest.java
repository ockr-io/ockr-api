package io.ockr.ecosystem.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.ockr.ecosystem.entity.Model;
import io.ockr.ecosystem.entity.TextPosition;
import io.ockr.ecosystem.entity.api.InferenceRequestBody;
import io.ockr.ecosystem.entity.api.InferenceResponse;
import io.ockr.ecosystem.entity.api.ModelRequestBody;
import io.ockr.ecosystem.service.ModelService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.with;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ModelApiTest {

    public final String MODEL_ENDPOINT = "/api/v1/model/";
    public final String REGISTER_ENDPOINT = MODEL_ENDPOINT + "register";
    public final String INFERENCE_ENDPOINT = MODEL_ENDPOINT + "inference";

    @LocalServerPort
    private int serverPort;

    @Autowired
    private ModelService modelService;
    private WireMockServer wireMockServer;

    @BeforeAll
    public void setup() throws JsonProcessingException {
        modelService.deleteAllModels();
        wireMockServer = new WireMockServer(5001);
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

        RestAssured.port = serverPort;
        RestAssured.baseURI = "http://localhost";
    }

    @AfterAll
    public void teardown() {
        modelService.deleteAllModels();
        wireMockServer.stop();
    }

    @Test
    @Order(1)
    public void testRegisterModel() {
        ModelRequestBody modelRequestBody = new ModelRequestBody("PP-OCRv3", "http://127.0.0.1", 5001);
        with().contentType(ContentType.JSON).body(modelRequestBody).post(REGISTER_ENDPOINT).then().statusCode(200);
    }

    @Test
    @Order(2)
    public void testGetAllModels() {
        List<String> models = with().get(MODEL_ENDPOINT).then().statusCode(200).extract().body().jsonPath().getList(".", String.class);
        assert models.size() == 1;
    }

    @Test
    @Order(3)
    public void testRegisterModelWithExistingName() {
        ModelRequestBody modelRequestBody = new ModelRequestBody("PP-OCRv3", "http://127.0.0.1", 5001);
        with().contentType(ContentType.JSON).body(modelRequestBody).post(REGISTER_ENDPOINT).then().statusCode(400);
    }

    @Test
    @Order(4)
    public void testRegisterAnotherModel() {
        ModelRequestBody modelRequestBody = new ModelRequestBody("PP-OCRv4", "http://127.0.0.1", 5000);
        with().contentType(ContentType.JSON).body(modelRequestBody).post(REGISTER_ENDPOINT).then().statusCode(200);
    }

    @Test
    @Order(5)
    public void testGetAllModelsAgain() {
        List<String> models = with().get(MODEL_ENDPOINT).then().statusCode(200).extract().body().jsonPath().getList(".", String.class);
        Assertions.assertEquals(models.size(), 2);
    }

    @Test
    @Order(6)
    public void testInference() {
        String base64Image = "SGVsbG8gZnJvbSB0aGUgb3RoZXIgc2lkZQ==";
        String model = "PP-OCRv3";
        Response response = with().contentType(ContentType.JSON).body(new InferenceRequestBody(model, base64Image)).post(INFERENCE_ENDPOINT);
        Assertions.assertEquals(200, response.getStatusCode());
        InferenceResponse inferenceResponse = response.getBody().as(InferenceResponse.class);
        Assertions.assertEquals(inferenceResponse.getModel(), model);
        Assertions.assertEquals(inferenceResponse.getOcrResults().size(), 2);
    }
}
