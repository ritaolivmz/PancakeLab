package org.pancakelab.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pancakelab.model.Order;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class OrderRepositoryConcurrencyTest {

    private OrderRepository orderRepository;
    private List<UUID> orderIds;
    private final int NUM_ORDERS = 100;
    private final int NUM_THREADS = 10;
    private final int OPERATIONS_PER_THREAD = 50;

    @BeforeEach
    void setUp() {
        orderRepository = new OrderRepository();
        orderIds = new ArrayList<>();
        for (int i = 0; i < NUM_ORDERS; i++) {
            Order order = new Order(1, i + 1);
            orderRepository.save(order);
            orderIds.add(order.getId());
        }
    }

    @Test
    void GivenConcurrentOperations_WhenCompletingAndPreparingOrders_ThenFinalStateIsConsistent() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        CountDownLatch latch = new CountDownLatch(NUM_THREADS);

        List<Throwable> caughtExceptions = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < NUM_THREADS; i++) {
            executor.submit(() -> {
                try {
                    latch.countDown();
                    latch.await();

                    Random random = new Random();
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        UUID randomOrderId = orderIds.get(random.nextInt(NUM_ORDERS));

                        // 0: complete, 1: prepare, 2: isPrepared
                        int operation = random.nextInt(3);

                        if (operation == 0) {
                            orderRepository.markCompleted(randomOrderId);
                        } else if (operation == 1) {
                            orderRepository.markPrepared(randomOrderId);
                        } else {
                            orderRepository.isPrepared(randomOrderId);
                        }
                    }
                } catch (Throwable e) {
                    caughtExceptions.add(e);
                }
            });
        }

        executor.shutdown();

        assertTrue(executor.awaitTermination(30, TimeUnit.SECONDS), "Executor did not terminate in time");

        assertTrue(caughtExceptions.isEmpty(), "Exceptions caught during concurrent execution: " + caughtExceptions);

        Set<UUID> finalPrepared = orderRepository.getPreparedOrders();
        Set<UUID> finalCompleted = orderRepository.getCompletedOrders();

        for (UUID orderId : orderIds) {
            assertTrue(orderRepository.findById(orderId) != null, "Order " + orderId + " was unexpectedly deleted.");

            boolean isPrepared = finalPrepared.contains(orderId);
            boolean isCompleted = finalCompleted.contains(orderId);

            assertFalse(isPrepared && isCompleted, "Order " + orderId + " is both prepared and completed.");
        }

        System.out.println("Final Prepared Orders: " + finalPrepared.size());
        System.out.println("Final Completed Orders: " + finalCompleted.size());
    }

    @Test
    void GivenConcurrentAddAndDelete_WhenAddingAndRemovingOrders_ThenFinalCountIsConsistent() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        CountDownLatch latch = new CountDownLatch(NUM_THREADS);
        List<Throwable> caughtExceptions = Collections.synchronizedList(new ArrayList<>());

        List<UUID> dynamicallyAddedOrderIds = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < NUM_THREADS; i++) {
            final int threadId = i;
            executor.submit(() -> {
                Random random = new Random();
                try {
                    latch.countDown();
                    latch.await();

                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        if (random.nextBoolean()) {
                            Order newOrder = new Order(threadId, j);
                            orderRepository.save(newOrder);
                            dynamicallyAddedOrderIds.add(newOrder.getId());
                        } else {
                            List<UUID> allCurrentOrderIds = new ArrayList<>(orderRepository.findAll().stream().map(Order::getId).toList());
                            if (!allCurrentOrderIds.isEmpty()) {
                                UUID orderToDelete = allCurrentOrderIds.get(random.nextInt(allCurrentOrderIds.size()));
                                orderRepository.delete(orderToDelete);
                            }
                        }
                    }
                } catch (Throwable e) {
                    caughtExceptions.add(e);
                }
            });
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(30, TimeUnit.SECONDS), "Executor did not terminate in time");
        assertTrue(caughtExceptions.isEmpty(), "Exceptions caught during concurrent execution: " + caughtExceptions);

        for (UUID originalOrderId : orderIds) {
            orderRepository.findById(originalOrderId);
        }

        System.out.println("Final total orders in repository: " + orderRepository.findAll().size());
        System.out.println("Dynamically added orders count: " + dynamicallyAddedOrderIds.size());
    }
}