package com.rockson.jsql.test.bean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;

public class PublicBean {
	
	public int int1;
	public String str1;

	
	public static void main(String[] args) throws IntrospectionException {
		System.out.println(PublicBean.class.getFields()[0]);
	}
}
