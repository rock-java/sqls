package com.rockson.sqls;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_5;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;


public class AsmResultSetMapFactory implements ResultSetMapFactory {

//	static final Set<String> definedClasses = new HashSet<String>();
	ClassLoader classLoader = new ClassLoader() {
		public java.lang.Class<?> loadClass(String name) throws ClassNotFoundException {
			if (0 != name.indexOf(Type.getInternalName(ResultSetMap.class) + "_")) {
				return ClassLoader.getSystemClassLoader().loadClass(name);
//				return Thread.currentThread().getContextClassLoader().loadClass(name);
			} else {
//				if(definedClasses.contains(name)) {
//					return Class.forName(name);
//				}
				Class<?> beanClass = Class.forName(getBeanName(name));
				byte[] bs;
				bs = genClass(beanClass);
//				definedClasses.add(name);
				return defineClass(name.replace('/', '.'), bs, 0, bs.length);
			}

		};
	};

	@Override
	public <B> ResultSetMap<B> build(Class<B> clazz) {
		try {
//			return (ResultSetMap<B>) classLoader.loadClass(genClassName(clazz)).newInstance();
			byte[] b = genClass(clazz);
			ClassLoader loader = ClassLoader.getSystemClassLoader();
			Class<?> cls = Class.forName("java.lang.ClassLoader");
			java.lang.reflect.Method method = cls.getDeclaredMethod(
			                "defineClass", new Class[] { String.class, byte[].class,
			                        int.class, int.class });
			method.setAccessible(true);
			Object[] args = new Object[] { genClassName(clazz).replace('/', '.'), b, new Integer(0),
			                    new Integer(b.length) };
			return (ResultSetMap<B>) ((Class<?>)method.invoke(loader, args)).newInstance();
		} catch (Exception e) {
			throw new BuilderException(e);
		}
	}

	protected <B> byte[] genClass(Class<B> clazz) {
		// String beanInternalName = Type.getInternalName(clazz);
		// String beanDesc = "L" + beanInternalName + ";";
		// String className = genClassName(clazz);
		// String signature = "Ljava/lang/Object;Lcom/rockson/jsql/ResultSetMap<L" + Type.getInternalName(clazz) +
		// ";>;";
		ClassWriter cw = new ClassWriter(0);
		cw.visit(V1_5, ACC_PUBLIC, genClassName(clazz),
				"Ljava/lang/Object;Lcom/rockson/jsql/ResultSetMap<L" + Type.getInternalName(clazz) + ";>;",
				"java/lang/Object", new String[] { Type.getInternalName(ResultSetMap.class) });
		genConstructor(cw);
		genMapMethod(cw, clazz);
		genMapOneMethod(cw, clazz);
		return cw.toByteArray();
	}

	private void genConstructor(ClassWriter cw) {
		MethodVisitor mvInit = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mvInit.visitIntInsn(ALOAD, 0);
		mvInit.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mvInit.visitInsn(RETURN);
		mvInit.visitMaxs(1, 1);
		mvInit.visitEnd();
	}

	/**
	 * List<Bean> map(ResultSet resultSet , Class<Bean> clazz)throws SQLException;
	 * 
	 * @param cw
	 * @param clazz
	 */
	private <B> void genMapMethod(ClassWriter cw, Class<B> clazz) {
		String beanInternalName = Type.getInternalName(clazz);

		MethodVisitor mvMap = cw.visitMethod(ACC_PUBLIC, "map",
				"(Ljava/sql/ResultSet;Ljava/lang/Class;)Ljava/util/List;", "(Ljava/sql/ResultSet;Ljava/lang/Class<L"
						+ beanInternalName + ";>;)Ljava/util/List<L" + beanInternalName + ";>;",
				new String[] { Type.getInternalName(SQLException.class) });
		// mvMap.visitCode();
		// result = new LinkedList()
		mvMap.visitTypeInsn(NEW, "java/util/LinkedList");
		mvMap.visitInsn(DUP);
		mvMap.visitMethodInsn(INVOKESPECIAL, "java/util/LinkedList", "<init>", "()V", false);
		mvMap.visitVarInsn(ASTORE, 3); // linkedList [result]
		// Bean bean = null;
		mvMap.visitInsn(ACONST_NULL);
		mvMap.visitVarInsn(ASTORE, 4);
		// while
		Label startReadLabel = new Label();
		Label checkNextLabel = new Label();
		mvMap.visitJumpInsn(GOTO, checkNextLabel);
		mvMap.visitLabel(startReadLabel);
		// bean = new Bean();
		mvMap.visitTypeInsn(NEW, beanInternalName);
		mvMap.visitInsn(DUP);
		mvMap.visitMethodInsn(INVOKESPECIAL, beanInternalName, "<init>", "()V", false);
		mvMap.visitVarInsn(ASTORE, 4); // bean = new Bean();
		try {
			for (PropertyDescriptor p : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
				Method writeMethod = p.getWriteMethod();
				if (null == writeMethod) {
					continue;
				}
				DbField dbField = clazz.getDeclaredField(p.getName()).getAnnotation(DbField.class);
				if (null == dbField) {
					dbField = writeMethod.getAnnotation(DbField.class);
				}
				String[] ngs = fetchMethodName(p.getPropertyType(), dbField);
				if (null == ngs) {
					continue;
				}
				mvMap.visitVarInsn(ALOAD, 4); // get bean
				mvMap.visitVarInsn(ALOAD, 1); // get resultSet
				mvMap.visitLdcInsn((null != dbField && null != dbField.value() ? dbField.value() : p.getName()));
				// resultSet.getType("filed")
				mvMap.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", ngs[0], ngs[1], true);
				// bean.setxxx(value);
				mvMap.visitMethodInsn(INVOKEVIRTUAL, beanInternalName, writeMethod.getName(), ngs[2], false);
			}
			mvMap.visitVarInsn(ALOAD, 3); // linkedlist
			mvMap.visitVarInsn(ALOAD, 4); // bean
			// boolean linkedlist.add(bean)
			mvMap.visitMethodInsn(INVOKEVIRTUAL, "java/util/LinkedList", "add", "(Ljava/lang/Object;)Z", false);
			mvMap.visitInsn(POP);
		} catch (IntrospectionException | NoSuchFieldException | SecurityException e) {
			throw new BuilderException(e);
		}

		mvMap.visitLabel(checkNextLabel);
		// resultSet.next()
		mvMap.visitVarInsn(ALOAD, 1);
		mvMap.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "next", "()Z", true);
		mvMap.visitJumpInsn(IFNE, startReadLabel);

