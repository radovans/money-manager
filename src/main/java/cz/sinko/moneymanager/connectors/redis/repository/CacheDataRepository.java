package cz.sinko.moneymanager.connectors.redis.repository;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

import cz.sinko.moneymanager.connectors.service.entity.CacheData;

@Repository
public interface CacheDataRepository extends KeyValueRepository<CacheData, String> {

}
