package com.lizikj.cache.config;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

/**
 * redis -kryo序列器
 * 
 * @author liaojw 
 * @date 2018年1月9日 下午6:23:08
 */
public class KryoRedisSerializer<T> implements RedisSerializer<T> {
	private Kryo kryo = new Kryo();
	
	@Override
	public byte[] serialize(T t) throws SerializationException {
		if(null == t){
			return null;
		}
		
		byte[] buffer = new byte[2048];
		Output output = new Output(buffer);
		kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
		kryo.writeClassAndObject(output, t);
		return output.getBuffer();
	}

	@Override
	public T deserialize(byte[] bytes) throws SerializationException {
		if(null == bytes || bytes.length == 0){
			return null;
		}
		
		Input input = new Input(bytes);
		kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
		T t = (T) kryo.readClassAndObject(input);
		return t;
	}

}
