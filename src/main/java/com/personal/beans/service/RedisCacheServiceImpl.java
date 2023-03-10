package com.personal.beans.service;

import com.personal.beans.models.BaseEntity;
import com.personal.beans.service.contracts.RedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.personal.beans.service.constants.LoggerConstants.DELETE_REDIS_CACHE_AT_TIME_TEMPLATE;
import static com.personal.beans.service.constants.ServiceConstants.TIME_FORMAT_TEMPLATE;

@Service
@Slf4j
public class RedisCacheServiceImpl implements RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisCacheServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean containsKey(String key) {
        return Boolean.TRUE.equals(this.redisTemplate.opsForHash().getOperations().hasKey(key));
    }

    @Override
    public <T> List<T> retrieve(String key, Class<T> tClazz) {
        return this.redisTemplate
                .opsForHash()
                .entries(key)
                .values()
                .stream()
                .map(tClazz::cast)
                .collect(Collectors.toList());
    }

    @Override
    public void save(String key, int entity) {
        this.redisTemplate.opsForHash().put(key, String.valueOf(entity), entity);
    }

    @Override
    public <T> void save(String key, List<T> entities) {
        entities.forEach(current -> this.save(key, current));
    }

    @Override
    public <T> void save(String key, T entity) {
        this.redisTemplate.opsForHash().put(key, ((BaseEntity) entity).getName(), entity);
    }

    @Scheduled(cron = "0 */2 * ? * *")
    public void cleanRedisCache() {
        Objects.requireNonNull(this.redisTemplate.getConnectionFactory()).getConnection().flushDb();
        log.info(String.format(DELETE_REDIS_CACHE_AT_TIME_TEMPLATE,
                DateTimeFormatter.ofPattern(TIME_FORMAT_TEMPLATE).format(LocalDateTime.now())));
    }
}
