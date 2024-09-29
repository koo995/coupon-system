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

    /**
     * 레이스 컨디션을 해결하기 위해서 제일 먼저,
     * 1. Java 에서 지원하는 synchronized 키워드를 사용할 수 있습니다. 하지만 서버가 여러대라면 동기화가 되지 않습니다.
     * 2. 다른 방법으로는 mysql을 활용한 락이 있다. 하지만 쿠폰개수를 가져오는 것부터 쿠폰을 생성하는 구간까지 락을 걸어야 해서 락을 거는 기간이 길어진다.
     * 이는 곧, 성능이 느려지는 문제가 있다.
     * 이 프로젝트의 핵심은 쿠폰의 갯수이므로 쿠폰 갯수의 정합성을 관리하면 된다.
     * 3. 레디스에는 incr이라는 명령어가 있고 이는 키의 벨류를 1씩 증가시킨다.
     * 레디스는 싱글 스레드를 기반으로 동작하여 레이스 건디션을 해결할 수 있고, incr은 성능도 굉장히 빠르다.
     * 이 명령어를 사용하여 발급된 쿠폰 갯수를 제어한다면 성능도 빠르며 데이터 장합성도 지킬 수 있다. 이것이 Redis 을 사용하는 이유다.
     */
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