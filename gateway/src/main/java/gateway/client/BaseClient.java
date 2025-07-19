package gateway.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


public abstract class BaseClient {

    protected final RestTemplate restTemplate;

    protected BaseClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected <T> ResponseEntity<T> sendRequest(String url, HttpMethod method, Object requestBody, Class<T> responseType, Long userId) {
        HttpHeaders headers = createHeaders(userId);
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            return restTemplate.exchange(url, method, requestEntity, responseType);
        } catch (RestClientException e) {
            throw e;
        }
    }

    protected <T> ResponseEntity<T> sendRequest(String url, HttpMethod method, Object requestBody, ParameterizedTypeReference<T> responseType, Long userId) {
        HttpHeaders headers = createHeaders(userId);
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            return restTemplate.exchange(url, method, requestEntity, responseType);
        } catch (RestClientException e) {
            throw e;
        }
    }

    protected <T> ResponseEntity<T> sendRequest(String url, HttpMethod method, Object requestBody, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            return restTemplate.exchange(url, method, requestEntity, responseType);
        } catch (RestClientException e) {
            throw e;
        }
    }

    protected <T> ResponseEntity<T> sendRequest(String url, HttpMethod method, Object requestBody, ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            return restTemplate.exchange(url, method, requestEntity, responseType);
        } catch (RestClientException e) {
            throw e;
        }
    }

    private HttpHeaders createHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }
}
