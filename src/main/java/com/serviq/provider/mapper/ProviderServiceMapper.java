package com.serviq.provider.mapper;

import com.serviq.provider.dto.request.CreateProviderServiceRequest;
import com.serviq.provider.dto.request.UpdateProviderServiceRequest;
import com.serviq.provider.dto.response.ProviderServiceResponse;
import com.serviq.provider.entity.ProviderService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProviderServiceMapper {

    /**
     * Maps CreateProviderServiceRequest to ProviderService entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProviderService toEntity(CreateProviderServiceRequest request);

    /**
     * Maps ProviderService entity to ProviderServiceResponse
     */
    ProviderServiceResponse toResponse(ProviderService entity);

    /**
     * Updates existing ProviderService entity with UpdateProviderServiceRequest
     * Only non-null fields from request will be updated
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orgId", ignore = true)
    @Mapping(target = "providerId", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UpdateProviderServiceRequest request, @MappingTarget ProviderService entity);
}
