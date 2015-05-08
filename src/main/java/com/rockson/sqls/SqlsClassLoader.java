package com.rockson.sqls;

public class SqlsClassLoader extends ClassLoader {

	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return super.loadClass(name);
	}
}
