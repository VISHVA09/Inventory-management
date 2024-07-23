

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.hateoas.IanaLinkRelations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import com.ims.server.item.Item;
import com.ims.server.itemAction.ItemActionServiceImpl;
import com.ims.server.item.ItemRepository;
import com.ims.server.itemAction.ItemAction;
import com.ims.server.itemAction.ItemActionModelAssembler;
import com.ims.server.itemAction.ItemActionRepository;
import com.ims.server.exception.ResourceNotFoundException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.hateoas.Link;
import java.time.LocalDateTime;
import org.mockito.Mock;

@RunWith(MockitoJUnitRunner.class)
public class ItemActionServiceImplTest {

    private ItemRepository itemRepository;

    private ItemActionRepository itemActionRepository;

    private ItemActionModelAssembler itemActionModelAssembler;

    private ItemActionServiceImpl itemActionService;


    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        itemActionRepository = mock(ItemActionRepository.class);
        itemActionModelAssembler = mock(ItemActionModelAssembler.class);
        itemActionService = new ItemActionServiceImpl(itemRepository, itemActionRepository, itemActionModelAssembler);
    }

    // Test 1: getItemActions when item exists and has actions
    @Test
    void testGetItemActions_ItemExistsAndHasActions() {
        // Mock data
        Long itemId = 1L;
        Item item = new Item(itemId, "Test Item", 10L);
        List<ItemAction> itemActionsList = new ArrayList<>();
        ItemAction itemAction = new ItemAction(1L, BigDecimal.TEN, 5L, item);
        itemActionsList.add(itemAction);

        // Mock behavior
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemActionRepository.findByItemId(itemId)).thenReturn( itemActionsList);
        when(itemActionModelAssembler.toModel(itemAction)).thenReturn(EntityModel.of(itemAction));

        // Invoke method and assert
        CollectionModel<EntityModel<ItemAction>> response = itemActionService.getItemActions(itemId);
        List<EntityModel<ItemAction>> items = new ArrayList(response.getContent());

        assertNotNull(items);
        assertEquals(itemActionsList.size(), items.size());
    }

    // Test 2: getItemActions when item does not exist
    @Test
    void testGetItemActions_ItemDoesNotExist() {
        // Mock data
        Long itemId = 1L;

        // Mock behavior
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Invoke method and assert exception handling
        assertThrows(ResourceNotFoundException.class, () -> {
            itemActionService.getItemActions(itemId);
        });
    }

    @Test
    void testSaveItemAction_ItemDoesNotExist() {
        // Mock data
        Long itemId = 1L;
        ItemAction itemAction = new ItemAction(1L, BigDecimal.TEN, 5L, null); // No item associated

        // Mock behavior
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Invoke method and assert exception handling
        assertThrows(ResourceNotFoundException.class, () -> {
            itemActionService.saveItemAction(itemId, itemAction);
        });
    }

    @Test
    void testGetItemAction_ValidItemAndActionIds() {
        // Mock data
        Long itemId = 1L;
        Long itemActionId = 1L;
        Item item = new Item(itemId, "Test Item", 10L);
        ItemAction itemAction = new ItemAction(itemActionId, BigDecimal.TEN, 5L, item);

        // Mock behavior
        when(itemActionRepository.findByIdAndItemId(itemActionId, itemId)).thenReturn(Optional.of(itemAction));
        when(itemActionModelAssembler.toModel(itemAction)).thenReturn(EntityModel.of(itemAction));

        // Invoke method and assert
        EntityModel<ItemAction> response = itemActionService.getItemAction(itemId, itemActionId);

        assertNotNull(response);
        assertEquals(itemActionId, response.getContent().getId());
    }

    // Test : getItemAction with invalid itemId or itemActionId
    @Test
    void testGetItemAction_ItemOrActionNotFound() {
        // Mock data
        Long itemId = 1L;
        Long itemActionId = 1L;

        // Mock behavior
        when(itemActionRepository.findByIdAndItemId(itemActionId, itemId)).thenReturn(Optional.empty());

        // Invoke method and assert exception handling
        assertThrows(ResourceNotFoundException.class, () -> {
            itemActionService.getItemAction(itemId, itemActionId);
        });
    }

    // Test : deleteItemAction with valid itemId and itemActionId
    @Test
    void testDeleteItemAction_ValidItemAndActionIds() {
        // Mock data
        Long itemId = 1L;
        Long itemActionId = 1L;
        Item item = new Item(itemId, "Test Item", 10L);
        ItemAction itemAction = new ItemAction(itemActionId, BigDecimal.TEN, 5L, item);

        // Mock behavior
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemActionRepository.findByIdAndItemId(itemActionId, itemId)).thenReturn(Optional.of(itemAction));

        // Invoke method and assert
        ResponseEntity<?> response = itemActionService.deleteItemAction(itemId, itemActionId);

        assertNull(response.getBody());
        assertEquals(204, response.getStatusCodeValue());
    }

    // Test : deleteItemAction with invalid itemId, itemActionId, or itemAction not found
    @Test
    void testDeleteItemAction_ItemOrActionNotFound() {
        // Mock data
        Long itemId = 1L;
        Long itemActionId = 1L;

        // Mock behavior
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Invoke method and assert exception handling
        assertThrows(ResourceNotFoundException.class, () -> {
            itemActionService.deleteItemAction(itemId, itemActionId);
        });
    }

