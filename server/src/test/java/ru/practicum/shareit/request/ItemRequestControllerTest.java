package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.service.RequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class ItemRequestControllerTest {

    @Mock
    private RequestService requestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(itemRequestController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void addItemRequest() throws Exception {
        RequestItemDto requestItemDto = new RequestItemDto("test", LocalDateTime.now());
        RequestDto requestDto = new RequestDto(1L, "test", null, LocalDateTime.now(), new HashSet<>());

        when(requestService.addItemRequest(ArgumentMatchers.any(), anyLong())).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestItemDto))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("test"));

        verify(requestService, times(1)).addItemRequest(any(), anyLong());
    }

    @Test
    void getUserItemRequests() throws Exception {
        long userId = 1L;
        RequestDto requestDto = new RequestDto(1L, "test", null, LocalDateTime.now(), new HashSet<>());

        when(requestService.getUserItemRequests(userId)).thenReturn(Collections.singletonList(requestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("test"));

        verify(requestService, times(1)).getUserItemRequests(userId);
    }

    @Test
    void getOtherUsersItemRequests() throws Exception {
        long userId = 1L;
        RequestDto requestDto = new RequestDto(1L, "test", null, LocalDateTime.now(), new HashSet<>());

        when(requestService.getOtherUsersItemRequests(userId)).thenReturn(Collections.singletonList(requestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("test"));

        verify(requestService, times(1)).getOtherUsersItemRequests(userId);
    }

    @Test
    void getItemRequestById() throws Exception {
        long userId = 1L;
        long requestId = 1L;
        RequestDto requestDto = new RequestDto(requestId, "test", null, LocalDateTime.now(), new HashSet<>());

        when(requestService.getItemRequestById(userId, requestId)).thenReturn(requestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("test"));

        verify(requestService, times(1)).getItemRequestById(userId, requestId);
    }
}