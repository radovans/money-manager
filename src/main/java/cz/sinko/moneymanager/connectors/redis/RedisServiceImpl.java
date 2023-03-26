package cz.sinko.moneymanager.connectors.redis;

import java.util.Optional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.connectors.service.entity.CacheData;
import cz.sinko.moneymanager.connectors.redis.repository.CacheDataRepository;
import cz.sinko.moneymanager.connectors.service.CacheService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class RedisServiceImpl implements CacheService {

	private final CacheDataRepository cacheDataRepository;

	@Async
	public void save(CacheData cacheData) {
		log.info("Saving cache data to redis: {}", cacheData);
		cacheDataRepository.save(cacheData);
	}

	public Optional<CacheData> getKey(String key) {
		log.info("Getting cached data from redis: {}", key);
		Optional<CacheData> cacheData = cacheDataRepository.findById(key);
		log.info("Cached data from redis: {}", cacheData);
		return cacheData;
	}

}
