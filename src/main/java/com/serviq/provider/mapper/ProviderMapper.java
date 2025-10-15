package com.serviq.provider.mapper;

import com.serviq.provider.dto.ProviderContactDTO;
import com.serviq.provider.dto.ProviderDTO;
import com.serviq.provider.dto.ProviderLocationDTO;
import com.serviq.provider.entity.Provider;
import com.serviq.provider.entity.ProviderContact;
import com.serviq.provider.entity.ProviderLocation;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProviderMapper {
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "locations", ignore = true)
    ProviderDTO toDTO(Provider provider);

    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "locations", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Provider toEntity(ProviderDTO dto);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "locations", ignore = true)
    void updateEntityFromDTO(ProviderDTO dto, @MappingTarget Provider provider);

    @Mapping(source = "provider.id", target = "providerId")
    ProviderContactDTO toContactDTO(ProviderContact contact);

    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProviderContact toContactEntity(ProviderContactDTO dto);

    List<ProviderContactDTO> toContactDTOList(List<ProviderContact> contacts);

    @Mapping(source = "provider.id", target = "providerId")
    ProviderLocationDTO toLocationDTO(ProviderLocation location);

    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProviderLocation toLocationEntity(ProviderLocationDTO dto);

    List<ProviderLocationDTO> toLocationDTOList(List<ProviderLocation> locations);
}
