package cz.sinko.moneymanager.connectors.service;

import java.util.Optional;

import cz.sinko.moneymanager.connectors.service.entity.CacheData;

public interface CacheService {

	void save(CacheData cacheData);

	Optional<CacheData> getKey(String key);

}