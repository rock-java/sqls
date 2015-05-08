package com.rockson.sqls;

public class ExecuteConfig {
	int fetchSize;
	int maxFieldSize;
	int maxRow;
	int queryTimeOut;
	public ExecuteConfig() {
		super();
	}
	public ExecuteConfig(int fetchSize, int maxFieldSize, int maxRow, int queryTimeOut) {
		super();
		this.fetchSize = fetchSize;
		this.maxFieldSize = maxFieldSize;
		this.maxRow = maxRow;
		this.queryTimeOut = queryTimeOut;
	}
}
