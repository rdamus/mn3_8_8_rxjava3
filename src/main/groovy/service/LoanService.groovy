package service


import client.LoanOperations
import groovy.util.logging.Slf4j
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiConsumer
import jakarta.inject.Singleton
import model.IngestReport

import java.util.concurrent.TimeUnit

@Slf4j
@Singleton
class LoanService implements LoanOperations {

    def maxReportCnt = 10
    def simulateProcessingMs = 1000L//1 sec to "process"
    List<IngestReport> createReports() {
        (0..<maxReportCnt).collect { new IngestReport(IngestReport.State.PROCESSING, it, "NONE") }
    }

    @Override
    Flowable<IngestReport> ingestFromFlowableIterable(Long delayMs) {
        log.info "ingest, but simulating a long delay for:$delayMs milliseconds"
        def reportList = createReports()
        Flowable.fromIterable(reportList).doOnEach(report ->
                log.info "emitting report:$report"
        ).delay(delayMs, TimeUnit.MILLISECONDS)
    }

    @Override
    Flowable<IngestReport> ingestFromFlowableGenerate(Long delayMs) {
        log.info "ingest, but simulating a long pre-process task for:$delayMs milliseconds"
        sleep(delayMs)
        //reset the number of reports for each call
        def reportList = createReports()
        Iterator<IngestReport> reportIterator = reportList.iterator()
        Flowable<IngestReport>.generate({ -> reportIterator }, { iterator, emitter ->
            log.info "generate - reportIterator.hasNext():${iterator.hasNext()}"
            sleep(simulateProcessingMs)
            if (iterator.hasNext()) {
                def report = iterator.next()
                emitter.onNext(report)
            } else {
                def finalReport = new IngestReport(
                        state: IngestReport.State.COMPLETE,
                        id: reportList.size(),
                        message: "complete!")
                emitter.onNext(finalReport)
                emitter.onComplete()
            }
        } as BiConsumer) as Flowable<IngestReport>
    }

    @Override
    Single<Integer> count() {
        Single.just(maxReportCnt)
    }
}
