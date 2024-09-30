package test.api.service;

import org.springframework.stereotype.Service;
import test.api.domain.Coupon;
import test.api.producer.CouponCreateProducer;
import test.api.repository.AppliedUserRepository;
import test.api.repository.CouponCountRepository;
import test.api.repository.CouponRepository;

@Service
public class ApplyService {

    private final CouponRepository couponRepository;

    private final CouponCountRepository couponCountRepository;

    private final CouponCreateProducer couponCreateProducer;

    private final AppliedUserRepository appliedUserRepository;

    public ApplyService(CouponRepository couponRepository, CouponCountRepository couponCountRepository, CouponCreateProducer couponCreateProducer, AppliedUserRepository appliedUserRepository) {
        this.couponRepository = couponRepository;
        this.couponCountRepository = couponCountRepository;
        this.couponCreateProducer = couponCreateProducer;
        this.appliedUserRepository = appliedUserRepository;
    }

    /**
     * 쿠폰 발급 로직
     */
    public void apply(Long userId) {
        Long apply = appliedUserRepository.add(userId);
        if (apply != 1) {
            return;
        }

        Long count = couponCountRepository.increment();
        if (count > 100) {
            return;
        }

        couponCreateProducer.create(userId);
    }
}
