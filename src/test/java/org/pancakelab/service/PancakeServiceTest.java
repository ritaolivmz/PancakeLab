package org.pancakelab.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pancakelab.model.pancakes.PancakeRecipe;
import org.pancakelab.repository.PancakeRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PancakeServiceTest {
    @Mock
    private PancakeRepository pancakeRepo;

    @InjectMocks
    private PancakeService pancakeService;

    private UUID testOrderId;

    @BeforeEach
    void setUp() {
        testOrderId = UUID.randomUUID();
    }

    @Test
    void GivenIngredients_WhenAddPancake_ThenPancakeIsSaved() {
        List<String> ingredients = Arrays.asList("bananas","cherries");
        pancakeService.addPancake(ingredients);
        verify(pancakeRepo, times(1)).save(any(PancakeRecipe.class));
    }

    @Test
    void GivenPancakeRecipe_WhenAddPancake_ThenPancakeIsSaved() {
        PancakeRecipe mockPancake = mock(PancakeRecipe.class);
        pancakeService.addPancake(mockPancake);
        verify(pancakeRepo, times(1)).save(mockPancake);
    }

    @Test
    void WhenGetPancakes_ThenAllPancakesAreReturned() {
        List<PancakeRecipe> expectedPancakes = Arrays.asList(mock(PancakeRecipe.class), mock(PancakeRecipe.class));
        when(pancakeRepo.retrievePancakes()).thenReturn(expectedPancakes);
        List<PancakeRecipe> actualPancakes = pancakeService.getPancakes();
        verify(pancakeRepo, times(1)).retrievePancakes();
        assertEquals(expectedPancakes, actualPancakes);
    }

    @Test
    void GivenOrderId_WhenGetPancakeDescriptionsForOrder_ThenCorrectDescriptionsAreReturned() {
        PancakeRecipe pancake1 = mock(PancakeRecipe.class);
        when(pancake1.getOrderId()).thenReturn(testOrderId);
        when(pancake1.description()).thenReturn("honey");
        PancakeRecipe pancake2 = mock(PancakeRecipe.class);
        when(pancake2.getOrderId()).thenReturn(UUID.randomUUID());
        PancakeRecipe pancake3 = mock(PancakeRecipe.class);
        when(pancake3.getOrderId()).thenReturn(testOrderId);
        when(pancake3.description()).thenReturn("strawberries");
        List<PancakeRecipe> allPancakes = Arrays.asList(pancake1, pancake2, pancake3);
        when(pancakeRepo.retrievePancakes()).thenReturn(allPancakes);
        List<String> actualDescriptions = pancakeService.getPancakeDescriptionsForOrder(testOrderId);
        List<String> expectedDescriptions = Arrays.asList("honey", "strawberries");
        assertEquals(expectedDescriptions, actualDescriptions);
    }

    @Test
    void GivenNoPancakesForOrderId_WhenGetPancakeDescriptionsForOrder_ThenEmptyListIsReturned() {
        when(pancakeRepo.retrievePancakes()).thenReturn(Collections.emptyList());
        List<String> actualDescriptions = pancakeService.getPancakeDescriptionsForOrder(testOrderId);
        assertTrue(actualDescriptions.isEmpty());
    }

    @Test
    void GivenPancakesExist_WhenRemovePancakes_ThenCorrectPancakesAreRemoved() {
        String description = "Chocolate Pancake";
        int count = 2;
        PancakeRecipe pancake1 = mock(PancakeRecipe.class);
        when(pancake1.getOrderId()).thenReturn(testOrderId);
        when(pancake1.description()).thenReturn(description);
        PancakeRecipe pancake2 = mock(PancakeRecipe.class);
        when(pancake2.getOrderId()).thenReturn(testOrderId);
        when(pancake2.description()).thenReturn(description);
        List<PancakeRecipe> allPancakes = Arrays.asList(pancake1, pancake2, mock(PancakeRecipe.class), mock(PancakeRecipe.class), mock(PancakeRecipe.class));
        when(pancakeRepo.retrievePancakes()).thenReturn(allPancakes);
        List<PancakeRecipe> removedPancakes = pancakeService.removePancakes(testOrderId, description, count);
        List<PancakeRecipe> expectedRemovedPancakes = Arrays.asList(pancake1, pancake2);
        verify(pancakeRepo, times(1)).removePancakes(expectedRemovedPancakes);
        assertEquals(expectedRemovedPancakes, removedPancakes);
    }

    @Test
    void GivenNoMatchingPancakes_WhenRemovePancakes_ThenNoPancakesAreRemoved() {
        when(pancakeRepo.retrievePancakes()).thenReturn(Collections.emptyList());
        List<PancakeRecipe> removedPancakes = pancakeService.removePancakes(testOrderId, "N.A.", 1);
        verify(pancakeRepo, never()).removePancakes(anyList());
        assertTrue(removedPancakes.isEmpty());
    }

    @Test
    void GivenOrderId_WhenRemovePancakesForOrder_ThenPancakesAreRemovedForThatOrder() {
        pancakeService.removePancakesForOrder(testOrderId);
        verify(pancakeRepo, times(1)).removePancakesForOrder(testOrderId);
    }

    @Test
    void GivenNullIngredientsList_WhenAddPancake_ThenIllegalArgumentExceptionIsThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pancakeService.addPancake((List<String>) null);
        });
        assertEquals("The provided list can not be null.", exception.getMessage());
        verifyNoInteractions(pancakeRepo);
    }

    @Test
    void GivenEmptyIngredientsList_WhenAddPancake_ThenIllegalArgumentExceptionIsThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pancakeService.addPancake(Collections.emptyList());
        });
        assertEquals("The provided list can not be empty.", exception.getMessage());
        verifyNoInteractions(pancakeRepo);
    }

    @Test
    void GivenNullPancakeObject_WhenAddPancake_ThenIllegalArgumentExceptionIsThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pancakeService.addPancake((PancakeRecipe) null);
        });
        assertEquals("The provided object can not be null.", exception.getMessage());
        verifyNoInteractions(pancakeRepo);
    }

    @Test
    void GivenNullOrderId_WhenGetPancakeDescriptionsForOrder_ThenIllegalArgumentExceptionIsThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pancakeService.getPancakeDescriptionsForOrder(null);
        });
        assertEquals("The provided order ID can not be null.", exception.getMessage());
        verifyNoInteractions(pancakeRepo);
    }

    @Test
    void GivenNullOrderId_WhenRemovePancakes_ThenIllegalArgumentExceptionIsThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pancakeService.removePancakes(null, "Chocolate", 1);
        });
        assertEquals("The provided order ID can not be null.", exception.getMessage());
        verifyNoInteractions(pancakeRepo);
    }

    @Test
    void GivenZeroCount_WhenRemovePancakes_ThenIllegalArgumentExceptionIsThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pancakeService.removePancakes(testOrderId, "Chocolate", 0);
        });
        assertEquals("All numbers must be positive.", exception.getMessage());
        verifyNoInteractions(pancakeRepo);
    }

    @Test
    void GivenNegativeCount_WhenRemovePancakes_ThenIllegalArgumentExceptionIsThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pancakeService.removePancakes(testOrderId, "Chocolate", -5);
        });
        assertEquals("All numbers must be positive.", exception.getMessage());
        verifyNoInteractions(pancakeRepo);
    }

    @Test
    void GivenNullOrderId_WhenRemovePancakesForOrder_ThenIllegalArgumentExceptionIsThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pancakeService.removePancakesForOrder(null);
        });
        assertEquals("The provided order ID can not be null.", exception.getMessage());
        verifyNoInteractions(pancakeRepo);
    }

    @Test
    void GivenPancakes_WhenRemovePancakes_ThenPancakesAreRemoved() {
        PancakeRecipe matchingPancake1 = mock(PancakeRecipe.class);
        when(matchingPancake1.getOrderId()).thenReturn(testOrderId);
        when(matchingPancake1.description()).thenReturn("Vanilla");
        PancakeRecipe matchingPancake2 = mock(PancakeRecipe.class);
        PancakeRecipe nonMatchingPancake = mock(PancakeRecipe.class);
        when(pancakeRepo.retrievePancakes()).thenReturn(Arrays.asList(matchingPancake1, matchingPancake2, nonMatchingPancake));
        String description = "Vanilla";
        int count = 1;
        List<PancakeRecipe> removedPancakes = pancakeService.removePancakes(testOrderId, description, count);
        verify(pancakeRepo, times(count)).removePancakes(Collections.singletonList(matchingPancake1));
        assertEquals(count, removedPancakes.size());
        assertEquals(matchingPancake1, removedPancakes.get(0));
    }
}
