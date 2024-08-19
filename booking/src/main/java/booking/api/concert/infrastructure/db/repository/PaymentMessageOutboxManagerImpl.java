package booking.api.concert.infrastructure.db.repository;

import booking.api.concert.domain.PaymentOutbox;
import booking.api.concert.domain.enums.PaymentOutboxState;
import booking.api.concert.domain.event.PaymentSuccessEvent;
import booking.api.concert.domain.message.PaymentMessageOutboxManager;
import booking.api.concert.infrastructure.db.PaymentOutboxEntity;
import booking.support.JsonUtil;
import booking.support.exception.CustomNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static booking.support.exception.ErrorCode.OUTBOX_IS_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class PaymentMessageOutboxManagerImpl implements PaymentMessageOutboxManager {

    private final JpaPaymentOutboxRepository jpaPaymentOutboxRepository;
    private String uuid;

    @Override
    public PaymentOutbox save(PaymentSuccessEvent message) {
        String payload = JsonUtil.toJson(message);
        uuid = UUID.randomUUID().toString();
        PaymentOutbox paymentOutbox = PaymentOutbox.create(uuid, payload);
        return jpaPaymentOutboxRepository.save(PaymentOutboxEntity.of(paymentOutbox)).toDomain();
    }

    @Override
    public void complete(PaymentSuccessEvent message) {
        PaymentOutbox paymentOutbox = jpaPaymentOutboxRepository.findById(uuid)
                .orElseThrow(() -> new CustomNotFoundException(OUTBOX_IS_NOT_FOUND, "해당하는 아웃박스 데이터가 없습니다."))
                .toDomain();
        paymentOutbox.publish();
        paymentOutbox.updateTime();
        jpaPaymentOutboxRepository.save(PaymentOutboxEntity.of(paymentOutbox));
    }

    @Override
    public PaymentOutbox findByUuid(String uuid) {
        return jpaPaymentOutboxRepository.findById(uuid)
                .orElseThrow(() -> new CustomNotFoundException(OUTBOX_IS_NOT_FOUND, "해당하는 아웃박스 데이터가 없습니다."))
                .toDomain();
    }

    @Override
    public List<PaymentOutbox> findAll() {
        List<PaymentOutboxEntity> entities = jpaPaymentOutboxRepository.findAll();
        return entities.stream()
                .map(PaymentOutboxEntity::toDomain)
                .toList();
    }

    @Override
    public List<PaymentOutbox> findAllByStatus(PaymentOutboxState paymentOutboxState) {
        List<PaymentOutboxEntity> entities = jpaPaymentOutboxRepository.findAllByStatus(paymentOutboxState);
        return entities.stream()
                .map(PaymentOutboxEntity::toDomain)
                .toList();
    }

    @Override
    public PaymentOutbox entitySave(PaymentOutbox paymentOutbox) {
        return jpaPaymentOutboxRepository.save(PaymentOutboxEntity.of(paymentOutbox)).toDomain();
    }
}
