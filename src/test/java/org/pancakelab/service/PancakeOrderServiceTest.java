package org.pancakelab.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pancakelab.factory.PancakeFactory;
import org.pancakelab.model.Order;
import org.pancakelab.model.OrderStatus;
import org.pancakelab.model.pancakes.PancakeRecipe;
import org.pancakelab.repository.OrderRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PancakeOrderServiceTest {

    @Mock
    private PancakeService pancakeService;
    @Mock
    private OrderService orderService;
    @Mock
    private OrderRepository orderRepo;

    @InjectMocks
    private PancakeOrderService pancakeOrderService;

    private UUID testOrderId;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrderId = UUID.randomUUID();
        testOrder = new Order(1, 1);
    }

    @Test
    void GivenPancakesInOrder_WhenRemovePancakesFromOrder_ThenPancakesAreRemovedAndLogged() {
        String description = "Chocolate Pancake";
        int count = 2;
        PancakeRecipe mockPancake1 = mock(PancakeRecipe.class);
        PancakeRecipe mockPancake2 = mock(PancakeRecipe.class);
        List<PancakeRecipe> removedPancakes = Arrays.asList(mockPancake1, mockPancake2);

        when(pancakeService.removePancakes(testOrderId, description, count)).thenReturn(removedPancakes);
        when(orderService.getOrder(testOrderId)).thenReturn(Optional.of(testOrder));

        pancakeOrderService.removePancakesFromOrder(testOrderId, description, count);

        verify(pancakeService, times(1)).removePancakes(testOrderId, description, count);
        verify(orderService, times(1)).getOrder(testOrderId);
    }

    @Test
    void GivenOrderDoesNotExist_WhenRemovePancakesFromOrder_ThenNoSuchElementExceptionIsThrown() {
        when(orderService.getOrder(testOrderId)).thenReturn(Optional.empty());
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            pancakeOrderService.removePancakesFromOrder(testOrderId,"Any Pancake",  1);
        });
        assertEquals("Order with ID " + testOrderId + " not found after pancake removal attempt.", exception.getMessage());
        verify(pancakeService, times(1)).removePancakes(any(UUID.class), anyString(), anyInt());
    }


    @Test
    void GivenExistingOrder_WhenCancelOrder_ThenOrderAndPancakesAreRemovedAndLogged() {
        when(orderService.getOrder(testOrderId)).thenReturn(Optional.of(testOrder));
        when(pancakeService.getPancakes()).thenReturn(Collections.emptyList());
        Map<UUID, Order> ordersMap = new HashMap<>();

        ordersMap.put(testOrderId, testOrder);

        pancakeOrderService.cancelOrder(testOrderId);

        verify(orderService, times(1)).getOrder(testOrderId);
        verify(pancakeService, times(1)).removePancakesForOrder(testOrderId);

    }

    @Test
    void GivenOrderDoesNotExist_WhenCancelOrder_ThenNoSuchElementExceptionIsThrown() {
        when(orderService.getOrder(testOrderId)).thenReturn(Optional.empty());
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            pancakeOrderService.cancelOrder(testOrderId);
        });
        assertEquals("Order with ID " + testOrderId + " not found when cancelling order.", exception.getMessage());
        verify(pancakeService, never()).removePancakesForOrder(any(UUID.class));
        verify(orderService, never()).listCompletedOrders();
    }

    @Test
    void GivenPancakesInOrder_WhenViewOrder_ThenDescriptionsAreReturned() {
        PancakeRecipe pancake1 = mock(PancakeRecipe.class);
        when(pancake1.getOrderId()).thenReturn(testOrderId);
        when(pancake1.description()).thenReturn("Chocolate Pancake");
        PancakeRecipe pancake2 = mock(PancakeRecipe.class);
        when(pancake2.getOrderId()).thenReturn(testOrderId);
        when(pancake2.description()).thenReturn("Strawberry Pancake");
        List<PancakeRecipe> allPancakes = Arrays.asList(pancake1, pancake2, mock(PancakeRecipe.class));
        when(pancakeService.getPancakes()).thenReturn(allPancakes);
        List<String> actualDescriptions = pancakeOrderService.viewOrder(testOrderId);
        verify(pancakeService, times(1)).getPancakes();
        List<String> expectedDescriptions = Arrays.asList("Chocolate Pancake", "Strawberry Pancake");
        assertEquals(expectedDescriptions, actualDescriptions);
    }

    @Test
    void GivenNoPancakesInOrder_WhenViewOrder_ThenEmptyListIsReturned() {
        when(pancakeService.getPancakes()).thenReturn(Collections.emptyList());
        List<String> actualDescriptions = pancakeOrderService.viewOrder(testOrderId);
        verify(pancakeService, times(1)).getPancakes();
        assertTrue(actualDescriptions.isEmpty());
    }

    @Test
    void GivenExistingOrder_WhenAddPancakesToOrder_ThenPancakesAreAddedAndLogged() {
        List<String> ingredients = Arrays.asList("flour", "sugar");
        int count = 3;
        when(orderService.getOrder(testOrderId)).thenReturn(Optional.of(testOrder));
        PancakeRecipe mockPancake = mock(PancakeRecipe.class);
        when(mockPancake.description()).thenReturn("Simple Pancake");
        when(pancakeService.getPancakes()).thenReturn(Collections.singletonList(mockPancake));
        try (MockedStatic<PancakeFactory> mockedPancakeFactory = mockStatic(PancakeFactory.class);
             MockedStatic<OrderLog> mockedOrderLog = mockStatic(OrderLog.class)) {
            mockedPancakeFactory.when(() -> PancakeFactory.createPancake(ingredients)).thenReturn(mockPancake);
            pancakeOrderService.addPancakesToOrder(testOrderId, ingredients, count);
            verify(orderService, times(1)).getOrder(testOrderId);
            mockedPancakeFactory.verify(() -> PancakeFactory.createPancake(ingredients), times(count));
            verify(mockPancake, times(count)).setOrderId(testOrderId);
            verify(pancakeService, times(count)).addPancake(mockPancake);
            mockedOrderLog.verify(() -> OrderLog.logAddPancake(eq(testOrder), eq("Simple Pancake"), anyList()), times(count));
        }
    }

    @Test
    void GivenOrderDoesNotExist_WhenAddPancakesToOrder_ThenIllegalArgumentExceptionIsThrown() {
        when(orderService.getOrder(testOrderId)).thenReturn(Optional.empty());
        try (MockedStatic<PancakeFactory> mockedPancakeFactory = mockStatic(PancakeFactory.class);
             MockedStatic<OrderLog> mockedOrderLog = mockStatic(OrderLog.class)) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                pancakeOrderService.addPancakesToOrder(testOrderId, Arrays.asList("flour"), 1);
            });
            assertEquals("Order not found", exception.getMessage());
            mockedPancakeFactory.verify(() -> PancakeFactory.createPancake(anyList()), never());
            verify(pancakeService, never()).addPancake(any(PancakeRecipe.class));
            mockedOrderLog.verify(() -> OrderLog.logAddPancake(any(Order.class), anyString(), anyList()), never());
        }
    }

    @Test
    void GivenPreparedOrder_WhenDeliverOrder_ThenOrderAndPancakesAreReturnedAndRemovedAndLogged() {
        when(orderService.getOrder(testOrderId)).thenReturn(Optional.of(testOrder));
        testOrder.setStatus(OrderStatus.PREPARED);
        List<String> pancakesToDeliver = Arrays.asList("Honey Pancake", "Blueberries Pancake");
        when(pancakeService.getPancakeDescriptionsForOrder(testOrderId)).thenReturn(pancakesToDeliver);
        when(pancakeService.getPancakes()).thenReturn(Collections.emptyList());
        Object[] result = pancakeOrderService.deliverOrder(testOrderId);
        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals(testOrder, result[0]);
        assertEquals(pancakesToDeliver, result[1]);
        verify(orderService, times(1)).getOrder(testOrderId);
        verify(pancakeService, times(1)).getPancakeDescriptionsForOrder(testOrderId);
        verify(pancakeService, times(1)).removePancakesForOrder(testOrderId);
        verify(orderService, times(1)).removeOrder(testOrderId);

    }

    @Test
    void GivenUnpreparedOrder_WhenDeliverOrder_ThenThrowsIllegalStateException() {
        when(orderService.getOrder(testOrderId)).thenReturn(Optional.of(testOrder));
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            pancakeOrderService.deliverOrder(testOrderId);
        });
        verify(orderService, times(1)).getOrder(testOrderId);
        verify(pancakeService, never()).getPancakeDescriptionsForOrder(any(UUID.class));
        verify(pancakeService, never()).removePancakesForOrder(any(UUID.class));
        verify(orderService, never()).removeOrder(any(UUID.class));
    }

    @Test
    void GivenPreparedOrderDoesNotExist_WhenDeliverOrder_ThenNoSuchElementExceptionIsThrown() {
        when(orderService.getOrder(testOrderId)).thenReturn(Optional.empty());
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            pancakeOrderService.deliverOrder(testOrderId);
        });
        assertEquals("Order with ID " + testOrderId + " not found when delivering order.", exception.getMessage());
        verify(orderService, times(1)).getOrder(testOrderId);
    }
}
