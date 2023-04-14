package controller

import client.LoanOperations
import groovy.util.logging.Slf4j
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.micronaut.validation.Validated
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import jakarta.inject.Inject
import model.IngestReport
import service.LoanService


@Slf4j
@Controller("/loan")
@Validated
class LoanController implements LoanOperations{

    @Inject
    LoanService loanService

    LoanController(){}

    @Get("/")
    HttpStatus index() {
        log.info "index"
        return HttpStatus.OK
    }

    @Override
    @ExecuteOn(TaskExecutors.IO)
    Flowable<IngestReport> ingestFromFlowableIterable(Long delayMs) {
        log.info "ingest:$delayMs"
        loanService.ingestFromFlowableIterable(delayMs)
    }

    @Override
    @ExecuteOn(TaskExecutors.IO)
    Flowable<IngestReport> ingestFromFlowableGenerate(Long delayMs) {
        log.info "ingestFromColdFlowableWithLongTask: $delayMs"
        loanService.ingestFromFlowableGenerate(delayMs)
    }

    @Override
    Single<Integer> count() {
        log.info "count"
        loanService.count()
    }
}
