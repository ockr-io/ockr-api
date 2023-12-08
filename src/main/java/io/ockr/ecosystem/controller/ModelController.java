package io.ockr.ecosystem.controller;

import io.ockr.ecosystem.entity.Model;
import io.ockr.ecosystem.entity.api.InferenceRequestBody;
import io.ockr.ecosystem.entity.api.InferenceResponse;
import io.ockr.ecosystem.entity.api.ModelRequestBody;
import io.ockr.ecosystem.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/v1/model/")
public class ModelController {

    @Autowired
    private ModelService modelService;

    @GetMapping("/")
    public ResponseEntity<List<String>> getAllModels() {
        List<Model> models = modelService.getAllModels();
        return ResponseEntity.ok(models.stream().map(Model::getName).toList());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerModel(@RequestBody ModelRequestBody requestBody) {
        Model model = Model.builder()
                .name(requestBody.getName())
                .url(requestBody.getUrl())
                .port(requestBody.getPort())
                .build();
        try {
            modelService.saveModel(model);
        } catch (org.springframework.dao.DataIntegrityViolationException exception) {
            return ResponseEntity.status(400).body("Model already exists");
        }

        return ResponseEntity.status(200).build();
    }

    @PostMapping("/inference")
    public ResponseEntity<?> inference(@RequestBody InferenceRequestBody requestBody) {
        InferenceResponse inferenceResponse;

        try {
            inferenceResponse = modelService.inference(requestBody.getModel(), requestBody.getBase64Image());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }

        if (inferenceResponse == null) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.ok(inferenceResponse);
    }
}
