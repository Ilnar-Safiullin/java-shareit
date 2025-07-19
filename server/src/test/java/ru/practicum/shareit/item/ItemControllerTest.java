package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.RequestCommentDto;
import ru.practicum.shareit.item.dto.ItemBodyDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void addItem() throws Exception {
        ItemBodyDto itemBodyDto = new ItemBodyDto("Item Name", "Item Description", true, null);
        ItemDto itemDto = new ItemDto(1L, "Item Name", "Item Description", true, null, null, Collections.emptyList(), null);

        when(itemService.add(anyLong(), ArgumentMatchers.any())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemBodyDto))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Item Name"));

        verify(itemService, times(1)).add(anyLong(), any());
    }

    @Test
    void updateItem() throws Exception {
        long itemId = 1L;
        ItemBodyDto itemBodyDto = new ItemBodyDto("Item Name", "Item Description", true, null);
        ItemDto itemDto = new ItemDto(itemId, "Updated Item Name", "Updated Item Description", true, null, null, Collections.emptyList(), null);

        when(itemService.update(eq(itemId), ArgumentMatchers.any(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemsId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemBodyDto))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Updated Item Name"));

        verify(itemService, times(1)).update(eq(itemId), any(), anyLong());
    }

    @Test
    void getItemById() throws Exception {
        long itemId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "Item Name", "Item Description", true, null, null, Collections.emptyList(), null);

        when(itemService.getById(itemId)).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemsId}", itemId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Item Name"));

        verify(itemService, times(1)).getById(itemId);
    }

    @Test
    void getItemsByOwner() throws Exception {
        long userId = 1L;
        ItemDto itemDto = new ItemDto(1L, "Item Name", "Item Description", true, null, null, Collections.emptyList(), null);

        when(itemService.getItemsByOwner(userId)).thenReturn(Collections.singletonList(itemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Item Name"));

        verify(itemService, times(1)).getItemsByOwner(userId);
    }

    @Test
    void searchItems() throws Exception {
        long userId = 1L;
        String searchText = "search text";
        ItemDto itemDto = new ItemDto(1L, "Item Name", "Item Description", true, null, null, Collections.emptyList(), null);

        when(itemService.search(searchText)).thenReturn(Collections.singletonList(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", searchText)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Item Name"));

        verify(itemService, times(1)).search(searchText);
    }

    @Test
    void addComment() throws Exception {
        long itemId = 1L;
        RequestCommentDto requestCommentDto = new RequestCommentDto("Great item!");
        CommentDto commentDto = new CommentDto(1L, "text", "author", null);

        when(itemService.addComment(eq(itemId), ArgumentMatchers.any(), anyLong())).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCommentDto))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("text"));

        verify(itemService, times(1)).addComment(eq(itemId), any(), anyLong());
    }
}