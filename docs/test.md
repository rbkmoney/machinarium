## Тестирование


## Интеграционные тесты

Создаем файл `machinegun/config.yaml`, где указываем вместо `{service_name}` ваш сервис для регистрации в machinegun

[Актуальная версия config.yaml](https://github.com/rbkmoney/machinegun/blob/master/config/config.yaml)


```
service_name: machinegun
erlang:
  cookie: "mg_cookie"
  ipv6: false
  disable_dns_cache: false
woody_server:
  ip: "::"
  port: 8022
  keep_alive_timeout: 60s
limits:
  process_heap: 2M
  disk:
    path: "/"
    value: 99%
  memory:
    type: cgroups
    value: 90%
logging:
  root: /var/log/machinegun
  crash_log: crash.log
  json_log: log.json
  level: debug
namespaces:
  cashreg:
    event_sinks:
      machine:
        type: machine
        machine_id: {service_name}
    default_processing_timeout: 30s
    timer_processing_timeout: 60s
    reschedule_timeout: 60s
    processor:
      url: http://host.docker.internal:8022/v1/processor
      pool_size: 50
snowflake_machine_id: 1
storage:
  type: memory

```

> I WANT TO CONNECT FROM A CONTAINER TO A SERVICE ON THE HOST
> The host has a changing IP address (or none if you have no network access). From 18.03 onwards our recommendation is to connect to the special DNS name `host.docker.internal`, which resolves to the internal IP address used by the host. This is for development purpose and will not work in a production environment outside of Docker Desktop for Mac.

Создаем абстрактный класс и наследуемся в тестах от него

[Актуальную версию tag-а](https://github.com/rbkmoney/machinegun/commits/master)

```
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = DEFINED_PORT)
@ContextConfiguration(classes = Application.class, initializers = AbstractIntegrationTest.Initializer.class)
public abstract class AbstractIntegrationTest {

    public final static String MG_IMAGE = "dr2.rbkmoney.com/rbkmoney/machinegun";
    public final static String MG_TAG = "5b85e3c73041e5cbcfcc35c465cf14214163389b";

    @ClassRule
    public static GenericContainer machinegunContainer = new GenericContainer(MG_IMAGE + ":" + MG_TAG)
            .withExposedPorts(8022)
            .withClasspathResourceMapping(
                    "/machinegun/config.yaml",
                    "/opt/machinegun/etc/config.yaml",
                    BindMode.READ_ONLY
            )
            .waitingFor(
                    new HttpWaitStrategy()
                            .forPath("/health")
                            .forStatusCode(200)
            );

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "service.mg.automaton.url=http://" + machinegunContainer.getContainerIpAddress() + ":" + machinegunContainer.getMappedPort(8022) + "/v1/automaton"
            ).applyTo(configurableApplicationContext);
        }
    }

}
```

