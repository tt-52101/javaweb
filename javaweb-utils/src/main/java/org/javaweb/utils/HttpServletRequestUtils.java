/*
 * Copyright yz 2016-01-17  Email:admin@javaweb.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.javaweb.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author yz 2016-01-17
 */
public class HttpServletRequestUtils {

	private static final Logger LOG = Logger.getLogger("info");

	protected static HttpServletRequestUtils httpServletRequestUtils = new HttpServletRequestUtils();

	/**
	 * 获取web目录,Weblogic 默认以war包部署的时候不能用getRealPath
	 * getResource("/")获取的是当前应用所在的类路径，截取到WEB-INF 之后的路径就是当前应用的web根目录了
	 *
	 * @param request
	 * @return
	 */
	public static String getDocumentRoot(HttpServletRequest request) {
		String webRoot = request.getSession().getServletContext().getRealPath("/");

		if (webRoot == null) {
			URL url = httpServletRequestUtils.getClass().getClassLoader().getResource("/");

			if (url == null) {
				try {
					url = new File(".").toURI().toURL();
				} catch (MalformedURLException e) {
					LOG.info(e.toString());
				}
			}

			webRoot = URLCanonicalizerUtils.getCanonicalURL(url.toString());
			webRoot = webRoot.substring(0, webRoot.contains("WEB-INF") ? webRoot.lastIndexOf("WEB-INF") : webRoot.length());
		}

		return FileUtils.fileSplitHandle(webRoot);
	}

	/**
	 * 获取类资源所在路径
	 *
	 * @return
	 * @throws IOException
	 */
	public static String getClassPathResource() throws IOException {
		Resource resource = new ClassPathResource("/");
		return resource.getFile().getAbsolutePath();
	}

	/**
	 * 获取传入文件在类资源目录的绝对路径
	 *
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String getClassPathResource(String path) throws IOException {
		Resource resource = new ClassPathResource(path);
		return resource.getFile().getAbsolutePath();
	}

	/**
	 * 获取请求文件的绝对路径，getServletPath()更加准确。访问目录可以获取到具体的索引文件
	 * 如访问/test,test目录下存在index.jsp getServletPath()可获取到只有的请求文件URL
	 * getDocumentRoot会返回一个标准目录路径结尾包含"/"
	 *
	 * @param request
	 * @return
	 */
	public static String getHttpRequestFileRealPath(HttpServletRequest request) {
		String documentRoot = getDocumentRoot(request);
		String path         = documentRoot + request.getServletPath();

		return path.replaceAll("/+", "/");
	}

