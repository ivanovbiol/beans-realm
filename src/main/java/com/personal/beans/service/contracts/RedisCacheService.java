package com.personal.beans.service.contracts;

import com.personal.beans.models.*;

import java.util.List;

public interface RedisCacheService {

    boolean containsKey(String key);

    <T> List<T> retrieve(String key, Class<T> tClazz);

    void save(String key, int entity);

    <T> void save(String key, List<T> entities);

    <T> void save(String key, T entity);
}
