# Подключение к gateway
Зайти в \gateway\config\GatewayConfig.java и добавить новый route в routeLocator,
например: 
```
.route("social-network-geo", r -> r.path("/api/v1/geo/**").uri("lb://social-network-geo"))
```
Указать путь, шаблон эендпоинтов, которые будут перенаправлены на указанный сервис. 
В качестве uri сервиса указать название сервиса через lb://