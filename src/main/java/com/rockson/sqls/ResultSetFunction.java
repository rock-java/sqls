package com.rockson.sqls;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@FunctionalInterface
public interface ResultSetFunction<T> {
	T apply(ResultSet resultSet) throws SQLException;
}
