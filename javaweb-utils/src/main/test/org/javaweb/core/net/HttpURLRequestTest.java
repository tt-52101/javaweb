package org.javaweb.core.net;

import org.junit.Test;

import java.net.MalformedURLException;

public class HttpURLRequestTest {

	@Test
	public void get() {
		try {
			HttpResponse response = new HttpURLRequest("http://javaweb.org/").data("p", "122").get();

			System.out.println(response.body());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void post() {

	}

}