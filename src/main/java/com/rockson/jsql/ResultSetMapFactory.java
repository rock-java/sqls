package com.rockson.jsql;

public interface ResultSetMapFactory {
	<B> ResultSetMap<B> build(Class<B> clazz) ; 
}
