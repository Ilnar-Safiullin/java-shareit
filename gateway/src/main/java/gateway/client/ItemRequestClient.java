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
    private final String SERVER_STRING_URL = "http://localhost:9090/requests";


    protected ItemRequestClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<RequestDto> addItemRequest(RequestItemDto requestItemDto, Long userId) {
        log.info("Попытка добавить запрос на элемент: {}, для пользователя с ID {}", requestItemDto, userId);
        return sendRequest(SERVER_STRING_URL, HttpMethod.POST, requestItemDto, RequestDto.class, userId);
    }

    public ResponseEntity<List<RequestDto>> getUserItemRequests(Long userId) {
        log.info("Запрос на получение запросов пользователя с ID {}", userId);
        return sendRequest(SERVER_STRING_URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<RequestDto>>() {}, userId);
    }

    public ResponseEntity<List<RequestDto>> getOtherUsersItemRequests(Long userId) {
        String url = SERVER_STRING_URL + "/all";
        log.info("Запрос на получение запросов других пользователей для пользователя с ID {}", userId);
        return sendRequest(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<RequestDto>>() {}, userId);
    }

    public ResponseEntity<RequestDto> getItemRequestById(Long userId, Long requestId) {
        String url = SERVER_STRING_URL + "/" + requestId;
        log.info("Запрос на получение запроса на элемент с ID {} для пользователя с ID {}", requestId, userId);
        return sendRequest(url, HttpMethod.GET, null, RequestDto.class, userId);
    }
}