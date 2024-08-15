package booking.api.concert.infrastructure.db.repository;

import booking.api.concert.domain.PaymentOutbox;
import booking.api.concert.domain.enums.PaymentOutboxState;
import booking.api.concert.domain.event.PaymentSuccessEvent;
import booking.api.concert.domain.message.PaymentMessage;
import booking.api.concert.domain.message.PaymentMessageOutbox;
import booking.api.concert.infrastructure.db.PaymentOutboxEntity;
import booking.support.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PaymentMessageOutboxImpl implements PaymentMessageOutbox {

    private final JpaPaymentOutboxRepository jpaPaymentOutboxRepository;

    @Override
    public PaymentOutbox save(PaymentMessage<PaymentSuccessEvent> message) {
        String payload = JsonUtil.toJson(message);
        PaymentOutbox paymentOutbox = PaymentOutbox.create(message.getMessageId(), payload);
        PaymentOutboxEntity entity = jpaPaymentOutboxRepository.save(PaymentOutboxEntity.of(paymentOutbox));
        return PaymentOutboxEntity.toDomain(entity);
    }

    @Override
    public void complete(PaymentMessage<PaymentSuccessEvent> message) {
        PaymentOutboxEntity entity = jpaPaymentOutboxRepository.findByUuid(message.getMessageId());
        PaymentOutbox paymentOutbox = PaymentOutboxEntity.toDomain(entity);
        paymentOutbox.publish();
        paymentOutbox.updateTime();
        jpaPaymentOutboxRepository.save(PaymentOutboxEntity.of(paymentOutbox));
    }

    @Override
    public PaymentOutbox findByUuid(String uuid) {
        return PaymentOutboxEntity.toDomain(jpaPaymentOutboxRepository.findByUuid(uuid));
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
        return PaymentOutboxEntity.toDomain(jpaPaymentOutboxRepository.save(PaymentOutboxEntity.of(paymentOutbox)));
    }
}
