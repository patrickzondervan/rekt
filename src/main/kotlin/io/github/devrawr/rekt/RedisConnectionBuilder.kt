package io.github.devrawr.rekt

import io.github.devrawr.rekt.decoding.Decoder
import io.github.devrawr.rekt.encoding.Encoder
import io.github.devrawr.rekt.pubsub.DataStream
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class RedisConnectionBuilder
{
    private var encoder: Encoder = Redis.encoder
    private var decoder: Decoder = Redis.decoder
    private var dataStream: DataStream = Redis.dataStream

    var inputStream: InputStream? = null
    var outputStream: OutputStream? = null

    fun dataStreamOf(dataStream: DataStream): RedisConnectionBuilder
    {
        return this.apply {
            this.dataStream = dataStream
        }
    }

    fun encoderOf(encoder: Encoder): RedisConnectionBuilder
    {
        return this.apply {
            this.encoder = encoder
        }
    }

    fun decoderOf(decoder: Decoder): RedisConnectionBuilder
    {
        return this.apply {
            this.decoder = decoder
        }
    }

    fun inputStreamOf(inputStream: InputStream): RedisConnectionBuilder
    {
        return this.apply {
            this.inputStream = inputStream
        }
    }

    fun outputStreamOf(outputStream: OutputStream): RedisConnectionBuilder
    {
        return this.apply {
            this.outputStream = outputStream
        }
    }

    fun socketOf(socket: Socket): RedisConnectionBuilder
    {
        return this.apply {
            this.inputStream = socket.getInputStream()
            this.outputStream = socket.getOutputStream()
        }
    }

    fun socketOf(hostname: String, port: Int): RedisConnectionBuilder
    {
        return this.socketOf(
            Socket(
                hostname,
                port
            ).apply {
                this.tcpNoDelay = true
            }
        )
    }

    fun build(): RedisConnection
    {
        if (this.outputStream == null || this.inputStream == null)
        {
            this.socketOf("127.0.0.1", 6379) // should set the outputStream and inputStream fields
        }

        return RedisConnection(
            output = BufferedOutputStream(outputStream!!),
            input = BufferedInputStream(inputStream!!),
            dataStream = dataStream,
            encoder = encoder,
            decoder = decoder,
        )
    }
}