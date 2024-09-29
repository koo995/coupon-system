package test.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import test.api.domain.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
