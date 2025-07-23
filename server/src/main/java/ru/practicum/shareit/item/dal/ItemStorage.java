package ru.practicum.shareit.item.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemStorage extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i " +
            "JOIN FETCH i.owner " +
            "WHERE i.owner.id = :ownerId")
    List<Item> findByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT i FROM Item i WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))) AND i.available = true")
    List<Item> searchByText(@Param("text") String text);

    List<Item> findByRequestId(Long requsetId);
}