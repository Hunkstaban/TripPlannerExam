package dat.services;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiPullTemp {
    ObjectMapper om = new ObjectMapper();

    public void APIToDTO () {
        try {
            // Create an HttpClient instance
            HttpClient client = HttpClient.newHttpClient();

            // Create a request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("URL TO API"))
                    .version(HttpClient.Version.HTTP_1_1)
                    .GET()
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check the status code and print the response
            if (response.statusCode() == 200) {
                String body = response.body();
                /*Example:
                CityInfoDTO[] cityDTOS = om.readValue(body, CityInfoDTO[].class);
                return cityDTOS;*/
            } else {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        return null;
    }
}
