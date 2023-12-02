package io.ockr.ecosystem.service;

import io.ockr.ecosystem.entity.Model;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@ComponentScan
public class ModelServiceTest {

    @Autowired
    private ModelService modelService;

    @BeforeEach
    public void setup() {
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
}
