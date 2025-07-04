package org.pancakelab.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pancakelab.model.Order;
import org.pancakelab.model.OrderStatus;
import org.pancakelab.repository.OrderRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepo;

    @InjectMocks
    private OrderService orderService;

    private UUID testOrderId;
    @Mock
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrderId = UUID.randomUUID();
    }

    @Test
    void GivenValidBuildingAndRoom_WhenCreateOrder_ThenOrderIsCreatedAndSaved() {
        int building = 1;
        int room = 101;
        Order createdOrder = orderService.createOrder(building, room);
        assertNotNull(createdOrder);
        assertEquals(building, createdOrder.getBuilding());
        assertEquals(room, createdOrder.getRoom());
        verify(orderRepo, times(1)).save(createdOrder);
    }

    @Test
    void GivenInvalidRoom_WhenCreateOrder_ThenIllegalArgumentExceptionIsThrown() {
        int building = 1;
        int room = -5;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(building, room);
        });
        assertEquals("Room %d does not exist in building %d.".formatted(room, building), exception.getMessage());
        verify(orderRepo, never()).save(any(Order.class));
    }

    @Test
    void GivenOrderId_WhenCompleteOrder_ThenOrderIsMarkedCompleted() {
        when(testOrder.getId()).thenReturn(testOrderId);
        when(testOrder.getStatus()).thenReturn(OrderStatus.PREPARED);
        when(orderRepo.findAll()).thenReturn(Collections.singletonList(testOrder));
        doNothing().when(orderRepo).markCompleted(any(Order.class));
        orderService.completeOrder(testOrderId);
        verify(orderRepo, times(1)).markCompleted(testOrder);
    }

    @Test
    void WhenListCompletedOrders_ThenCompletedOrdersAreReturned() {
        Set<UUID> expectedCompletedOrders = new HashSet<>(Arrays.asList(UUID.randomUUID(), testOrderId));
        when(orderRepo.getCompletedOrders()).thenReturn(expectedCompletedOrders);
        Set<UUID> actualCompletedOrders = orderService.listCompletedOrders();
        verify(orderRepo, times(1)).getCompletedOrders();
        assertEquals(expectedCompletedOrders, actualCompletedOrders);
    }

    @Test
    void WhenListPreparedOrders_ThenPreparedOrdersAreReturned() {
        Set<UUID> expectedPreparedOrders = new HashSet<>(Arrays.asList(UUID.randomUUID(), testOrderId));
        when(orderRepo.getPreparedOrders()).thenReturn(expectedPreparedOrders);
        Set<UUID> actualPreparedOrders = orderService.listPreparedOrders();
        verify(orderRepo, times(1)).getPreparedOrders();
        assertEquals(expectedPreparedOrders, actualPreparedOrders);
    }

    @Test
    void WhenGetOrders_ThenAllOrdersAreReturned() {
        Map<UUID, Order> expectedOrders = new HashMap<>();
        expectedOrders.put(testOrderId, testOrder);
        expectedOrders.put(UUID.randomUUID(), mock(Order.class));
        when(orderRepo.getOrders()).thenReturn(expectedOrders);
        Map<UUID, Order> actualOrders = orderService.getOrders();
        verify(orderRepo, times(1)).getOrders();
        assertEquals(expectedOrders, actualOrders);
    }

    @Test
    void GivenOrderId_WhenPrepareOrder_ThenOrderIsMarkedPrepared() {
        when(testOrder.getId()).thenReturn(testOrderId);
        when(orderRepo.findAll()).thenReturn(Collections.singletonList(testOrder));
        orderService.prepareOrder(testOrderId);
        verify(orderRepo, times(1)).markPrepared(testOrderId);
    }

    @Test
    void GivenPreparedOrder_WhenIsPrepared_ThenReturnsTrue() {
        when(orderRepo.isPrepared(testOrderId)).thenReturn(true);
        boolean isPrepared = orderService.isPrepared(testOrderId);
        verify(orderRepo, times(1)).isPrepared(testOrderId);
        assertTrue(isPrepared);
    }

    @Test
    void GivenUnpreparedOrder_WhenIsPrepared_ThenReturnsFalse() {
        when(orderRepo.isPrepared(testOrderId)).thenReturn(false);
        boolean isPrepared = orderService.isPrepared(testOrderId);
        verify(orderRepo, times(1)).isPrepared(testOrderId);
        assertFalse(isPrepared);
    }

    @Test
    void GivenExistingOrder_WhenGetOrder_ThenOrderIsReturned() {
        when(orderRepo.findById(testOrderId)).thenReturn(testOrder);
        Optional<Order> actualOrder = orderService.getOrder(testOrderId);
        assertTrue(actualOrder.isPresent());
        assertEquals(testOrder, actualOrder.get());
    }

    @Test
    void GivenOrderId_WhenRemoveOrder_ThenOrderIsDeleted() {
        orderService.removeOrder(testOrderId);
        verify(orderRepo, times(1)).delete(testOrderId);
    }

    @Test
    void GivenInvalidBuilding_WhenCreateOrder_ThenIllegalArgumentExceptionIsThrown() {
        int building = -1;
        int room = 20;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(building, room);
        });
        assertEquals("Building -1 does not exist.", exception.getMessage());
        verify(orderRepo, never()).save(any(Order.class));
    }

    @Test
    void GivenNullOrderId_WhenCompleteOrder_ThenIllegalArgumentExceptionIsThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.completeOrder(null);
        });
        assertEquals("The provided order ID can not be null.", exception.getMessage());
        verifyNoInteractions(orderRepo);
    }

    @Test
    void GivenNonExistentOrderId_WhenCompleteOrder_ThenIllegalArgumentExceptionIsThrown() {
        when(orderRepo.findAll()).thenReturn(Collections.emptyList());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.completeOrder(UUID.randomUUID());
        });
        assertEquals("The provided order ID " + exception.getMessage().substring(exception.getMessage().indexOf("ID ") + 3), exception.getMessage());
        verify(orderRepo, times(1)).findAll();
        verify(orderRepo, never()).markCompleted(any(UUID.class));
    }

    @Test
    void GivenAlreadyCompletedOrder_WhenCompleteOrder_ThenIllegalStateExceptionIsThrown() {
        when(testOrder.getId()).thenReturn(testOrderId);
        when(testOrder.getStatus()).thenReturn(OrderStatus.COMPLETED);
        when(orderRepo.findAll()).thenReturn(Collections.singletonList(testOrder));
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderService.completeOrder(testOrderId);
        });
        assertEquals("The provided order ID " + testOrderId + " is already completed.", exception.getMessage());
        verify(orderRepo, times(1)).findAll();
    }

    @Test
    void GivenNullOrderId_WhenPrepareOrder_ThenIllegalArgumentExceptionIsThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.prepareOrder(null);
        });
        assertEquals("The provided order ID can not be null.", exception.getMessage());
        verifyNoInteractions(orderRepo);
    }

    @Test
    void GivenNonExistentOrderId_WhenPrepareOrder_ThenIllegalArgumentExceptionIsThrown() {
        when(orderRepo.findAll()).thenReturn(Collections.emptyList());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.prepareOrder(UUID.randomUUID());
        });
        assertEquals("The provided order ID " + exception.getMessage().substring(exception.getMessage().indexOf("ID ") + 3), exception.getMessage());
        verify(orderRepo, times(1)).findAll();
        verify(orderRepo, never()).markPrepared(any(UUID.class));
    }

    @Test
    void GivenNullOrderId_WhenIsPrepared_ThenIllegalArgumentExceptionIsThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.isPrepared(null);
        });
        assertEquals("The provided order ID can not be null.", exception.getMessage());
        verifyNoInteractions(orderRepo);
    }

    @Test
    void GivenNullOrderId_WhenGetOrder_ThenIllegalArgumentExceptionIsThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.getOrder(null);
        });
        assertEquals("The provided order ID can not be null.", exception.getMessage());
        verifyNoInteractions(orderRepo);
    }

    @Test
    void GivenNullOrderId_WhenRemoveOrder_ThenIllegalArgumentExceptionIsThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.removeOrder(null);
        });
        assertEquals("The provided order ID can not be null.", exception.getMessage());
        verifyNoInteractions(orderRepo);
    }
}
