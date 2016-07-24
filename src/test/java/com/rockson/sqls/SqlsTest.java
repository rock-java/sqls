package com.rockson.sqls;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.rockson.sqls.Sqls;

public class SqlsTest {
	public static class Bean{
		public int id;
		public int i1;
		public Date d1;
		
		public Bean() {
			super();
		}
		public Bean( int i1, Date d1) {
			this.i1 = i1;
			this.d1 = d1;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public int getI1() {
			return i1;
		}
		public void setI1(int i1) {
			this.i1 = i1;
		}
		public Date getD1() {
			return d1;
		}
		public void setD1(Date d1) {
			this.d1 = d1;
		}
		
		@Override
		public String toString() {
			return id+","+i1+","+d1;
		}
		
	}
	
	static Connection con;
	@BeforeClass
	public static void init() throws SQLException{
		con = ConnectionManager.get();
	}
	@AfterClass
	public static void despose() throws SQLException{
		con.close();
	}
	@Test
	public void testQuery() throws SQLException{
		for(int i = 0 ;i<10;i++){
		List<Bean> r = Sqls.query(con, "select id,i1,d1 from test", null, Bean.class);
		System.out.println(r);
		}
	}
	@Test
	public void testQueryOne() throws SQLException{
		for(int i = 0 ;i<10;i++){
			
//		HashMap<String, Integer> params = new HashMap<String, Integer>();
//		params.put("id", 2);
		Bean r = Sqls.queryOne(con, "select id,i1,d1 from test",null, Bean.class);
		}
//		System.out.println(r.id);
	}
	@Test
	public void testQueryMap() throws SQLException{
		for(int i = 0 ;i<10;i++){
			List<Map<String, Object>> r = Sqls.query(con, "select id,i1,d1 from test", null);
		}
//		System.out.println(r);
	}
	@Test
	public void testQueryMapOne() throws SQLException{
		for(int i = 0 ;i<10;i++){
			Map<String, Object> r = Sqls.queryOne(con, "select id,i1,d1 from test", null);
		}
//		System.out.println(r);
	}
	@Test
	public void testExecute() throws SQLException{
		for(int i = 0 ;i<0;i++){
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("i1",i);
			params.put("v1", "v"+i);
			params.put("d1", new Date(System.currentTimeMillis()));
			System.out.println(Sqls.execute(con, "insert test(i1,v1,d1) values({i1},{v1},{d1})", params));
		}
	}
	
	@Test
	public void testPost() throws SQLException{
		HashMap conditions = new HashMap<String , Object>();
		conditions.put("id", 11);
		System.out.println(Sqls.post(con, "test", new Bean(100,new Date(100000))));
	}
	@Test
	public void testGet() throws SQLException{
		HashMap conditions = new HashMap<String , Object>();
		conditions.put("id", 11);
		System.out.println(Sqls.get(con, "test", conditions));
	}
	@Test
	public void testGetString() throws SQLException{
		HashMap conditions = new HashMap<String , Object>();
		System.out.println(Sqls.get(con, "test", "id=1"));
	}
	@Test
	public void testPut() throws SQLException{
		HashMap conditions = new HashMap<String , Object>();
		HashMap newValues = new HashMap<String , Object>();
		conditions.put("id", 11);
		newValues.put("i1", 101);
		System.out.println(Sqls.put(con, "test",newValues, conditions));
	}
	@Test
	public void testDelete() throws SQLException{
		HashMap conditions = new HashMap<String , Object>();
		conditions.put("id", 11);
		System.out.println(Sqls.delete(con, "test", conditions));
	}
}
