### Примеры

Примеры по реализации серверной части


### Серверная часть

Создаем сервлеты с обработчиками

Для Automaton
```
@WebServlet("/v1/processor")
@RequiredArgsConstructor
public class ProcessorEndpoint extends GenericServlet {

    private Servlet thriftServlet;

    private final ProcessorSrv.Iface processorHandler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        thriftServlet = new THServiceBuilder()
                .build(ProcessorSrv.Iface.class, processorHandler);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        thriftServlet.service(req, res);
    }

}
```

Для Event sink
```
@WebServlet("/v1/event_sink")
@RequiredArgsConstructor
public class EventSinkEndpoint extends GenericServlet {

    private Servlet thriftServlet;

    private final EventSinkSrv.Iface processorHandler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        thriftServlet = new THServiceBuilder()
                .build(EventSinkSrv.Iface.class, processorHandler);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        thriftServlet.service(req, res);
    }

}
```

Далее создаем обработчик, логику в методах реализуем согласно поставленной задаче

Пример для Automaton
```
@Slf4j
@Component
public class ProcessorHandler extends AbstractProcessorHandler<Value, Change> {

    private final NameOurService nameOurService;

    public ProcessorHandler(NameOurService nameOurService) {
        super(Value.class, Change.class);
        this.nameOurService = nameOurService;
    }

    @Override
    protected SignalResultData<Change> processSignalInit(String namespace, String machineId, Value args) {
        log.info("Trying to process signal init, namespace='{}', machineId='{}'", namespace, machineId);
        SourceData sourceData = nameOurService.action(sourceType);
        SignalResultData<Change> signalResultData = new SignalResultData<>(
                Collections.singletonList(buildCreatedChange(sourceData)),
                buildComplexActionWithDeadline(sourceData.getNextExecutionTime(), buildLastEventHistoryRange())
        );
        log.info("Response: {}", signalResultData);
        return signalResultData;
    }

    @Override
    protected SignalResultData<Change> processSignalTimeout(String namespace, String machineId, List<TMachineEvent<Change>> tMachineEvents) {
        log.info("Trying to process signal timeout, namespace='{}', machineId='{}', events='{}'", namespace, machineId, tMachineEvents);
        Change change = getLastEvent(tMachineEvents);
        if (change == null) {
            throw new IllegalStateException("Failed to process signal timeout because previous changes not found");
        }
        SourceData sourceData = nameOurService.action(change);
        SignalResultData<Change> signalResultData = new SignalResultData<>(
                Collections.singletonList(buildCreatedChange(sourceData)),
                buildComplexActionWithDeadline(sourceData.getNextExecutionTime(), buildLastEventHistoryRange())
        );
        log.info("Response: {}", signalResultData);
        return signalResultData;
    }

    @Override
    protected CallResultData<Change> processCall(String namespace, String machineId, Value args, List<TMachineEvent<Change>> tMachineEvents) {
        return new CallResultData<>(getLastEvent(tMachineEvents), Collections.emptyList(), new ComplexAction());
    }

    private Change getLastEvent(List<TMachineEvent<Change>> tMachineEvents) {
        if (tMachineEvents.isEmpty()) {
            return null;
        }
        return tMachineEvents.get(tMachineEvents.size() - 1).getData();
    }

}
```

Пример для Event sink
```
@Slf4j
@Component
public class EventSinkHandler implements EventSinkSrv.Iface {

    private final EventSinkClient<Change> eventSinkClient;

    public EventSinkHandler(EventSinkClient<Change> eventSinkClient) {
        this.eventSinkClient = eventSinkClient;
    }

    @Override
    public List<SinkEvent> getEvents(EventRange eventRange) throws TException {
        List<TSinkEvent<Change>> events;
        if (eventRange.isSetAfter()) {
            events = eventSinkClient.getEvents(eventRange.getLimit(), eventRange.getAfter());
        } else {
            events = eventSinkClient.getEvents(eventRange.getLimit());
        }

        return events.stream()
                .map(ProtoUtil::toSinkEvent)
                .collect(Collectors.toList());
    }

    @Override
    public long getLastEventID() throws NoLastEvent, TException {
        return eventSinkClient.getLastEventId().orElse(Long.MIN_VALUE);
    }

}
```
