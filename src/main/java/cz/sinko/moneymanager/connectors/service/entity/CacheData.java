package cz.sinko.moneymanager.connectors.service.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
@RedisHash("cacheData")
@ToString
public class CacheData {

	@Id
	private String key;

	@Indexed
	private String value;

}
