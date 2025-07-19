package gateway.client;

import gateway.dto.RequestDto;
import gateway.dto.RequestItemDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class ItemRequestClient extends BaseClient {
    private final String serverStringUrl = "http://localhost:9090/requests";//не могу сделать заглавными ругается чекСтайл. И статик нельзя наверное, он же во всех классах есть


    protected ItemRequestClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<RequestDto> addItemRequest(RequestItemDto requestItemDto, Long userId) {
        log.info("Попытка добавить запрос на элемент: {}, для пользователя с ID {}", requestItemDto, userId);
        return sendRequest(serverStringUrl, HttpMethod.POST, requestItemDto, RequestDto.class, userId);
    }

    public ResponseEntity<List<RequestDto>> getUserItemRequests(Long userId) {
        log.info("Запрос на получение запросов пользователя с ID {}", userId);
        return sendRequest(serverStringUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<RequestDto>>() {}, userId);
    }

    public ResponseEntity<List<RequestDto>> getOtherUsersItemRequests(Long userId) {
        String url = serverStringUrl + "/all";
        log.info("Запрос на получение запросов других пользователей для пользователя с ID {}", userId);
        return sendRequest(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<RequestDto>>() {}, userId);
    }

    public ResponseEntity<RequestDto> getItemRequestById(Long userId, Long requestId) {
        String url = serverStringUrl + "/" + requestId;
        log.info("Запрос на получение запроса на элемент с ID {} для пользователя с ID {}", requestId, userId);
        return sendRequest(url, HttpMethod.GET, null, RequestDto.class, userId);
    }
}