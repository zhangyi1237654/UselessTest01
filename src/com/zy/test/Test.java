package com.zy.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
* @author zy007
* 
*/
public class Test {

	public static void main(String[] args) {
		Filter f1 = new Filter();
		f1.setId(10);
		
		Filter f2 = new Filter();
		f2.setUserName("lucy");
		
		Filter f3 = new Filter();
		f3.setEmail("liu@sina.com,zh@163.com,77777@qq.com");
		
		String sql1 = query(f1);
		String sql2 = query(f2);
		String sql3 = query(f3);
		
		System.out.println(sql1);
		System.out.println(sql2);
		System.out.println(sql3);
		
	}

	
	
	
	private static String query(Filter f) {
		StringBuilder sb = new StringBuilder();
		Class c = f.getClass();
		boolean exists = c.isAnnotationPresent(Table.class);
		if(!exists){
			return null;
		}
		Table t = (Table)c.getAnnotation(Table.class);
		String tableName = t.value();
		sb.append("select * from ").append(tableName).append(" where 1=1 ");
		Field[] fArray = c.getDeclaredFields();
			for (Field field : fArray) {
				boolean fExists = field.isAnnotationPresent(Column.class);
				if(!fExists){
					continue;
				}
				Column column = field.getAnnotation(Column.class);
				String columnName = column.value();
				
				String filedName = field.getName();
				String getMethodName = "get"+ filedName.substring(0,1).toUpperCase()+
						filedName.substring(1);
				Object fieldValue = null;
				try {
					Method getMethod = c.getMethod(getMethodName);
					fieldValue = getMethod.invoke(f);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(fieldValue==null || (fieldValue instanceof Integer && (Integer)fieldValue==0)){
					continue;
				}
				sb.append(" and ").append(filedName);
				if(fieldValue instanceof String){
					if(((String)fieldValue).contains(",")){
						String[] values = ((String)fieldValue).split("'");
						sb.append(" in(");
						for (String v : values) {
							sb.append("'").append(v).append("'").append(",");
						}
						sb.deleteCharAt(sb.length()-1);
						sb.append(")");
					}else{
					sb.append(" = ").append("'").append(fieldValue).append("'");
					}
				}else if(fieldValue instanceof Integer){
					sb.append(" = ").append(fieldValue);
				}
			}
		
		return sb.toString();
	}

}
