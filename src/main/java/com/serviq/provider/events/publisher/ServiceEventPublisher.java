package com.serviq.provider.events.publisher;

import com.serviq.provider.dto.event.ServiceEventDto;
import com.serviq.provider.events.EventPublisher;
import com.serviq.provider.exception.EventPublishException;
import com.serviq.provider.mapper.ServiceEventMapper;
import com.serviq.provider.service.events.ServiceEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceEventPublisher implements EventPublisher<ServiceEventDto> {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ServiceEventMapper eventMapper;

    @Value("${kafka.topic.service-events}")
    private String topic;


    @Override
    public void publish(ServiceEventDto eventDto) {
        try {
            ServiceEvent avroEvent = eventMapper.toServiceEventAvro(eventDto);
            String key = eventDto.getServiceId(); // Using serviceId as partition key

            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(topic, key, avroEvent);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully published service created event. ServiceId: {}, Topic: {}, Partition: {}, Offset: {}",
                            eventDto.getServiceId(),
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish service created event. ServiceId: {}, Error: {}",
                            eventDto.getServiceId(), ex.getMessage(), ex);
                }
            });

        } catch (Exception e) {
            log.error("Error while publishing service event. ServiceId: {}",
                    eventDto.getServiceId(), e);
            throw new EventPublishException("Failed to publish service event", e);
        }
    }
}
