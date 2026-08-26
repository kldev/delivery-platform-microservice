package com.pl.platform.svc.idempotency.adapter

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpInputMessage
import java.io.InputStream

class CachedHttpInputMessage(
    private val headers: HttpHeaders,
    private val body: ByteArray
) : HttpInputMessage {

    override fun getHeaders(): HttpHeaders =
        headers

    override fun getBody(): InputStream =
        body.inputStream()
}