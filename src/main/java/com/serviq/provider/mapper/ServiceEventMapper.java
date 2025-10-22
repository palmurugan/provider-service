package com.serviq.provider.mapper;

import com.serviq.provider.dto.event.ServiceEventDto;
import com.serviq.provider.service.events.ServiceEvent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ServiceEventMapper {

    public ServiceEventDto toServiceEventDto(ServiceEvent serviceEvent) {
        return ServiceEventDto.builder()
                .eventId(serviceEvent.getEventId())
                .eventType(serviceEvent.getEventType())
                .occurredOn(serviceEvent.getOccurredOn())
                .orgId(serviceEvent.getOrgId())
                .serviceId(serviceEvent.getServiceId())
                .title(serviceEvent.getTitle())
                .categoryId(serviceEvent.getCategoryId())
                .category(serviceEvent.getCategory())
                .providerId(serviceEvent.getProviderId())
                .providerName(serviceEvent.getProviderName())
                .duration(serviceEvent.getDuration())
                .unit(serviceEvent.getUnit())
                .price(new BigDecimal(serviceEvent.getPrice()))
                .currency(serviceEvent.getCurrency())
                .latitude(serviceEvent.getLatitude())
                .longitude(serviceEvent.getLongitude())
                .isActive(serviceEvent.getIsActive())
                .build();
    }

    public ServiceEvent toServiceEventAvro(ServiceEventDto serviceEventDto) {
        return ServiceEvent.newBuilder()
                .setEventId(serviceEventDto.getEventId())
                .setEventType(serviceEventDto.getEventType())
                .setOccurredOn(serviceEventDto.getOccurredOn())
                .setOrgId(serviceEventDto.getOrgId())
                .setServiceId(serviceEventDto.getServiceId())
                .setTitle(serviceEventDto.getTitle())
                .setCategoryId(serviceEventDto.getCategoryId())
                .setCategory(serviceEventDto.getCategory())
                .setProviderId(serviceEventDto.getProviderId())
                .setProviderName(serviceEventDto.getProviderName())
                .setDuration(serviceEventDto.getDuration())
                .setUnit(serviceEventDto.getUnit())
                .setPrice(serviceEventDto.getPrice().toString())
                .setCurrency(serviceEventDto.getCurrency())
                .setLatitude(serviceEventDto.getLatitude())
                .setLongitude(serviceEventDto.getLongitude())
                .setIsActive(serviceEventDto.isActive())
                .build();
    }
}
