
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;

import com.ims.server.exception.ResourceNotFoundException;
import com.ims.server.exception.ResourceUniqueViolationException;
import com.ims.server.item.Item;
import com.ims.server.item.ItemModelAssembler;
import com.ims.server.item.ItemRepository;
import com.ims.server.item.ItemServiceImpl;
import com.ims.server.itemAction.ItemActionRepository;
import java.lang.NullPointerException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import com.ims.server.item.ItemController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;



@RunWith(MockitoJUnitRunner.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemActionRepository itemActionRepository;

    @Mock
    private ItemModelAssembler itemModelAssembler;

    @InjectMocks
    private ItemServiceImpl itemService;

    BigDecimal ten = new BigDecimal("10.0");
    BigDecimal fifteen = new BigDecimal("15.0");
    Long hundred = 100L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetItems() {
        // Mock data
        

        Item item1 = new Item(1L, "001", "Item 1", ten, fifteen, hundred);
        Item item2 = new Item(2L, "002", "Item 2", ten, fifteen, hundred);
        List<Item> itemList = Arrays.asList(item1, item2);

        // Mock behavior
        when(itemRepository.findAll()).thenReturn(itemList);
        when(itemModelAssembler.toModel(item1)).thenReturn(EntityModel.of(item1));
        when(itemModelAssembler.toModel(item2)).thenReturn(EntityModel.of(item2));

        // Invoke method
        CollectionModel<EntityModel<Item>> result = itemService.getItems();

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        // Add more assertions as needed
    }

   @Test
    void testSaveItem() {
        // Arrange
        Item item = new Item(null, "001", "Test Item", BigDecimal.TEN, BigDecimal.valueOf(15), 100L);
        Item savedItem = new Item(1L, "001", "Test Item", BigDecimal.TEN, BigDecimal.valueOf(15), 100L);
        
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);
        
        EntityModel<Item> itemModel = EntityModel.of(savedItem);
        Link selfLink = Link.of("/items/1").withSelfRel();
        itemModel.add(selfLink);
        
        when(itemModelAssembler.toModel(savedItem)).thenReturn(itemModel);

        // Act
        ResponseEntity<?> response = itemService.saveItem(item);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(itemModel, response.getBody());
        assertEquals("/items/1", response.getHeaders().getLocation().toString());

        verify(itemRepository, times(1)).save(any(Item.class));
        verify(itemModelAssembler, times(1)).toModel(savedItem);
    }

    @Test
    void testGetItem() {
        // Mock data
        BigDecimal ten = new BigDecimal("10.0");
        BigDecimal fifteen = new BigDecimal("15.0");
        Long hundred = 100L;
        Long itemId = 1L;
        Item item = new Item(itemId, "001", "Item 1", ten, fifteen, hundred);
        EntityModel<Item> itemModel = EntityModel.of(item);

        // Mock behavior
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemModelAssembler.toModel(item)).thenReturn(itemModel);

        // Invoke method
        EntityModel<Item> result = itemService.getItem(itemId);

        // Assertions
        assertNotNull(result);
        assertEquals(itemId, result.getContent().getId());
        // Add more assertions as needed
    }


    @Test
    void testDeleteItem() {
        // Mock data
        Long itemId = 1L;
        Item item = new Item(itemId, "001", "Item 1", BigDecimal.TEN, BigDecimal.ONE, 100L);

        // Mock behavior
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // Invoke method
        ResponseEntity<?> response = itemService.deleteItem(itemId);

        // Assertions
        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());
        // Add more assertions as needed
    }

    @Test
    void testGetItems_EmptyList() {
        // Mock behavior
        when(itemRepository.findAll()).thenReturn(Collections.emptyList());

        // Invoke method
        CollectionModel<EntityModel<Item>> result = itemService.getItems();

        // Assertions
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
    }

@Test
void testSaveItem_DuplicateItemCode() {
    // Mock data
    BigDecimal cost = new BigDecimal("30.0");
    BigDecimal price = new BigDecimal("35.0");
    Long quantity = 300L;
    Item newItem = new Item(null, "001", "Duplicate Item", cost, price, quantity);
    Item existingItem = new Item(1L, "001", "Existing Item", cost, price, quantity);

    // Mock behavior
    when(itemRepository.findByCode(newItem.getCode())).thenReturn(Optional.of(existingItem));

    // Invoke method and assert exception handling
    ResourceUniqueViolationException exception = assertThrows(ResourceUniqueViolationException.class, 
        () -> itemService.saveItem(newItem));

    // Additional assertions
    assertEquals("Duplicate Resource", exception.getMessage());
    verify(itemRepository, times(1)).findByCode(newItem.getCode());
    verify(itemRepository, never()).save(any(Item.class));
    verify(itemModelAssembler, never()).toModel(any(Item.class));
}

    @Test
    void testGetItem_NonExistingItem() {
        // Mock data
        Long itemId = 1L;

        // Mock behavior
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Invoke method and assert exception handling
        assertThrows(ResourceNotFoundException.class, () -> itemService.getItem(itemId));
    }

