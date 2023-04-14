package client

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Get
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import model.IngestReport

interface LoanOperations {
    @Get(uri = '/ingest/{delayMs}', processes = MediaType.APPLICATION_JSON_STREAM)
    Flowable<IngestReport> ingestFromFlowableIterable(Long delayMs)

    @Get(uri = '/ingest/cold/{delayMs}', processes = MediaType.APPLICATION_JSON_STREAM)
    Flowable<IngestReport> ingestFromFlowableGenerate(Long delayMs)

    @Get(uri = '/count')
    Single<Integer> count()
}