package io.ockr.ecosystem.controller;

import io.ockr.ecosystem.entity.Model;
import io.ockr.ecosystem.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
