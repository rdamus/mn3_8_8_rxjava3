package com.example

import client.LoanClient
import groovy.util.logging.Slf4j
import io.micronaut.http.client.exceptions.ReadTimeoutException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import model.IngestReport
import spock.lang.Specification

@Slf4j
@MicronautTest
class LoanClientSpec extends Specification{
    @Inject
    LoanClient loanClient

    def simulatedTaskMs = 11000L//milliseconds

    def "test ingest as Flowable fromIterable"(){
        given:
        def taskDuration = 0L
        when:
        def reportCnt = loanClient.count().blockingGet()
        def iter = loanClient.ingestFromFlowableIterable(taskDuration).blockingIterable().iterator()
        log.info "expect this many reports:$reportCnt"
        then:
        while(iter.hasNext()){
            def report = iter.next()
            log.info "received report from flowable: $report"
            assert report.state.is(IngestReport.State.PROCESSING)
        }
        when:"we set the task duration to simulate a delay after processing"
        taskDuration = simulatedTaskMs
        iter = loanClient.ingestFromFlowableIterable(taskDuration).blockingIterable().iterator()
        while(iter.hasNext()){
            def report = iter.next()
            log.info "received report from flowable: $report"
            assert report.state.is(IngestReport.State.PROCESSING)
        }
        then:"the ReadTimeoutException occurs despite this being a streaming request"
        thrown(ReadTimeoutException)
    }

    def "test ingest as Flowable fromIterable with blockingNext()"(){
        given:
        def taskDuration = 0L
        when:
        def reportCnt = loanClient.count().blockingGet()
        def iter = loanClient.ingestFromFlowableIterable(taskDuration).blockingNext().iterator()
        log.info "expect this many reports:$reportCnt"
        then:
        while(iter.hasNext()){
            def report = iter.next()
            log.info "received report from flowable: $report"
            assert report.state.is(IngestReport.State.PROCESSING)
        }
        when:"we set the task duration to simulate a delay after processing"
        taskDuration = simulatedTaskMs
        iter = loanClient.ingestFromFlowableIterable(taskDuration).blockingNext().iterator()
        while(iter.hasNext()){
            def report = iter.next()
            log.info "received report from flowable: $report"
            assert report.state.is(IngestReport.State.PROCESSING)
        }
        then:"the ReadTimeoutException occurs despite this being a streaming request"
        thrown(ReadTimeoutException)
    }

    def "test ingest as Flowable.generate"(){
        given:
        def taskDuration = 0L
        when:
        def reportCnt = loanClient.count().blockingGet()
        def iter = loanClient.ingestFromFlowableGenerate(taskDuration).blockingIterable().iterator()
        log.info "expect this many reports:$reportCnt"
        then:
        while(iter.hasNext()){
            def report = iter.next()
            log.info "received report from flowable: $report"
            if( !iter.hasNext() ){
                //we emit a final object as COMPLETE
                assert report.state.is(IngestReport.State.COMPLETE)
            }else{
                assert report.state.is(IngestReport.State.PROCESSING)
            }
        }
        when:"we set the task duration to simulate pre-processing"
        taskDuration = simulatedTaskMs
        iter = loanClient.ingestFromFlowableGenerate(taskDuration).blockingIterable().iterator()
        while(iter.hasNext()){
            def report = iter.next()
            log.info "received report from flowable: $report"
            if( !iter.hasNext() ){
                //we emit a final object as COMPLETE
                assert report.state.is(IngestReport.State.COMPLETE)
            }else{
                assert report.state.is(IngestReport.State.PROCESSING)
            }
        }
        then:"the ReadTimeoutException occurs despite this being a streaming request"
        thrown(ReadTimeoutException)
    }

    def "test ingest as Flowable.generate with blockingNext()"(){
        given:
        def taskDuration = 0L
        when:
        def reportCnt = loanClient.count().blockingGet()
        def iter = loanClient.ingestFromFlowableGenerate(taskDuration).blockingNext().iterator()
        log.info "expect this many reports:$reportCnt"
        then:
        while(iter.hasNext()){
            def report = iter.next()
            log.info "received report from flowable: $report"
            if( !iter.hasNext() ){
                //we emit a final object as COMPLETE
                assert report.state.is(IngestReport.State.COMPLETE)
            }else{
                assert report.state.is(IngestReport.State.PROCESSING)
            }
        }
        when:"we set the task duration to simulate pre-processing"
        taskDuration = simulatedTaskMs
        iter = loanClient.ingestFromFlowableGenerate(taskDuration).blockingNext().iterator()
        while(iter.hasNext()){
            def report = iter.next()
            log.info "received report from flowable: $report"
            if( !iter.hasNext() ){
                //we emit a final object as COMPLETE
                assert report.state.is(IngestReport.State.COMPLETE)
            }else{
                assert report.state.is(IngestReport.State.PROCESSING)
            }
        }
        then:"the ReadTimeoutException occurs despite this being a streaming request"
        thrown(ReadTimeoutException)
    }
}