@Test
void testReplaceItem_ExistingItem() {
    Long itemId = 1L;
    Item existingItem = new Item(itemId, "001", "Old Item", BigDecimal.TEN, BigDecimal.valueOf(15), 100L);
    Item newItem = new Item(null, "001-updated", "Updated Item", BigDecimal.valueOf(20), BigDecimal.valueOf(25), 200L);
    Item updatedItem = new Item(itemId, "001-updated", "Updated Item", BigDecimal.valueOf(20), BigDecimal.valueOf(25), 200L);

    when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
    when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);
    when(itemModelAssembler.toModel(updatedItem)).thenReturn(EntityModel.of(updatedItem,
            linkTo(methodOn(ItemController.class).getItem(updatedItem.getId())).withSelfRel()));

    ResponseEntity<?> response = itemService.replaceItem(itemId, newItem);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertTrue(response.getHeaders().getLocation().toString().contains("/items/1"));
    EntityModel<Item> responseBody = (EntityModel<Item>) response.getBody();
    assertEquals(updatedItem.getId(), responseBody.getContent().getId());
    assertEquals(newItem.getCode(), responseBody.getContent().getCode());

    verify(itemRepository).findById(itemId);
    verify(itemRepository).save(any(Item.class));
    verify(itemModelAssembler).toModel(updatedItem);
}

@Test
void testReplaceItem_NonExistingItem() {
    Long itemId = 1L;
    Item newItem = new Item(null, "001", "New Item", BigDecimal.TEN, BigDecimal.valueOf(15), 100L);
    Item savedItem = new Item(itemId, "001", "New Item", BigDecimal.TEN, BigDecimal.valueOf(15), 100L);

    when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
    when(itemRepository.save(any(Item.class))).thenReturn(savedItem);
    when(itemModelAssembler.toModel(savedItem)).thenReturn(EntityModel.of(savedItem,
            linkTo(methodOn(ItemController.class).getItem(savedItem.getId())).withSelfRel()));

    ResponseEntity<?> response = itemService.replaceItem(itemId, newItem);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertTrue(response.getHeaders().getLocation().toString().contains("/items/1"));
    EntityModel<Item> responseBody = (EntityModel<Item>) response.getBody();
    assertEquals(savedItem.getId(), responseBody.getContent().getId());
    assertEquals(newItem.getCode(), responseBody.getContent().getCode());

    verify(itemRepository).findById(itemId);
    verify(itemRepository).save(any(Item.class));
    verify(itemModelAssembler).toModel(savedItem);
}

@Test
void testReplaceItem_NullNewItem() {
    Long itemId = 1L;
    Item existingItem = new Item(itemId, "001", "Existing Item", BigDecimal.TEN, BigDecimal.valueOf(15), 100L);

    when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

    assertThrows(NullPointerException.class, () -> itemService.replaceItem(itemId, null));

    verify(itemRepository).findById(itemId);
    verify(itemRepository, never()).save(any(Item.class));
    verify(itemModelAssembler, never()).toModel(any(Item.class));
}

@Test
void testReplaceItem_PartialUpdate() {
    Long itemId = 1L;
    Item existingItem = new Item(itemId, "001", "Old Item", BigDecimal.TEN, BigDecimal.valueOf(15), 100L);
    Item partialItem = new Item(null, null, "Updated Item", null, BigDecimal.valueOf(20), 100L);
    Item expectedUpdatedItem = new Item(itemId, null, "Updated Item", null, BigDecimal.valueOf(20), 100L);

    when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
    when(itemRepository.save(any(Item.class))).thenReturn(expectedUpdatedItem);
    when(itemModelAssembler.toModel(expectedUpdatedItem)).thenReturn(EntityModel.of(expectedUpdatedItem,
            linkTo(methodOn(ItemController.class).getItem(expectedUpdatedItem.getId())).withSelfRel()));

    ResponseEntity<?> response = itemService.replaceItem(itemId, partialItem);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    EntityModel<Item> responseBody = (EntityModel<Item>) response.getBody();
    assertEquals(expectedUpdatedItem.getId(), responseBody.getContent().getId());
    assertEquals(expectedUpdatedItem.getName(), responseBody.getContent().getName());
    assertEquals(expectedUpdatedItem.getPrice(), responseBody.getContent().getPrice());
    assertEquals(existingItem.getCode(), responseBody.getContent().getCode());
    assertEquals(existingItem.getCost(), responseBody.getContent().getCost());
    assertEquals(existingItem.getQuantity(), responseBody.getContent().getQuantity());

    verify(itemRepository).findById(itemId);
    verify(itemRepository).save(any(Item.class));
    verify(itemModelAssembler).toModel(expectedUpdatedItem);
}

@Test
void testReplaceItem_InvalidItemId() {
    Long invalidItemId = -1L;
    Item newItem = new Item(null, "001", "New Item", BigDecimal.TEN, BigDecimal.valueOf(15), 100L);

    when(itemRepository.findById(invalidItemId)).thenReturn(Optional.empty());
    when(itemRepository.save(any(Item.class))).thenThrow(new IllegalArgumentException("Invalid item ID"));

    assertThrows(IllegalArgumentException.class, () -> itemService.replaceItem(invalidItemId, newItem));

    verify(itemRepository).findById(invalidItemId);
    verify(itemRepository).save(any(Item.class));
    verify(itemModelAssembler, never()).toModel(any(Item.class));
}

@Test
void testReplaceItem_DuplicateItemCode() {
    Long itemId = 1L;
    Item existingItem = new Item(itemId, "001", "Existing Item", BigDecimal.TEN, BigDecimal.valueOf(15), 100L);
    Item newItem = new Item(null, "002", "New Item", BigDecimal.valueOf(20), BigDecimal.valueOf(25), 200L);

    when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
    when(itemRepository.save(any(Item.class))).thenThrow(new DataIntegrityViolationException("Duplicate item code"));

    assertThrows(DataIntegrityViolationException.class, () -> itemService.replaceItem(itemId, newItem));

    verify(itemRepository).findById(itemId);
    verify(itemRepository).save(any(Item.class));
    verify(itemModelAssembler, never()).toModel(any(Item.class));
}

}
