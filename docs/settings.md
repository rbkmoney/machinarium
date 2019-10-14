## Настройки


## Общие

Добавить в `pom.xml` в зависимости

```
<dependency>
    <groupId>com.rbkmoney</groupId>
    <artifactId>machinarium</artifactId>
    <version>${machinarium.version}</version>
</dependency>
```

В зависимостях также должны быть указаны
```
<dependency>
    <groupId>com.rbkmoney.woody</groupId>
    <artifactId>woody-thrift</artifactId>
    <version>${woody-thrift.version}</version>
</dependency>
<dependency>
    <groupId>com.rbkmoney.gec</groupId>
    <artifactId>serializer</artifactId>
    <version>${serializer.version}</version>
</dependency>
```

### Использование

Для того, чтобы начать пользоваться библиотекой после подключения, необходимо:

в `application.yml` добавить:

```
service:
  mg:
    automaton:
      url: http://localhost:8080/v1/automaton
      namespace: project_namespace
    eventSink:
      url: http://localhost:8022/v1/event_sink
      sinkId: sink_id_project
    networkTimeout: 5000
```

Создать файлы конфигурации

Пример для клиента `automaton`
```
@Configuration
public class AutomationConfiguration {

    @Bean
    public AutomatonSrv.Iface automatonThriftClient(
            @Value("${service.mg.automaton.url}") Resource resource,
            @Value("${service.mg.networkTimeout}") int networkTimeout
    ) throws IOException {
        return new THSpawnClientBuilder()
                .withAddress(resource.getURI())
                .withNetworkTimeout(networkTimeout)
                .build(AutomatonSrv.Iface.class);
    }

    @Bean
    public AutomatonClient<com.rbkmoney.machinegun.msgpack.Value, Change> automatonClient(
            @Value("${service.mg.automaton.namespace}") String namespace,
            AutomatonSrv.Iface automatonThriftClient
    ) {
        return new TBaseAutomatonClient<>(automatonThriftClient, namespace, Change.class);
    }

}
```

Пример для клиента `event sink`
```
@Configuration
public class EventSinkConfiguration {
    
    @Bean
    public EventSinkSrv.Iface eventSinkThriftClient(
            @Value("${service.mg.eventSink.url}") Resource resource,
            @Value("${service.mg.networkTimeout}") int networkTimeout
    ) throws IOException {
        return new THSpawnClientBuilder()
                .withAddress(resource.getURI())
                .withNetworkTimeout(networkTimeout)
                .build(EventSinkSrv.Iface.class);
    }

    @Bean
    public EventSinkClient<Change> eventSinkClient(
            @Value("${service.mg.eventSink.sinkId}") String eventSinkId,
            EventSinkSrv.Iface eventSinkThriftClient
    ) {
        return new TBaseEventSinkClient<>(eventSinkThriftClient, eventSinkId, Change.class);
    }

}
```

Подключить в нужном месте через `Autowired`

```
@Autowired
AutomatonClient<Value, Change> automatonClient;
```

```
@Autowired
EventSinkClient<Change> eventSinkClient;
```


Далее вызывать методы определенные в интерфейсе