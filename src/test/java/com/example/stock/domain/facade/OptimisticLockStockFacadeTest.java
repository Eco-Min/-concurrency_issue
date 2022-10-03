package com.example.stock.domain.facade;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import com.example.stock.service.PessimisticLockStockService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OptimisticLockStockFacadeTest {
    @Autowired
    private OptimisticLockStockFacade stockService;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void before() {
        Stock stock = new Stock(1L, 100L);
        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    public void after() {
        stockRepository.deleteAll();
    }

    @Test
    @DisplayName("스톡 감소")
    public void stock_decrease() throws Exception{
        //given
        stockService.decreaseStock(1L, 1L);

        //when
        Stock stock = stockRepository.findById(1L).orElseThrow();
        int numOfCores = Runtime.getRuntime().availableProcessors();

        //then
        assertThat(stock.getQuantity()).isEqualTo(99);
    }

    @Test
    @DisplayName("동싱 100개의 요청")
    public void request_100_() throws Exception{
        //given
        int threadCount = 100;
//        int numOfCores = Runtime.getRuntime().availableProcessors();
//        System.out.println("numOfCores = " + numOfCores);
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        //when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decreaseStock(1L, 1L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();

        //then
        assertThat(stock.getQuantity()).isEqualTo(0L);

    }
}