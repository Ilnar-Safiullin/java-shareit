package gateway.client;

import gateway.dto.RequestUserDto;
import gateway.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class UserClient extends BaseClient {
    private final String SERVER_STRING_URL = "http://localhost:9090/users";


    protected UserClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<UserDto> add(RequestUserDto requestUserDto) {
        log.info("Попытка добавить User");
        return sendRequest(SERVER_STRING_URL, HttpMethod.POST, requestUserDto, UserDto.class);
    }

    public ResponseEntity<UserDto> updateUser(long userId, RequestUserDto requestUserDto) {
        log.info("Попытка обновить User");
        String url = SERVER_STRING_URL + "/" + userId;
        return sendRequest(url, HttpMethod.PATCH, requestUserDto, UserDto.class);
    }

    public ResponseEntity<?> deleteUser(long userId) {
        String url = SERVER_STRING_URL + "/" + userId;
        return sendRequest(url, HttpMethod.DELETE, null, Void.class);
    }

    public ResponseEntity<UserDto> getUserById(long userId) {
        String url = SERVER_STRING_URL + "/" + userId;
        return sendRequest(url, HttpMethod.GET, null, UserDto.class);
    }
}
