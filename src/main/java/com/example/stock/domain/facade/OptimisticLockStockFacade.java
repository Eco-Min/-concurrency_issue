package com.example.stock.domain.facade;

import com.example.stock.domain.Stock;
import com.example.stock.service.OptimisticLockStockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 실패 했을 경우 재 시도를 해줘야 하므로
 * facade 객체를 하나 만든다
 */

@Service
public class OptimisticLockStockFacade {

    private OptimisticLockStockService optimisticLockStockService;

    public OptimisticLockStockFacade(OptimisticLockStockService optimisticLockStockService) {
        this.optimisticLockStockService = optimisticLockStockService;
    }

    @Transactional
    public synchronized void decreaseStock(Long id, Long quantity) throws InterruptedException {
        while (true) {
            try {
                optimisticLockStockService.decreaseStock(id, quantity);

                break;
            } catch (Exception e) {
                Thread.sleep(50);
            }
        }
    }
}
