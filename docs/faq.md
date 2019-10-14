# FAQ


Вопросы для обсуждения и ряд полученных ответов на них:


`Вопрос:`

> Откуда `machinegun` будет знать, на какой url отправлять запрос после вызова методов `automatonClient`-а

`Ответ:`

При подготовке к выливке надо прописать его в конфигурации.

Пример для сервиса `xrates`

в `sls/wetkitty/macroservice/service/machinegun.sls#L53`

```
rates_host: {{ salt['pillar.get']('wetkitty:macroservice:xrates:service-name') }}
```

в `sls/wetkitty/macroservice/service/files/machinegun.yaml.tpl`

```
rates:
    event_sinks:
        machine:
            type: machine
            machine_id: rates
    processor:
        url: http://{{ rates_host }}:8022/v1/processor
        http_keep_alive_timeout: {{ woody_client_keep_alive }}ms
```


---



`Вопрос:`

> Описание вопроса

`Ответ:`

Описание ответа

---


