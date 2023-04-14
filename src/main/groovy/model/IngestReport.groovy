package model

import groovy.transform.ToString

@ToString(includeNames = true)
class IngestReport {
    enum State{PROCESSING, COMPLETE}
    State state
    Long id
    String message

    IngestReport(){}

    IngestReport(State state, Long id, String message) {
        this.state = state
        this.id = id
        this.message = message
    }
}
