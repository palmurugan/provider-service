package com.serviq.provider.mapper;

import com.serviq.provider.dto.request.CreateProviderServiceRequest;
import com.serviq.provider.dto.request.UpdateProviderServiceRequest;
import com.serviq.provider.dto.response.LocationResponse;
import com.serviq.provider.dto.response.ProviderServiceResponse;
import com.serviq.provider.entity.ProviderService;
import com.serviq.provider.entity.ServiceLocation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Mapping(target = "locations", source = "serviceLocations")
    ProviderServiceResponse toResponse(ProviderService entity);

    /**
     * Maps Set<ServiceLocation> to Set<LocationResponse>
     */
    default Set<LocationResponse> mapServiceLocations(Set<ServiceLocation> serviceLocations) {
        if (serviceLocations == null || serviceLocations.isEmpty()) {
            return Collections.emptySet();
        }
        return serviceLocations.stream()
                .map(this::toLocationResponse)
                .collect(Collectors.toSet());
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "location.name")
    @Mapping(target = "city", source = "location.city")
    @Mapping(target = "state", source = "location.state")
    @Mapping(target = "country", source = "location.country")
    @Mapping(target = "postalCode", source = "location.postalCode")
    @Mapping(target = "latitude", source = "location.latitude")
    @Mapping(target = "longitude", source = "location.longitude")
    //@Mapping(target = "isPrimary", source = "isPrimary")
    //@Mapping(target = "isActive", source = "isActive")
    //@Mapping(target = "createdAt", source = "createdAt")
    LocationResponse toLocationResponse(ServiceLocation serviceLocation);


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
