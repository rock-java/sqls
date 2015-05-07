package com.rockson.jsql;

import java.sql.ResultSet;

public class PreparedResult {
	public int type;
	public ResultSet resultSet;
	public int updateCount;
	public PreparedResult(int type, int updateCount) {
		this.type = type;
		this.updateCount = updateCount;
	}
	
	public PreparedResult(int type, ResultSet resultSet) {
		this.type = type;
		this.resultSet = resultSet;
	}

	public PreparedResult() {
	}
	
	
	
	
}
