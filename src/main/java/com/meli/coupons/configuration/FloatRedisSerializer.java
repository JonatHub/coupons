package com.meli.coupons.configuration;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import java.nio.ByteBuffer;

public class FloatRedisSerializer implements RedisSerializer<Float> {

    @Override
    public byte[] serialize(Float value) throws SerializationException {
        if (value == null) {
            return new byte[0];
        }
        return ByteBuffer.allocate(4).putFloat(value).array();
    }

    @Override
    public Float deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length != 4) {
            return null;
        }
        return ByteBuffer.wrap(bytes).getFloat();
    }
}
