package com.rockson.jsql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.rockson.jsql.test.bean.Bean;
import com.sun.xml.internal.ws.org.objectweb.asm.Type;

public class FixedResultSetMap implements ResultSetMap<Bean> {

	public static void main(String[] args) {
		System.out.println(Type.getMethodDescriptor(FixedResultSetMap.class.getMethods()[1]));
	}

	@Override
	public List<Bean> map(ResultSet resultSet, Class<Bean> clazz) throws SQLException {
		try {
			List<Bean> result = new LinkedList<Bean>();
			Bean bean = null;
			while (resultSet.next()) {
				bean = new Bean();
				bean.setI(resultSet.getInt("i"));
				// user.setName(resultSet.getString("name"));
				result.add(bean);
			}
			return result;
		} finally {
			resultSet.close();

		}
	}

	@Override
	public Bean mapOne(ResultSet resultSet, Class<Bean> clazz) throws SQLException {
		Bean bean = null;
		if (resultSet.next()) {
			bean = new Bean();
			bean.setI(resultSet.getInt("id"));
		}
		resultSet.close();
		return bean;

	}

}
