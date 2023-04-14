## Micronaut 3.8.8 and RxJava3 Project
Demo project to show change in behavior of the reactive streams

In `micronaut-3.3.4` (plugin version `3.3.2`) and `rxjava2` it was possible to do the following:

Declare a `Flowable` method in a parent inerface, and then extend this interface in a declarative client
```groovy
interface LoanOperations {
    @Get(uri = '/ingest/tracker/{loanTapeId}/schema/{schemaId}', processes = MediaType.APPLICATION_JSON_STREAM)
    Flowable<IngestReport> ingestTracker(@NonNull Long loanTapeId, @NonNull Long schemaId)
}

@Client(id='loan', path='${loan.context-path:/}${loan.api.version}/loan')
interface LoanClient extends LoanOperations{}
```

Implement the method and execute the call on the TaskExecutors.IO thread because pre-processing causes this `Flowable`
to not start emitting immediately
```groovy
@Controller('/${loan.api.version}/loan')
class LoanController{
    @ExecuteOn(TaskExecutors.IO)
    Flowable<IngestReport> ingestTracker(@NonNull Long loanTapeId, @NonNull Long schemaId) {
        //pre-processing that takes up to a minute
        //start emitting the IngestReport objects
    }
}
```

i used to be able to call the `Flowable.blockingIterable()` from an app and get an iterator and the code would happily wait 
(without setting the `http.client.read-timeout` or changing the `read-idle-timeout`) and the following would just work:

```groovy
//either of these would work
//def iterator = loanClient.ingestTracker(persistedTape.id, persistedSchema.id).blockingNext().iterator()
def iterator = loanClient.ingestTracker(persistedTape.id, persistedSchema.id).blockingIterable().iterator()
def report = null
while( iterator.hasNext() ){
    report = iterator.next()
    if( report.state.is IngestReport.State.COMPLETE ){
        log.info "COMPLETE!"
    }
}
```

## Micronaut 3.8.8 Documentation

- [User Guide](https://docs.micronaut.io/3.8.8/guide/index.html)
- [API Reference](https://docs.micronaut.io/3.8.8/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/3.8.8/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

- [Shadow Gradle Plugin](https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow)
## Feature rxjava3 documentation

- [Micronaut RxJava 3 documentation](https://micronaut-projects.github.io/micronaut-rxjava3/snapshot/guide/index.html)


## Feature http-client documentation

- [Micronaut HTTP Client documentation](https://docs.micronaut.io/latest/guide/index.html#httpClient)


