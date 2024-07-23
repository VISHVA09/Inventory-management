import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.EntityModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.ims.server.itemSummary.ItemSummary;
import com.ims.server.itemSummary.ItemSummaryServiceImpl;
import com.ims.server.itemSummary.ItemSummaryModelAssembler;
import com.ims.server.itemAction.ItemActionRepository;
import com.ims.server.itemAction.ItemAction;
import com.ims.server.item.Item;


class ItemSummaryServiceImplTest {

    @InjectMocks
    private ItemSummaryServiceImpl itemSummaryService;

    @Mock
    private ItemActionRepository itemActionRepository;

    @Mock
    private ItemSummaryModelAssembler itemSummaryModelAssembler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetItemSummary_NoItemActions() {
        when(itemActionRepository.findByCreatedDateBetween(any(), any())).thenReturn(Collections.emptyList());
        when(itemSummaryModelAssembler.toModel(any())).thenReturn(EntityModel.of(new ItemSummary()));

        EntityModel<ItemSummary> result = itemSummaryService.getItemSummary();

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getContent().getDailyProfit());
        assertEquals(0L, result.getContent().getSoldCount());
        assertEquals(0L, result.getContent().getInsertedCount());
    }

    @Test
    void testGetItemSummary_OnlyInsertions() {
        ItemAction action1 = createItemAction(10L, new BigDecimal("100"), new BigDecimal("80"));
        ItemAction action2 = createItemAction(5L, new BigDecimal("50"), new BigDecimal("40"));
        when(itemActionRepository.findByCreatedDateBetween(any(), any())).thenReturn(Arrays.asList(action1, action2));
        when(itemSummaryModelAssembler.toModel(any())).thenAnswer(i -> EntityModel.of(i.getArgument(0)));

        EntityModel<ItemSummary> result = itemSummaryService.getItemSummary();

        assertNotNull(result);
        assertEquals(new BigDecimal("-250"), result.getContent().getDailyProfit());
        assertEquals(0L, result.getContent().getSoldCount());
        assertEquals(15L, result.getContent().getInsertedCount());
    }

    @Test
    void testGetItemSummary_OnlySales() {
        ItemAction action1 = createItemAction(-5L, new BigDecimal("100"), new BigDecimal("80"));
        ItemAction action2 = createItemAction(-3L, new BigDecimal("50"), new BigDecimal("40"));
        when(itemActionRepository.findByCreatedDateBetween(any(), any())).thenReturn(Arrays.asList(action1, action2));
        when(itemSummaryModelAssembler.toModel(any())).thenAnswer(i -> EntityModel.of(i.getArgument(0)));

        EntityModel<ItemSummary> result = itemSummaryService.getItemSummary();

        assertNotNull(result);
        assertEquals(new BigDecimal("130"), result.getContent().getDailyProfit());
        assertEquals(8L, result.getContent().getSoldCount());
        assertEquals(0L, result.getContent().getInsertedCount());
    }

    @Test
    void testGetItemSummary_MixedActions() {
        ItemAction action1 = createItemAction(10L, new BigDecimal("100"), new BigDecimal("80"));
        ItemAction action2 = createItemAction(-5L, new BigDecimal("100"), new BigDecimal("80"));
        when(itemActionRepository.findByCreatedDateBetween(any(), any())).thenReturn(Arrays.asList(action1, action2));
        when(itemSummaryModelAssembler.toModel(any())).thenAnswer(i -> EntityModel.of(i.getArgument(0)));

        EntityModel<ItemSummary> result = itemSummaryService.getItemSummary();

        assertNotNull(result);
        assertEquals(new BigDecimal("-100"), result.getContent().getDailyProfit());
        assertEquals(5L, result.getContent().getSoldCount());
        assertEquals(10L, result.getContent().getInsertedCount());
    }

    @Test
    void testGetItemSummary_NegativeProfit() {
        ItemAction action = createItemAction(-5L, new BigDecimal("80"), new BigDecimal("100"));
        when(itemActionRepository.findByCreatedDateBetween(any(), any())).thenReturn(Collections.singletonList(action));
        when(itemSummaryModelAssembler.toModel(any())).thenAnswer(i -> EntityModel.of(i.getArgument(0)));

        EntityModel<ItemSummary> result = itemSummaryService.getItemSummary();

        assertNotNull(result);
        assertEquals(new BigDecimal("-100"), result.getContent().getDailyProfit());
        assertEquals(5L, result.getContent().getSoldCount());
        assertEquals(0L, result.getContent().getInsertedCount());
    }

    private ItemAction createItemAction(long quantity, BigDecimal price, BigDecimal cost) {
        ItemAction action = new ItemAction();
        action.setQuantity(quantity);
        action.setPrice(price);
        Item item = new Item();
        item.setCost(cost);
        action.setItem(item);
        return action;
    }
}
