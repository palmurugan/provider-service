package com.serviq.provider.service;

import com.serviq.provider.entity.Location;

import java.util.UUID;

public interface LocationService {
    Location getLocationById(UUID locationId);
}