@Test
void saveItemAction_ValidItemIdAndItemAction_ReturnsCreatedResponse() {
        // Arrange
        Long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        ItemAction itemAction = new ItemAction();
        ItemAction savedItemAction = new ItemAction();
        savedItemAction.setId(1L);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemActionRepository.save(any(ItemAction.class))).thenReturn(savedItemAction);

        Link selfLink = Link.of("/api/items/1/itemActions/1").withSelfRel();
        when(itemActionModelAssembler.toModel(savedItemAction)).thenReturn(EntityModel.of(savedItemAction, selfLink));

        // Act
        ResponseEntity<?> response = itemActionService.saveItemAction(itemId, itemAction);

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof EntityModel);
}

@Test
void saveItemAction_ItemNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        Long itemId = 1L;
        ItemAction itemAction = new ItemAction();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> itemActionService.saveItemAction(itemId, itemAction));
}

@Test
    void replaceItemAction_ExistingItemButNewItemAction_ReturnsSavedResponse() {
        // Arrange
        Long itemId = 1L;
        Long itemActionId = 2L;
        Item item = new Item();
        item.setId(itemId);
        ItemAction newItemAction = new ItemAction();
        ItemAction savedItemAction = new ItemAction();
        savedItemAction.setId(itemActionId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemActionRepository.findByIdAndItemId(itemActionId, itemId)).thenReturn(Optional.empty());
        when(itemActionRepository.save(any(ItemAction.class))).thenReturn(savedItemAction);

        Link selfLink = Link.of("/api/items/1/itemActions/2").withSelfRel();
        when(itemActionModelAssembler.toModel(savedItemAction)).thenReturn(EntityModel.of(savedItemAction, selfLink));

        // Act
        ResponseEntity<?> response = itemActionService.replaceItemAction(itemId, itemActionId, newItemAction);

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof EntityModel);
    }

    @Test
    void replaceItemAction_ItemNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        Long itemId = 1L;
        Long itemActionId = 1L;
        ItemAction newItemAction = new ItemAction();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            itemActionService.replaceItemAction(itemId, itemActionId, newItemAction));
    }

    @Test
    void replaceItemAction_ValidInput_SetsCorrectLinks() {
        // Arrange
        Long itemId = 1L;
        Long itemActionId = 1L;
        Item item = new Item();
        item.setId(itemId);
        ItemAction existingItemAction = new ItemAction();
        existingItemAction.setId(itemActionId);
        ItemAction newItemAction = new ItemAction();
        ItemAction updatedItemAction = new ItemAction();
        updatedItemAction.setId(itemActionId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemActionRepository.findByIdAndItemId(itemActionId, itemId)).thenReturn(Optional.of(existingItemAction));
        when(itemActionRepository.save(any(ItemAction.class))).thenReturn(updatedItemAction);

        Link selfLink = Link.of("/api/items/1/itemActions/1").withSelfRel();
        Link itemsLink = Link.of("/api/items").withRel("items");
        Link itemActionsLink = Link.of("/api/items/1/itemActions").withRel("itemActions");

        EntityModel<ItemAction> entityModel = EntityModel.of(updatedItemAction, selfLink, itemsLink, itemActionsLink);
        when(itemActionModelAssembler.toModel(updatedItemAction)).thenReturn(entityModel);

        // Act
        ResponseEntity<?> response = itemActionService.replaceItemAction(itemId, itemActionId, newItemAction);

        // Assert
        assertTrue(response.getBody() instanceof EntityModel);
        EntityModel<?> responseBody = (EntityModel<?>) response.getBody();
        assertTrue(responseBody.getLinks().hasLink(IanaLinkRelations.SELF));
        assertTrue(responseBody.getLinks().hasLink("items"));
        assertTrue(responseBody.getLinks().hasLink("itemActions"));
    }

}

