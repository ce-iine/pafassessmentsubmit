package vttp2023.batch4.paf.assessment.services;

import java.io.StringReader;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map.Entry;
import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

@Service
public class ForexService {


	RestTemplate restTemplate = new RestTemplate();

	String beginurl = "https://api.frankfurter.app/latest?amount=";
	String end = "&from=AUD&to=SGD";



	// TODO: Task 5 
	public float convert(String from, String to, float amount) {

		String finalUrl = beginurl + amount + end;
		ResponseEntity<String> result = restTemplate.getForEntity(finalUrl, String.class);

		String jsonString = result.getBody().toString();
		JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        JsonObject jsonObject = jsonReader.readObject();

        JsonObject jsonObjectData = jsonObject.getJsonObject("rates");
		float floatVal = Float.parseFloat(jsonObjectData.getJsonNumber("SGD").toString());

		System.out.println("THIS SHOULD BE SGD>>>>" + jsonObjectData.getJsonNumber("SGD"));

		return floatVal;
	}
}
