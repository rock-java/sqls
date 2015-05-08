package com.rockson.sqls;

public interface ResultSetMapFactory {
	<B> ResultSetMap<B> build(Class<B> clazz) ; 
}
