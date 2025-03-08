package voteapp.geostorageservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import voteapp.geostorageservice.dto.CityDto;
import voteapp.geostorageservice.model.City;
import voteapp.geostorageservice.utils.CityMapper;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisCityService {

    private final RedisTemplate<String, Object> redisTemplate;

    // Метод для очистки кэша
    public void clearCache() {
        // Удаление всех данных о городах
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            Set<String> keys = redisTemplate.keys("city:*"); // Получаем все ключи для городов
            for (String key : keys) {
                redisTemplate.delete(key);  // Удаляем все ключи для городов
            }
            return null;
        });
    }


    // Метод для пакетной вставки городов в Redis
    public void batchSave(List<City> cities) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (City city : cities) {
                CityDto cityDto = CityMapper.cityToCityDto(city);
                // Устанавливаем ключ в формате city:{cityId}
                redisTemplate.opsForValue().set("city:" + cityDto.getId(), cityDto);
            }
            return null;
        });
    }

    public List<CityDto> getCitiesByCountryId(Long countryId) {
        Set<String> keys = redisTemplate.keys("city:*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }

        List<Object> cities = redisTemplate.opsForValue().multiGet(keys);
        return cities.stream()
                .filter(obj -> obj instanceof CityDto)
                .map(obj -> (CityDto) obj)
                .filter(city -> city.getCountryId().equals(countryId))
                .collect(Collectors.toList());
    }
}
