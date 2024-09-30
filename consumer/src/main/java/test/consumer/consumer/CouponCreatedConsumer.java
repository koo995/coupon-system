package test.consumer.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import test.consumer.domain.Coupon;
import test.consumer.domain.FailedEvent;
import test.consumer.repository.CouponRepository;
import test.consumer.repository.FailedEventRepository;

@Component
public class CouponCreatedConsumer {
    private final CouponRepository couponRepository;
    private final FailedEventRepository failedEventRepository;
    private final Logger logger = LoggerFactory.getLogger(CouponCreatedConsumer.class);

    public CouponCreatedConsumer(CouponRepository couponRepository, FailedEventRepository failedEventRepository) {
        this.couponRepository = couponRepository;
        this.failedEventRepository = failedEventRepository;
    }

    @KafkaListener(topics = "coupon_create", groupId = "group_1")
    public void listener(Long userId) {
        try {
            couponRepository.save(new Coupon(userId));
        } catch (Exception e) {
            logger.error("Error occurred while saving coupon", userId);
            failedEventRepository.save(new FailedEvent(userId));
        }
    }
}
