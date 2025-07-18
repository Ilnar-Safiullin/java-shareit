package gateway.client;

import gateway.dto.RequestUserDto;
import gateway.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserClient userClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAdd() {
        RequestUserDto requestUserDto = new RequestUserDto("testName", "test@email.com");
        UserDto userDto = new UserDto(1L, "testName", "test@email.com");
        ResponseEntity<UserDto> responseEntity = ResponseEntity.ok(userDto);

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(UserDto.class)))
                .thenReturn(responseEntity);

        ResponseEntity<UserDto> result = userClient.add(requestUserDto);

        assertThat(result.getBody()).isEqualTo(userDto);
        verify(restTemplate, times(1)).exchange(any(String.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(UserDto.class));
    }

    @Test
    void testUpdateUser() {
        long userId = 1L;
        RequestUserDto requestUserDto = new RequestUserDto("testName", "test@email.com");
        UserDto userDto = new UserDto(userId, "testName", "test@email.com");
        ResponseEntity<UserDto> responseEntity = ResponseEntity.ok(userDto);

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(UserDto.class)))
                .thenReturn(responseEntity);

        ResponseEntity<UserDto> result = userClient.updateUser(userId, requestUserDto);

        assertThat(result.getBody()).isEqualTo(userDto);
        verify(restTemplate, times(1)).exchange(any(String.class), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(UserDto.class));
    }

    @Test
    void testDeleteUser() {
        long userId = 1L;

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> result = userClient.deleteUser(userId);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        verify(restTemplate, times(1)).exchange(any(String.class), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class));
    }

    @Test
    void testGetUserById() {
        long userId = 1L;
        UserDto userDto = new UserDto(userId, "testName", "test@email.com");
        ResponseEntity<UserDto> responseEntity = ResponseEntity.ok(userDto);

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(UserDto.class)))
                .thenReturn(responseEntity);

        ResponseEntity<UserDto> result = userClient.getUserById(userId);

        assertThat(result.getBody()).isEqualTo(userDto);
        verify(restTemplate, times(1)).exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(UserDto.class));
    }
}

