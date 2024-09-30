package test.api.service;

import org.springframework.stereotype.Service;
import test.api.domain.Coupon;
import test.api.producer.CouponCreateProducer;
import test.api.repository.CouponCountRepository;
import test.api.repository.CouponRepository;

@Service
public class ApplyService {

    private final CouponRepository couponRepository;

    private final CouponCountRepository couponCountRepository;

    private final CouponCreateProducer couponCreateProducer;

    public ApplyService(CouponRepository couponRepository, CouponCountRepository couponCountRepository, CouponCreateProducer couponCreateProducer) {
        this.couponRepository = couponRepository;
        this.couponCountRepository = couponCountRepository;
        this.couponCreateProducer = couponCreateProducer;
    }

    /**
     * 쿠폰 발급 로직
     */
    public void apply(Long userId) {
        Long count = couponCountRepository.increment();
        if (count > 100) {
            return;
        }

        couponCreateProducer.create(userId);
    }
}
