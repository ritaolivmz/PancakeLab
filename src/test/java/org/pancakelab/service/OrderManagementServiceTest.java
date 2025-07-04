package org.pancakelab.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pancakelab.model.Order;
import org.pancakelab.model.pancakes.PancakeRecipe;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderManagementServiceTest {

    @Mock
    private PancakeService pancakeService;
    @Mock
    private OrderService orderService;
    @Mock
    private PancakeOrderService pancakeOrderService;

    @InjectMocks
    private OrderManagementService orderManagementService;

    private UUID testOrderId;
    @Mock
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrderId = UUID.randomUUID();
    }

    @Test
    void GivenValidInput_WhenRemovePancakesFromOrder_ThenPancakeOrderServiceIsCalled() {
        String description = "chocolate";
        int count = 1;
        orderManagementService.removePancakesFromOrder(testOrderId, description, count);
        verify(pancakeOrderService, times(count)).removePancakesFromOrder(testOrderId, description, count);
    }

    @Test
    void GivenOrderId_WhenViewOrder_ThenPancakeOrderServiceIsCalledAndDescriptionsReturned() {
        List<String> expectedDescriptions = Arrays.asList("chocolate", "banana honey");
        when(pancakeOrderService.viewOrder(testOrderId)).thenReturn(expectedDescriptions);
        List<String> actualDescriptions = orderManagementService.viewOrder(testOrderId);
        verify(pancakeOrderService, times(1)).viewOrder(testOrderId);
        assertEquals(expectedDescriptions, actualDescriptions);
    }

    @Test
    void GivenValidInput_WhenAddPancakesToOrder_ThenPancakeOrderServiceIsCalled() {
        List<String> ingredients = Arrays.asList("flour", "sugar");
        int count = 2;
        orderManagementService.addPancakesToOrder(testOrderId, ingredients, count);
        verify(pancakeOrderService, times(1)).addPancakesToOrder(testOrderId, ingredients, count);
    }

    @Test
    void GivenOrderId_WhenDeliverOrder_ThenPancakeOrderServiceIsCalledAndResultReturned() {
        Object[] expectedResult = new Object[]{testOrder, Arrays.asList("Pancake 1")};
        when(pancakeOrderService.deliverOrder(testOrderId)).thenReturn(expectedResult);
        Object[] actualResult = orderManagementService.deliverOrder(testOrderId);
        verify(pancakeOrderService, times(1)).deliverOrder(testOrderId);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void GivenValidBuildingAndRoom_WhenCreateOrder_ThenOrderServiceIsCalledAndOrderReturned() {
        int building = 5;
        int room = 10;
        when(orderService.createOrder(building, room)).thenReturn(testOrder);
        Order createdOrder = orderManagementService.createOrder(building, room);
        verify(orderService, times(1)).createOrder(building, room);
        assertEquals(testOrder, createdOrder);
    }

    @Test
    void GivenOrderId_WhenCancelOrder_ThenPancakeOrderServiceIsCalled() {
        orderManagementService.cancelOrder(testOrderId);
        verify(pancakeOrderService, times(1)).cancelOrder(testOrderId);
    }

    @Test
    void GivenOrderId_WhenCompleteOrder_ThenOrderServiceIsCalled() {
        orderManagementService.completeOrder(testOrderId);
        verify(orderService, times(1)).completeOrder(testOrderId);
    }

    @Test
    void WhenListCompletedOrders_ThenOrderServiceIsCalledAndSetReturned() {
        Set<UUID> expectedCompleted = new HashSet<>(Arrays.asList(UUID.randomUUID(), testOrderId));
        when(orderService.listCompletedOrders()).thenReturn(expectedCompleted);
        Set<UUID> actualCompleted = orderManagementService.listCompletedOrders();
        verify(orderService, times(1)).listCompletedOrders();
        assertEquals(expectedCompleted, actualCompleted);
    }

    @Test
    void WhenListPreparedOrders_ThenOrderServiceIsCalledAndSetReturned() {
        Set<UUID> expectedPrepared = new HashSet<>(Arrays.asList(UUID.randomUUID(), testOrderId));
        when(orderService.listPreparedOrders()).thenReturn(expectedPrepared);
        Set<UUID> actualPrepared = orderManagementService.listPreparedOrders();
        verify(orderService, times(1)).listPreparedOrders();
        assertEquals(expectedPrepared, actualPrepared);
    }

    @Test
    void GivenOrderId_WhenPrepareOrder_ThenOrderServiceIsCalled() {
        orderManagementService.prepareOrder(testOrderId);
        verify(orderService, times(1)).prepareOrder(testOrderId);
    }

    @Test
    void GivenOrderId_WhenIsPrepared_ThenOrderServiceIsCalledAndBooleanReturned() {
        when(orderService.isPrepared(testOrderId)).thenReturn(true);
        boolean isPrepared = orderManagementService.isPrepared(testOrderId);
        verify(orderService, times(1)).isPrepared(testOrderId);
        assertTrue(isPrepared);
    }

    @Test
    void GivenOrderId_WhenGetOrder_ThenOrderServiceIsCalledAndOptionalReturned() {
        when(orderService.getOrder(testOrderId)).thenReturn(Optional.of(testOrder));
        Optional<Order> actualOrder = orderManagementService.getOrder(testOrderId);
        verify(orderService, times(1)).getOrder(testOrderId);
        assertTrue(actualOrder.isPresent());
        assertEquals(testOrder, actualOrder.get());
    }

    @Test
    void GivenOrderId_WhenRemoveOrder_ThenOrderServiceIsCalled() {
        orderManagementService.removeOrder(testOrderId);
        verify(orderService, times(1)).removeOrder(testOrderId);
    }

    @Test
    void GivenIngredients_WhenAddPancake_ThenPancakeServiceIsCalled() {
        List<String> ingredients = Arrays.asList("chocolate", "banana");
        orderManagementService.addPancake(ingredients);
        verify(pancakeService, times(1)).addPancake(ingredients);
    }

    @Test
    void GivenPancakeRecipe_WhenAddPancake_ThenPancakeServiceIsCalled() {
        PancakeRecipe mockPancake = mock(PancakeRecipe.class);
        orderManagementService.addPancake(mockPancake);
        verify(pancakeService, times(1)).addPancake(mockPancake);
    }

    @Test
    void WhenGetPancakes_ThenPancakeServiceIsCalledAndListReturned() {
        List<PancakeRecipe> expectedPancakes = Arrays.asList(mock(PancakeRecipe.class), mock(PancakeRecipe.class));
        when(pancakeService.getPancakes()).thenReturn(expectedPancakes);
        List<PancakeRecipe> actualPancakes = orderManagementService.getPancakes();
        verify(pancakeService, times(1)).getPancakes();
        assertEquals(expectedPancakes, actualPancakes);
    }

    @Test
    void GivenOrderId_WhenGetPancakeDescriptionsForOrder_ThenPancakeServiceIsCalledAndListReturned() {
        List<String> expectedDescriptions = Arrays.asList("Dark Chocolate", "Milk Chocolate");
        when(pancakeService.getPancakeDescriptionsForOrder(testOrderId)).thenReturn(expectedDescriptions);
        List<String> actualDescriptions = orderManagementService.getPancakeDescriptionsForOrder(testOrderId);
        verify(pancakeService, times(1)).getPancakeDescriptionsForOrder(testOrderId);
        assertEquals(expectedDescriptions, actualDescriptions);
    }

    @Test
    void GivenOrderId_WhenRemovePancakesForOrder_ThenPancakeServiceIsCalled() {
        orderManagementService.removePancakesForOrder(testOrderId);
        verify(pancakeService, times(1)).removePancakesForOrder(testOrderId);
    }

    @Test
    void GivenPancakes_WhenRemovePancakes_ThenPancakeServiceIsCalledAndListReturned() {
        String description = "Vanilla Pancake";
        int count = 1;
        List<PancakeRecipe> expectedRemovedPancakes = Collections.singletonList(mock(PancakeRecipe.class));
        when(pancakeService.removePancakes(testOrderId, description, count)).thenReturn(expectedRemovedPancakes);
        List<PancakeRecipe> actualRemovedPancakes = orderManagementService.removePancakes(testOrderId, description, count);
        verify(pancakeService, times(1)).removePancakes(testOrderId, description, count);
        assertEquals(expectedRemovedPancakes, actualRemovedPancakes);
    }
}
