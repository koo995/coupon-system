package test.consumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import test.consumer.domain.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
