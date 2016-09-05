package net.m0cchi.function;

import net.m0cchi.value.Element;
import net.m0cchi.value.Environment;
import net.m0cchi.value.Value;

public class HttpXXX extends AbstractHttpFunction {
	private static final long serialVersionUID = -4464765272540731870L;

	public HttpXXX() {
		String[] args = new String[getArgs().length + 1];
		args[0] = "http method";
		int i = 1;
		for (String arg : getArgs()) {
			args[i++] = arg;
		}
		setArgs(args);
	}

	@Override
	public Element invoke(Environment environment) {
		@SuppressWarnings("unchecked")
		Value<String> methodValue = (Value<String>) environment.getValue(getArgs()[0]);
		String method = methodValue.getNativeValue();

		return new AbstractHttpFunction() {
			private static final long serialVersionUID = -6411768721652531066L;

			@Override
			public String getMethod() {
				return method;
			}
		}.invoke(environment);
	}

	@Override
	public String getMethod() {
		throw new UnsupportedOperationException();
	}

}
