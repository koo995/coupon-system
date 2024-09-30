package test.consumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import test.consumer.domain.FailedEvent;

public interface FailedEventRepository extends JpaRepository<FailedEvent, Long> {
}
