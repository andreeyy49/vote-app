package voteapp.geostorageservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import voteapp.geostorageservice.model.Country;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisCountryService {

    private final RedisTemplate<String, Object> redisTemplate;

    // Метод для очистки всех данных в кэше Redis
    public void clearCache() {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            // Получаем все ключи, которые начинаются с "country:" и очищаем их
            Set<String> keys = redisTemplate.keys("country:*");
            for (String key : keys) {
                redisTemplate.delete(key);  // Удаляем каждую страну из кэша
            }
            return null;
        });
    }

    public void batchSave(List<Country> countries) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Country country : countries) {
                redisTemplate.opsForValue().set("country:" + country.getId(), country);  // Пакетная вставка в Redis
            }
            return null;
        });
    }

    // Метод для получения всех стран из Redis
    public List<Country> getAllCountries() {
        Set<String> keys = redisTemplate.keys("country:*");  // Получаем все ключи с префиксом "country:"
        if (keys != null && !keys.isEmpty()) {
            List<Object> cachedCountries = redisTemplate.opsForValue().multiGet(keys);  // Получаем все страны по ключам

            // Преобразуем List<Object> в List<Country> с использованием приведения типа
            if (cachedCountries != null) {
                return cachedCountries.stream()
                        .filter(obj -> obj instanceof Country)  // Фильтруем объекты типа Country
                        .map(obj -> (Country) obj)  // Кастуем каждый объект в Country
                        .collect(Collectors.toList());  // Собираем результат в список
            }
        }
        return null;  // Если стран нет в кэше
    }


    // Метод для получения страны по ID из Redis
    public Country getCountryById(Long countryId) {
        String key = "country:" + countryId;  // Формируем ключ для Redis
        ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();

        // Ищем страну по ключу
        Object cachedCountry = valueOps.get(key);

        if (cachedCountry != null) {
            return (Country) cachedCountry;  // Возвращаем найденную страну
        }
        return null;  // Если страна не найдена в кэше
    }

}
