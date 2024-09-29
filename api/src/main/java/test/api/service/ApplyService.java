package test.api.service;

import org.springframework.stereotype.Service;
import test.api.domain.Coupon;
import test.api.repository.CouponCountRepository;
import test.api.repository.CouponRepository;

@Service
public class ApplyService {

    private final CouponRepository couponRepository;

    private final CouponCountRepository couponCountRepository;

    public ApplyService(CouponRepository couponRepository, CouponCountRepository couponCountRepository) {
        this.couponRepository = couponRepository;
        this.couponCountRepository = couponCountRepository;
    }

    /**
     * 쿠폰 발급 로직
     */
    public void apply(Long userId) {
        Long count = couponCountRepository.increment();
        if (count > 100) {
            return;
        }

        couponRepository.save(new Coupon(userId));
    }
}
