package com.rockson.jsql;

public class JsqlClassLoader extends ClassLoader {

	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return super.loadClass(name);
	}
}
