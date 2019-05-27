package org.javaweb.net;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class MultipartRequestTest {

	@Test
	public void request() {
		try {
			// 设置请求的表单域,可以直接.data(k,v),也可以这样批量set
			Map<String, String> data = new LinkedHashMap<String, String>();
			data.put("action", "queryDetail");
			data.put("wzlb", "DCFS");
			data.put("noticeId", "356-1 and 1<ascii(substr(user, 1, 1))");
			data.put("showwzlb", "");

			String url = "http://localhost:8080/1.php?XDEBUG_SESSION_START=11391";

			// 设置需要传入的流,可以是FileInputStream或者二进制流(ByteArrayInputStream)，只要是InputStream就行。
			// MultipartFileField 中的第一个参数是表单域名称,如果不传值默认是"file",尽量记得设置这个值
			Set<MultipartFileField> fileFields = new LinkedHashSet<MultipartFileField>();
			MultipartFileField      field1     = new MultipartFileField("1.html", new FileInputStream("/Users/yz/1.html"));
			MultipartFileField      field2     = new MultipartFileField("file2", "2.txt", new FileInputStream("/Users/yz/2.txt"));
			fileFields.add(field1);
			fileFields.add(field2);

			HttpResponse response = new MultipartRequest(url).data(data).files(fileFields).request();
			System.out.println(response.body());
			System.out.println(response.getException());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}