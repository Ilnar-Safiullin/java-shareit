package gateway.client;

import gateway.dto.CommentDto;
import gateway.dto.ItemBodyDto;
import gateway.dto.ItemDto;
import gateway.dto.RequestCommentDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class ItemClient extends BaseClient {
    private final String serverStringUrl = "http://localhost:9090/items";//не могу сделать заглавными ругается чекСтайл. И статик нельзя наверное, он же во всех классах есть


    protected ItemClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<ItemDto> add(ItemBodyDto itemBodyDto, long userId) {
        return sendRequest(serverStringUrl, HttpMethod.POST, itemBodyDto, ItemDto.class, userId);
    }

    public ResponseEntity<ItemDto> update(long itemsId, ItemBodyDto itemBodyDto, Long userId) {
        String url = serverStringUrl + "/" + itemsId;
        return sendRequest(url, HttpMethod.PATCH, itemBodyDto, ItemDto.class, userId);
    }

    public ResponseEntity<ItemDto> getById(long itemsId) {
        String url = serverStringUrl + "/" + itemsId;
        return sendRequest(url, HttpMethod.GET, null, ItemDto.class, null);
    }

    public ResponseEntity<Collection<ItemDto>> getItemsByOwner(Long userId) {
        String url = serverStringUrl + "?ownerId=" + userId;
        return sendRequest(url, HttpMethod.GET, null, new ParameterizedTypeReference<Collection<ItemDto>>() {}, userId);
    }

    public ResponseEntity<List<ItemDto>> search(String text, Long userId) {
        String url = serverStringUrl + "/search?text=" + text;
        return sendRequest(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<ItemDto>>() {}, userId);
    }

    public ResponseEntity<CommentDto> addComment(Long itemId, RequestCommentDto requestCommentDto, Long userId) {
        String url = serverStringUrl + "/" + itemId + "/comment";
        return sendRequest(url, HttpMethod.POST, requestCommentDto, CommentDto.class, userId);
    }

}