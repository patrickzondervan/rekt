package io.github.devrawr.rekt.decoding.impl.decoders

import io.github.devrawr.rekt.RedisConnection
import io.github.devrawr.rekt.convert.RESPDecodingConverter
import io.github.devrawr.rekt.decoding.exception.ByteLayoutException

object BulkStringDecoder : RESPDecodingConverter<ByteArray>
{
    override fun convert(connection: RedisConnection): ByteArray?
    {
        val length = this.readString(connection.input)
            .decodeToString()
            .toIntOrNull()

        if (length == null // failed to parse the number, probably a wrongly formatted string, or length is higher than the 32-bit integer limit
            || length == -1 // Null Bulk String
        )
        {
            return null // Null Bulk String, just return null (I guess?)
        }

        val buffer = ByteArray(length)
        var index = 0

        while (index < length)
        {
            index += connection.input.read(buffer, index, length - index)
        }

        if (connection.input.read() != '\r'.code || connection.input.read() != '\n'.code)
        {
            throw ByteLayoutException("CRLF scheme is not formatted properly!")
        }

        return buffer
    }
}