	/**
	 * 如果经过nginx反向代理后可能会获取到一个本地的IP地址如:127.0.0.1、192.168.1.100
	 * 配置nginx把客户端真实IP地址放到nginx请求头中的x-real-ip或x-forwarded-for的值
	 *
	 * @param request
	 * @return
	 */
	public static String getRemoteAddr(HttpServletRequest request) {
		String ip            = request.getRemoteAddr();
		String xRealIp       = request.getHeader("x-real-ip");
		String xForwardedFor = request.getHeader("x-forwarded-for");

		// 如果获取到的IP是本机或内网IP则取header中的IP
		if (ip.equals(request.getLocalAddr()) || IPV4Utils.isLanIp(ip)) {
			ip = IPV4Utils.isValid(xRealIp) ? xRealIp : IPV4Utils.isValid(xForwardedFor) ? xForwardedFor : ip;
		}

		return ip.startsWith("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
	}

	/**
	 * 替换危险内容首字母为HTML实体
	 *
	 * @param str
	 * @param key
	 * @return
	 */
	public static String getAsciiEncoding(String str, String key) {
		for (String s : key.split(",")) {
			str = str.replaceAll(
					"(?i)(" + s + ")",
					s.replace("" + s.charAt(0), "&#" + (int) s.charAt(0) + ";")
			);
		}

		return str;
	}

	/**
	 * 过滤ServletRequest的getParameter方法,把参数值HTML实体化
	 *
	 * @param request
	 * @param name
	 * @return
	 */
	public static String getParameter(HttpServletRequest request, String name) {
		Map<String, String[]> parameterMap = request.getParameterMap();

		if (parameterMap.containsKey(name)) {
			String[] strs = parameterMap.get(name);

			if (strs.length == 1) {
				return htmlSpecialChars(strs[0]);
			}
		}

		return request.getParameter(name);
	}

	/**
	 * htmlSpecialChars 函数把一些预定义的字符转换为 HTML 实体
	 *
	 * @param content
	 * @return
	 */
	public static String htmlSpecialChars(String content) {
		if (content != null) {
			char[]        arr = content.toCharArray();
			StringBuilder sb  = new StringBuilder();

			for (char c : arr) {
				switch (c) {
					case '&':
						sb.append("&amp;");
						break;
					case '"':
						sb.append("&quot;");
						break;
					case '\'':
						sb.append("&#039;");
						break;
					case '<':
						sb.append("&lt;");
						break;
					case '>':
						sb.append("&gt;");
						break;
					default:
						sb.append(c);
						break;
				}
			}

			return sb.toString();
		}

		return null;
	}

	/**
	 * queryString转义
	 *
	 * @param queryString
	 * @return
	 */
	public static String queryStringEscape(String queryString) {
		if (queryString != null) {
			char[]        arr = queryString.toCharArray();
			StringBuilder sb  = new StringBuilder();

			for (char c : arr) {
				switch (c) {
					case '"':
						sb.append("&quot;");
						break;
					case '\'':
						sb.append("&#039;");
						break;
					case '<':
						sb.append("&lt;");
						break;
					case '>':
						sb.append("&gt;");
						break;
					default:
						sb.append(c);
						break;
				}
			}

			return sb.toString();
		}

		return queryString;
	}

	/**
	 * 把一个数组所有的字符串转换为 HTML 实体
	 *
	 * @param obj
	 */
	public static void htmlSpecialChars(Object[] obj) {
		for (int i = 0; i < obj.length; i++) {
			if (obj[i] != null) {
				obj[i] = htmlSpecialChars(obj[i].toString());
			}
		}
	}

	/**
	 * 获取当前线程上下文中的请求对象 HttpServletRequest
	 *
	 * @return
	 */
	public static HttpServletRequest getCurrentHttpServletRequest() {
		try {
			return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取当前线程上下文中的响应对象 HttpServletResponse
	 *
	 * @return
	 */
	public static HttpServletResponse getCurrentHttpServletResponse() {
		try {
			return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取当前线程上下文中的Session对象 HttpSession
	 *
	 * @return
	 */
	public static HttpSession getCurrentHttpSession() {
		HttpServletRequest request = getCurrentHttpServletRequest();

		if (request != null) {
			return getCurrentHttpServletRequest().getSession();
		}

		return null;
	}

	/**
	 * 获取请求请求token,如果session中没有有效的token 自动生成一个32位的token字符串
	 *
	 * @param request
	 * @return
	 */
	public static String getToken(HttpServletRequest request) {
		Object obj   = request.getSession().getAttribute("token");
		String token = null;

		if (StringUtils.isNotEmpty(obj)) {
			token = (String) obj;
		} else {
			token = StringUtils.getRandomString(32);
			request.getSession().setAttribute("token", token);
		}

		return token;
	}

	/**
	 * 判断上传的文件名是否合法 验证文件名和后缀
	 *
	 * @param commonsMultipartFile
	 * @return
	 */
	public static boolean isAllowedFileName(CommonsMultipartFile commonsMultipartFile) {
		if (commonsMultipartFile != null) {
			String fileName = commonsMultipartFile.getOriginalFilename();

			if (fileName.indexOf('\u0000') != -1) {
				return false;
			}

			// 文件后缀验证
			String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

			return !Pattern.compile(
					"asp|asa|cer|jsp|php", Pattern.CASE_INSENSITIVE | Pattern.DOTALL
			).matcher(fileExt).find();
		}

		return true;
	}

	public static MultipartFile uploadCommonsMultipartFile(HttpServletRequest request,
	                                                       CommonsMultipartFile docFile) {

		return uploadCommonsMultipartFile(request, docFile, false);
	}

	/**
	 * 上传文件并返回http绝对路径,如果anySuffixFile设置为true且uploads目录
	 * 未限制jsp/jspx类的脚本文件解析、执行那么黑客可以获取服务器权限并控制服务器。
	 *
	 * @param request
	 * @param docFile
	 * @param anySuffixFile 是否允许上传任意后缀文件
	 * @return
	 */
	public static MultipartFile uploadCommonsMultipartFile(
			HttpServletRequest request, CommonsMultipartFile docFile, boolean anySuffixFile) {

		MultipartFile multipart = new MultipartFile();

		if (anySuffixFile || isAllowedFileName(docFile)) {
			ServletContext context 	= request.getSession().getServletContext();
			String dateDirName = new SimpleDateFormat("yyyyMMdd").format(new Date());
			String savePath    = context.getRealPath("/") + "uploads/files/" + dateDirName + "/";
			String saveUrl     = request.getContextPath() + "/uploads/files/" + dateDirName + "/";
			String fileName    = docFile.getOriginalFilename();
			String fileExt     = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

			// unix时间戳+随机生成文件名
			SimpleDateFormat df           = new SimpleDateFormat("yyyyMMddHHmmss");
			String           newFileName  = df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt;
			File             uploadedFile = new File(savePath, newFileName);

			if (!uploadedFile.getParentFile().exists()) {
				uploadedFile.getParentFile().mkdirs();
			}

			try {
				docFile.transferTo(uploadedFile);
			} catch (IOException e) {
				LOG.info("文件上传异常:" + e.toString());
			}

			multipart.setOriginalFilename(docFile.getOriginalFilename());
			multipart.setFilename(newFileName);
			multipart.setSize(docFile.getSize());
			multipart.setPath(uploadedFile.getAbsolutePath());
			multipart.setUrl(saveUrl + newFileName);
		}

		return multipart;
	}

	public static String getWebBaseUrlPath(HttpServletRequest request) {
		String portStr = (request.getServerPort() != 80 && request.getServerPort() != -1) ?
				":" + request.getServerPort() : "";

		return request.getScheme() + "://" + request.getServerName() + portStr + request.getContextPath() + "/";
	}

}
