package io.ockr.ecosystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;
import io.ockr.ecosystem.entity.Model;
import io.ockr.ecosystem.entity.api.InferenceResponse;
import io.ockr.ecosystem.repository.ModelRepository;
import org.jooq.tools.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModelService {

    @Autowired
    private ModelRepository modelRepository;

    private final OkHttpClient client;

    public ModelService() {
        this.client = new OkHttpClient();
    }

    public InferenceResponse inference(String modelName, String base64Image, String modelVersion,
                                       Map<String, Object> parameters) throws IOException {
        Model model = this.getModelByName(modelName);

        if (model == null) {
            return null;
        }

        return this.inference(model, base64Image, modelVersion, parameters);
    }

    public InferenceResponse inference(Model model, String base64Image, String modelVersion,
                                       Map<String, Object> parameters) throws IOException {
        Map<String, Object> jsonObject = new HashMap<>();

        if (parameters != null) {
            jsonObject.put("parameters", parameters);
        }

        jsonObject.put("base64_image", base64Image);
        jsonObject.put("ocr_model_name", model.getName());

        if (modelVersion != null) {
            jsonObject.put("ocr_model_version", modelVersion);
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, JSONObject.toJSONString(jsonObject));
        Request request = new Request.Builder()
                .url(model.getUrl() + ":" + model.getPort() + "/inference")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.body().string(), InferenceResponse.class);
    }

    public List<Model> getAllModels() {
        return modelRepository.findAll();
    }

    public Model getModelByName(String name) {
        return modelRepository.findByName(name).orElse(null);
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
