package io.ockr.ecosystem.service;

import io.ockr.ecosystem.entity.Model;
import io.ockr.ecosystem.repository.ModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ModelService {

    @Autowired
    private ModelRepository modelRepository;

    public List<Model> getAllModels() {
        return modelRepository.findAll();
    }

    public Model getModelByName(String name) {
        return modelRepository.findById(name).orElse(null);
    }

    public void saveModel(Model model) {
        modelRepository.save(model);
    }

    public void saveAll(List<Model> models) {
        modelRepository.saveAll(models);
    }

    public void deleteModelByName(String name) {
        modelRepository.deleteById(name);
    }

    public void deleteAllModels() {
        modelRepository.deleteAll();
    }
}
