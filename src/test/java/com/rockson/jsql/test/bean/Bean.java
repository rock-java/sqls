package com.rockson.jsql.test.bean;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import jdk.nashorn.internal.ir.annotations.Ignore;

import com.rockson.jsql.DbField;

public class Bean {

	private boolean b;
	@DbField("sv")
	private short s;
	private int i;
	@DbField(ignore = true)
	private long l;
	private float f;
	private double d;
	private String str;
	private Date date;
	private Date datetime;
	private Time time;
	@DbField(ignore = true)
	private Timestamp timestamp;
	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	private java.util.Date utilDate;
	
	@Override
	public String toString() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return String.format("b:%b,s:%d,i:%d,l:%d,f:%f,d:%f,str:%s,date:%s,t:%s,dt:%s,ts:%s,ud:%s\r", b ,s , i ,l,f,d,str,date,time,datetime,timestamp , utilDate);
	}

	public java.util.Date getUtilDate() {
		return utilDate;
	}

	public void setUtilDate(java.util.Date utilDate) {
		this.utilDate = utilDate;
	}

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}

	public short getS() {
		return s;
	}

	public void setS(short s) {
		this.s = s;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public long getL() {
		return l;
	}

	public void setL(long l) {
		this.l = l;
	}

	public float getF() {
		return f;
	}

	public void setF(float f) {
		this.f = f;
	}

	public double getD() {
		return d;
	}

	public void setD(double d) {
		this.d = d;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

}
