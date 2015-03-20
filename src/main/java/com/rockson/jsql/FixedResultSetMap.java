package com.rockson.jsql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.sun.xml.internal.ws.org.objectweb.asm.Type;

public class FixedResultSetMap implements ResultSetMap<User> {
	
	public static void main(String[] args) {
		System.out.println(Type.getMethodDescriptor(FixedResultSetMap.class.getMethods()[1]));
	}

	@Override
	public List<User> map(ResultSet resultSet, Class<User> clazz) throws SQLException {
		try {
			List<User> result = new LinkedList<User>();
			User user = null;
			while (resultSet.next()) {
				user = new User();
				user.setId(resultSet.getInt("id"));
//				user.setName(resultSet.getString("name"));
				result.add(user);
			}
			return result;
		} finally {
			resultSet.close();

		}
	}

//	@Override
//	public User mapOne(ResultSet resultSet, Class<User> clazz) throws SQLException {
//		try {
//			User user = null;
//			while (resultSet.next()) {
//				user = new User();
//				user.setId(resultSet.getInt("id"));
//				user.setName(resultSet.getString("name"));
//			}
//			resultSet.close();
//			return user;
//		} finally {
//			resultSet.close();
//
//		}
//	}

}
