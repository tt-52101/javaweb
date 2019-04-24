package org.javaweb.core.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yz on 2017/2/20.
 *
 * @author yz
 */
public class GsonUtils {

	private static final JsonObjectTypeAdapter TYPE_ADAPTER = new JsonObjectTypeAdapter();

	/**
	 * 创建一个禁用HTML转义、禁止int自动转Double的Gson对象
	 *
	 * @return
	 */
	public static Gson createDefaultGson() {
		Gson gson = new GsonBuilder().disableHtmlEscaping().
				registerTypeAdapter(List.class, TYPE_ADAPTER).
				registerTypeAdapter(Map.class, TYPE_ADAPTER).
				create();

		return gson;
	}

	/**
	 * 适配JSON序列化数据类型:https://stackoverflow.com/questions/36508323/how-can-i-prevent-gson-from-converting-integers-to-doubles
	 */
	public static final class JsonObjectTypeAdapter extends TypeAdapter<Object> {

		private final TypeAdapter<Object> delegate = new Gson().getAdapter(Object.class);

		@Override
		public Object read(JsonReader in) throws IOException {
			JsonToken token = in.peek();

			switch (token) {
				case BEGIN_ARRAY:
					List<Object> list = new ArrayList<Object>();
					in.beginArray();

					while (in.hasNext()) {
						list.add(read(in));
					}

					in.endArray();
					return list;

				case BEGIN_OBJECT:
					Map<String, Object> map = new LinkedTreeMap<String, Object>();
					in.beginObject();

					while (in.hasNext()) {
						map.put(in.nextName(), read(in));
					}

					in.endObject();
					return map;

				case STRING:
					return in.nextString();

				case NUMBER:
					Number num = in.nextDouble();

					if (Math.ceil(num.doubleValue()) == num.longValue())
						return num.longValue();
					else {
						return num.doubleValue();
					}

				case BOOLEAN:
					return in.nextBoolean();

				case NULL:
					in.nextNull();
					return null;

				default:
					throw new IllegalStateException();
			}
		}

		@Override
		public void write(JsonWriter out, Object value) throws IOException {
			if (value == null) {
				out.nullValue();

				return;
			}

			delegate.write(out, value);
		}
	}

}
