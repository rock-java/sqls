# sqls
Map sql to java object using asm.

## Examples:
```java
public static void main(String[] args) throws SQLException  {
	Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/db", "root", "xxx");
	
	Map<String,Object> params = new HashMap<>();
	params.put("id" , 1);
	List<User> users = Sqls.query(con, "select id,name from users where id={id}", params, Bean.class);
	User user = Sqls.queryOne(con, "select id,name,addr from users",null, User.class);
	List<Map<String, Object>> r = Sqls.query(con, "select id,name,addr from users", null);
	Map<String, Object> r = Sqls.queryOne(con, "select id,name,addr from users limit 1", null);
	Sqls.execute(con, "insert users(id,name,addr) values({id},{name},{addrs})", user);
	
	//for restful api 
	Sqls.post(con, "test", new Bean(100,new Date(100000)));
	Sqls.get(con, "test", conditions);
	Sqls.put(con, "test",newValues, conditions);
	Sqls.delete(con, "test", conditions);
	
	connection.close();
}
```

## Features:
1. Faster!
2. Simple!

## License MIT
