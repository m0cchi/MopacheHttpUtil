package net.m0cchi.function;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.m0cchi.value.AtomicType;
import net.m0cchi.value.Element;
import net.m0cchi.value.Environment;
import net.m0cchi.value.Function;
import net.m0cchi.value.NULL.NIL;
import net.m0cchi.value.Value;

public abstract class AbstractHttpFunction extends Function {
	private static final long serialVersionUID = -8901569015587205028L;
	private static String FAIL_CODE = "{\"ok\":false}";

	public AbstractHttpFunction() {
		setArgs("http uri", "http body");
	}

	public abstract String getMethod();

	private HttpURLConnection createConnection(String uri) throws IOException {
		URL url = new URL(uri);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setRequestProperty("Content-Type", "charset=utf-8");
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setChunkedStreamingMode(0);
		return connection;

	}

	private String getResult(InputStream is, int resultCode) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(is);
		InputStreamReader isr = new InputStreamReader(bis, "utf-8");
		BufferedReader br = new BufferedReader(isr);
		StringBuilder builder = new StringBuilder();
		if (resultCode == HttpURLConnection.HTTP_OK) {
			String line;
			while ((line = br.readLine()) != null) {
				builder.append(line);
			}
		} else {
			builder.append(FAIL_CODE);
		}

		return builder.toString();
	}

	public String request(String uri, String body) throws IOException {
		HttpURLConnection connection = createConnection(uri);
		connection.setRequestMethod(getMethod());

		BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
		bos.write(body.getBytes());
		bos.flush();
		connection.getOutputStream().close();

		String result = getResult(connection.getInputStream(), connection.getResponseCode());
		connection.getInputStream().close();
		
		return result;
	}

	@Override
	public Element invoke(Environment environment) {
		Value<?> uriValue = (Value<?>) environment.getValue(getArgs()[0]);
		@SuppressWarnings("unchecked")
		Value<String> body = (Value<String>) environment.getValue(getArgs()[1]);
		Object uri = uriValue.getNativeValue();
		String result = null;
		try {
			result = request(uri.toString(), body.getNativeValue());
		} catch (Throwable e) {
		}

		Element ret = result == null ? NIL.NIL : new Value<String>(AtomicType.LETTER, result);
		return ret;
	}

}
