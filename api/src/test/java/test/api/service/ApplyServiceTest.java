package test.api.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import test.api.repository.CouponRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class ApplyServiceTest {
    @Autowired
    private ApplyService applyService;
    @Autowired
    private CouponRepository couponRepository;

    @DisplayName("한번만 응모")
    @Test
    void 한번만_응모() throws Exception {
        // given
        applyService.apply(1L);

        // when
        long count = couponRepository.count();

        // then
        assertThat(count).isEqualTo(1);
    }

    @DisplayName("동시에 여러명 응모")
    @Test
    void 여러_응모() throws Exception {
        // given
        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for(int i = 0; i < threadCount; i++) {
            long userId = i;
            executorService.submit(() -> {
                try {
                    applyService.apply(userId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        long count = couponRepository.count();
        assertThat(count).isEqualTo(100);
    }
}