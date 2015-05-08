package com.rockson.sqls;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface ResultSetMap<B> {
	List<B> map(ResultSet resultSet, Class<B> clazz) throws SQLException;

	B mapOne(ResultSet resultSet, Class<B> clazz) throws SQLException;
}
