package com.serviq.provider.service;

import com.serviq.provider.entity.ServiceLocation;

import java.util.List;
import java.util.UUID;

public interface ServiceLocationService {

    void addLocationToService(UUID providerServiceId, UUID locationId, boolean isPrimary);

    void removeLocationFromService(UUID serviceId, UUID locationId);

    List<ServiceLocation> getServiceLocations(UUID serviceId);
}
