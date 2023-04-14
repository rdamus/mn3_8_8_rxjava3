package client

import io.micronaut.http.client.annotation.Client

@Client("/loan")
interface LoanClient extends LoanOperations{
}