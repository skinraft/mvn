package mvn.servlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
	public static void main(String[] args) {
		for (int i = 0; i < 1000; i++) {
			int numOfThreads = Runtime.getRuntime().availableProcessors();
			System.err.println("numOfThreads----"+numOfThreads);
			ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);
			executor.submit(new Callable<String>() {
				@Override
				public String call() throws Exception {
					// TODO Auto-generated method stub
					String result=sendGet();
					System.out.println("I----"+result);
					return result;
				}
			});
		}
	}

	public static String sendGet() {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = "http://localhost:8080/mvn/Userinfo";
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			// 建立实际的连接
			connection.connect();
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}
}
