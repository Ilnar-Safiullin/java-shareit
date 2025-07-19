package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dal.RequestStorage;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dal.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private RequestStorage requestStorage;

    @Mock
    private UserStorage userStorage;

    @Mock
    private ItemStorage itemStorage;

    @InjectMocks
    private RequestServiceImpl requestService;

    @Test
    void addItemRequest_shouldSaveRequestWhenUserExists() {
        Long userId = 1L;
        RequestItemDto requestItemDto = new RequestItemDto("testRequest", LocalDateTime.now());
        User user = new User(userId, "testName", "test@example.com");
        Request savedRequest = new Request();
        savedRequest.setId(1L);
        savedRequest.setDescription(requestItemDto.getDescription());
        savedRequest.setRequester(user);
        savedRequest.setCreated(requestItemDto.getCreated());

        when(userStorage.findById(userId)).thenReturn(Optional.of(user));
        when(requestStorage.save(any(Request.class))).thenReturn(savedRequest);

        RequestDto result = requestService.addItemRequest(requestItemDto, userId);

        assertNotNull(result);
        assertEquals(savedRequest.getId(), result.getId());
        assertEquals(requestItemDto.getDescription(), result.getDescription());
        assertEquals(userId, result.getRequester().getId());

        verify(userStorage, times(1)).findById(userId);
        verify(requestStorage, times(1)).save(any(Request.class));
    }

    @Test
    void addItemRequest_shouldThrowNotFoundExceptionWhenUserNotFound() {
        Long userId = 99L;
        RequestItemDto requestItemDto = new RequestItemDto("testRequest", LocalDateTime.now());

        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            requestService.addItemRequest(requestItemDto, userId);
        });

        verify(userStorage, times(1)).findById(userId);
        verify(requestStorage, never()).save(any());
    }

    @Test
    void getUserItemRequests_shouldReturnRequestsForUser() {
        Long userId = 1L;
        User user = new User(userId, "testName", "test@example.com");
        Request request1 = new Request(1L, "test1", user, LocalDateTime.now());
        Request request2 = new Request(2L, "test2", user, LocalDateTime.now());

        when(userStorage.findById(userId)).thenReturn(Optional.of(user));
        when(requestStorage.findAllByRequesterId(userId)).thenReturn(List.of(request1, request2));

        List<RequestDto> result = requestService.getUserItemRequests(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(request1.getId(), result.get(0).getId());
        assertEquals(request2.getId(), result.get(1).getId());

        verify(userStorage, times(1)).findById(userId);
        verify(requestStorage, times(1)).findAllByRequesterId(userId);
    }

    @Test
    void getOtherUsersItemRequests_shouldReturnRequestsFromOtherUsers() {
        Long requesterId = 1L;
        User otherUser = new User(2L, "testName", "test@example.com");
        Request request1 = new Request(1L, "test1", otherUser, LocalDateTime.now());
        Request request2 = new Request(2L, "test2", otherUser, LocalDateTime.now());

        when(userStorage.findById(requesterId)).thenReturn(Optional.of(new User()));
        when(requestStorage.findByRequesterIdNotOrderByCreatedDesc(requesterId))
                .thenReturn(List.of(request1, request2));

        List<RequestDto> result = requestService.getOtherUsersItemRequests(requesterId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(request1.getId(), result.get(0).getId());
        assertEquals(request2.getId(), result.get(1).getId());
        assertNotEquals(requesterId, result.get(0).getRequester().getId());

        verify(userStorage, times(1)).findById(requesterId);
        verify(requestStorage, times(1)).findByRequesterIdNotOrderByCreatedDesc(requesterId);
    }

    @Test
    void getItemRequestById_shouldReturnRequestWithItems() {
        Long requestId = 1L;
        Long userId = 1L;
        User requester = new User(userId, "testName", "test@example.com");
        Request request = new Request(requestId, "test1", requester, LocalDateTime.now());
        Item item1 = new Item(1L, requester, "testName", "testDescription", true, request);
        Item item2 = new Item(2L, requester, "testName2", "testDescription2", true, request);

        when(userStorage.findById(userId)).thenReturn(Optional.of(requester));
        when(requestStorage.findById(requestId)).thenReturn(Optional.of(request));
        when(itemStorage.findByRequestId(requestId)).thenReturn(List.of(item1, item2));

        RequestDto result = requestService.getItemRequestById(requestId, userId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertEquals(2, result.getItems().size());

        verify(userStorage, times(1)).findById(userId);
        verify(requestStorage, times(1)).findById(requestId);
        verify(itemStorage, times(1)).findByRequestId(requestId);
    }

    @Test
    void getItemRequestById_shouldThrowNotFoundExceptionWhenRequestNotFound() {
        Long requestId = 99L;
        Long userId = 1L;
        User user = new User(userId, "testName", "test@example.com");

        when(userStorage.findById(userId)).thenReturn(Optional.of(user));
        when(requestStorage.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            requestService.getItemRequestById(requestId, userId);
        });

        verify(userStorage, times(1)).findById(userId);
        verify(requestStorage, times(1)).findById(requestId);
        verify(itemStorage, never()).findByRequestId(any());
    }
}