		mvMap.visitVarInsn(ALOAD, 1);
		mvMap.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "close", "()V", true);

		mvMap.visitVarInsn(ALOAD, 3);
		mvMap.visitInsn(ARETURN);
		mvMap.visitMaxs(3, 5);
		mvMap.visitEnd();
	}

	/**
	 * Bean mapOne(ResultSet resultSet , Class<Bean> clazz)throws SQLException;
	 * 
	 * @param cw
	 * @param clazz
	 */
	private <B> void genMapOneMethod(ClassWriter cw, Class<B> clazz) {
		String beanInternalName = Type.getInternalName(clazz);
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "mapOne",
				"(Ljava/sql/ResultSet;Ljava/lang/Class;)Ljava/lang/Object;", "(Ljava/sql/ResultSet;Ljava/lang/Class<L"
						+ beanInternalName + ";>;)L" + beanInternalName + ";",
				new String[] { Type.getInternalName(SQLException.class) });
		mv.visitCode();
		Label noBeanLabel = new Label();
		// bean=null
		mv.visitInsn(ACONST_NULL);
		mv.visitVarInsn(ASTORE, 3);

		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "next", "()Z", true);
		mv.visitJumpInsn(IFEQ, noBeanLabel);

		// bean=new Bean();
		mv.visitTypeInsn(NEW, beanInternalName);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, beanInternalName, "<init>", "()V", false);
		mv.visitVarInsn(ASTORE, 3);
		try {
			for (PropertyDescriptor p : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
				Method writeMethod = p.getWriteMethod();
				if (null == writeMethod) {
					continue;
				}
				DbField dbField = clazz.getDeclaredField(p.getName()).getAnnotation(DbField.class);
				if (null == dbField) {
					dbField = writeMethod.getAnnotation(DbField.class);
				}
				String[] ngs = fetchMethodName(p.getPropertyType(), dbField);
				if (null == ngs) {
					continue;
				}
				mv.visitVarInsn(ALOAD, 3);
				mv.visitVarInsn(ALOAD, 1); // get resultSet
				mv.visitLdcInsn((null != dbField && null != dbField.value() ? dbField.value() : p.getName()));
				// resultSet.getType("filed")
				mv.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", ngs[0], ngs[1], true);
				// bean.setxxx(value);
				mv.visitMethodInsn(INVOKEVIRTUAL, beanInternalName, writeMethod.getName(), ngs[2], false);
			}
		} catch (IntrospectionException | NoSuchFieldException | SecurityException e) {
			throw new BuilderException(e);
		}

		mv.visitLabel(noBeanLabel);

		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "close", "()V", true);

		mv.visitVarInsn(ALOAD, 3);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(4, 4);
		mv.visitEnd();

	}

	public static String getBeanName(String fname) {
		return fname.substring(fname.indexOf('_') + 1).replace("_", ".");
	}

	public static String genClassName(Class<?> clazz) {
		return Type.getInternalName(ResultSetMap.class) + "_" + clazz.getName().replace('.', '_');
	}

	// public String genClassName(String className){
	// return
	// Type.getInternalName(ResultSetMap.class)+"_"+className.replace('.', '_');
	// }

	/**
	 * 
	 * @param clazz
	 * @param dbField
	 * @return [getMethodName , getMethodDesc,beanSetMethodDesc]
	 */
	public String[] fetchMethodName(Class<?> clazz, DbField dbField) {
		try {
			if (null != dbField) {
				if (dbField.ignore()) {
					return null;
				}
				if (DbExtraTypes.NCHAR == dbField.type() || DbExtraTypes.NVARCHAR == dbField.type()
						|| DbExtraTypes.LONGNVARCHAR == dbField.type()) {
					return new String[] { "getNString",
							Type.getMethodDescriptor(ResultSet.class.getMethod("getNString", String.class)),
							"(Ljava/lang/String;)V" };
				}
			}
			if (boolean.class.equals(clazz) || Boolean.class.equals(clazz)) {
				return new String[] { "getBoolean",
						Type.getMethodDescriptor(ResultSet.class.getMethod("getBoolean", String.class)), "(Z)V" };
			}
			if (byte.class.equals(clazz) || Byte.class.equals(clazz)) {
				return new String[] { "getByte",
						Type.getMethodDescriptor(ResultSet.class.getMethod("getByte", String.class)), "(B)V" };
			}
			if (short.class.equals(clazz) || Short.class.equals(clazz)) {
				return new String[] { "getShort",
						Type.getMethodDescriptor(ResultSet.class.getMethod("getShort", String.class)), "(S)V" };
			}
			if (int.class.equals(clazz) || Integer.class.equals(clazz)) {
				return new String[] { "getInt",
						Type.getMethodDescriptor(ResultSet.class.getMethod("getInt", String.class)), "(I)V" };
			}
			if (long.class.equals(clazz) || Long.class.equals(clazz)) {
				return new String[] { "getLong",
						Type.getMethodDescriptor(ResultSet.class.getMethod("getLong", String.class)), "(J)V" };
			}
			if (float.class.equals(clazz) || Float.class.equals(clazz)) {
				return new String[] { "getFloat",
						Type.getMethodDescriptor(ResultSet.class.getMethod("getFloat", String.class)), "(F)V" };
			}
			if (double.class.equals(clazz) || Double.class.equals(clazz)) {
				return new String[] { "getDouble",
						Type.getMethodDescriptor(ResultSet.class.getMethod("getDouble", String.class)), "(D)V" };
			}
			if (String.class.equals(clazz)) {
				return new String[] { "getString",
						Type.getMethodDescriptor(ResultSet.class.getMethod("getString", String.class)),
						"(Ljava/lang/String;)V" };
			}

			if (Date.class.equals(clazz)) {
				return new String[] { "getDate",
						Type.getMethodDescriptor(ResultSet.class.getMethod("getDate", String.class)),
						"(Ljava/sql/Date;)V" };
			}
			if (Time.class.equals(clazz)) {
				return new String[] { "getTime",
						Type.getMethodDescriptor(ResultSet.class.getMethod("getTime", String.class)),
						"(Ljava/sql/Time;)V" };
			}
			if (Timestamp.class.equals(clazz)) {
				return new String[] { "getTimestamp",
						Type.getMethodDescriptor(ResultSet.class.getMethod("getTimestamp", String.class)),
						"(Ljava/sql/Timestamp;)V" };
			}
			if (java.util.Date.class.equals(clazz)) {
				return new String[] { "getDate",
						Type.getMethodDescriptor(ResultSet.class.getMethod("getDate", String.class)),
						"(Ljava/util/Date;)V" };
			}
			if (BigDecimal.class.equals(clazz)) {
				return new String[] { "getBigDecimal",
						Type.getMethodDescriptor(ResultSet.class.getMethod("getBigDecimal", String.class)),
						"(Ljava/math/BigDecimal;)V" };
			}
			if (byte[].class.equals(clazz)) {
				return new String[] { "getBytes",
						Type.getMethodDescriptor(ResultSet.class.getMethod("getBytes", String.class)), "([B)V" };
			}
			if (URL.class.equals(clazz)) {
				return new String[] { "getURL",
						Type.getMethodDescriptor(ResultSet.class.getMethod("getURL", String.class)), "(Ljava/net/URL)V" };
			}
			if (Blob.class.equals(clazz)) {
				return new String[] { "getBlob",
						Type.getMethodDescriptor(ResultSet.class.getMethod("getBlob", String.class)),
						"(Ljava/sql/Blob)V" };
			}
			if (Clob.class.equals(clazz)) {
				return new String[] { "getClob",
						Type.getMethodDescriptor(ResultSet.class.getMethod("getClob", String.class)),
						"(Ljava/sql/Clob)V" };
			}
			if (RowId.class.equals(clazz)) {
				return new String[] { "getRowId",
						Type.getMethodDescriptor(ResultSet.class.getMethod("getRowId", String.class)),
						"(Ljava/sql/RowId)V" };
			}
			if (Ref.class.equals(clazz)) {
				return new String[] { "getRef",
						Type.getMethodDescriptor(ResultSet.class.getMethod("getRef", String.class)), "(Ljava/sql/Ref)V" };
			}
			if (Array.class.equals(clazz)) {
				return new String[] { "getArray",
						Type.getMethodDescriptor(ResultSet.class.getMethod("getArray", String.class)),
						"(Ljava/sql/Array)V" };
			}
			if (Object.class.equals(clazz)) {
				return new String[] { "getObject",
						Type.getMethodDescriptor(ResultSet.class.getMethod("getObject", String.class)),
						"(Ljava/lang/getObject)V" };
			}

			return null;
		} catch (NoSuchMethodException | SecurityException e) {
			throw new BuilderException(e);
		}
	}
}
