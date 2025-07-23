package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.user.dto.RequestUserDto;


import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestUserDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator;

    public RequestUserDtoJsonTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    public void testRequestUserDtoSerialization() throws Exception {
        RequestUserDto request = new RequestUserDto("testName", "test@email.com");
        String json = objectMapper.writeValueAsString(request);

        assertThat(json).contains("name");
        assertThat(json).contains("email");
    }

    @Test
    public void testRequestUserDtoDeserialization() throws Exception {
        String json = "{\"name\":\"testName\",\"email\":\"test@email.com\"}";
        RequestUserDto request = objectMapper.readValue(json, RequestUserDto.class);

        assertThat(request.getName()).isEqualTo("testName");
        assertThat(request.getEmail()).isEqualTo("test@email.com");
    }
}