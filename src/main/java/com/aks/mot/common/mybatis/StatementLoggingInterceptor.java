package com.aks.mot.common.mybatis;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.CallableStatementHandler;
import org.apache.ibatis.executor.statement.PreparedStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aks.mot.common.util.ExceptionUtil;

@Intercepts({@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}), @Signature(type = StatementHandler.class, method = "update", args = {Statement.class})})
public class StatementLoggingInterceptor implements Interceptor {

	private static Logger logger = LoggerFactory.getLogger(StatementLoggingInterceptor.class);

	private Field proxyMappedStatement;

	private Field proxyDelegate;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

	public StatementLoggingInterceptor() {
		try {
			proxyMappedStatement = BaseStatementHandler.class.getDeclaredField("mappedStatement");
			proxyMappedStatement.setAccessible(true);
			proxyDelegate = RoutingStatementHandler.class.getDeclaredField("delegate");
			proxyDelegate.setAccessible(true);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 *
	**/
	@Override
	public Object intercept(Invocation invocation) throws Throwable {

		Object result = null;
		int resultCount = 0;
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
		MappedStatement mappedStatement = null;

		if (proxyDelegate.get(statementHandler) instanceof PreparedStatementHandler) {
			PreparedStatementHandler statement = (PreparedStatementHandler) proxyDelegate.get(statementHandler);
			mappedStatement = (MappedStatement) proxyMappedStatement.get(statement);
		} else if (proxyDelegate.get(statementHandler) instanceof CallableStatementHandler) {
			CallableStatementHandler statement = (CallableStatementHandler) proxyDelegate.get(statementHandler);
			mappedStatement = (MappedStatement) proxyMappedStatement.get(statement);
		}

		String sqlmapId = mappedStatement.getId();
		BoundSql boundSql = statementHandler.getBoundSql();
		String parameterMappedSql = boundSql.getSql();
		Object parameterObject = statementHandler.getParameterHandler().getParameterObject();
		Configuration configuration = mappedStatement.getConfiguration();

		String strValue = "";
		try {
			if (parameterObject == null) {
				// case1) 파라미터가 없는 경우
				parameterMappedSql = parameterMappedSql.replaceAll("\\?", "''");
			} else if (parameterObject instanceof Map) {
				// case2) 파라미터의 객체 타입이 Map인 경우
				List<Object> entryParamsValueList = getParameters(configuration, boundSql, parameterObject);
				List<ParameterMapping> entryParamKeyList = boundSql.getParameterMappings();

				if (entryParamsValueList.size() != entryParamKeyList.size()) {
					for (int i = 0; i < entryParamKeyList.size(); i++) {
						String key = entryParamKeyList.get(i).getProperty();
						Map<String, Object> map = (Map) parameterObject;
						Object value = map.get(key);

						if (value == null) {
							parameterMappedSql = Pattern.compile("\\?").matcher(parameterMappedSql).replaceFirst("null /* " + entryParamKeyList.get(i).getProperty() + " */");
						} else if (value instanceof String) {
							strValue = value.toString();
							strValue = String.format("'%s'", strValue);
							parameterMappedSql = Pattern.compile("\\?").matcher(parameterMappedSql).replaceFirst(strValue);
						} else {
							strValue = value.toString();
							parameterMappedSql = parameterMappedSql.replaceFirst("\\?", strValue);
						}
					}
				} else {
					for (int i = 0; i < entryParamKeyList.size(); i++) {
						Object value = entryParamsValueList.get(i);
						if (value == null) {
							parameterMappedSql = Pattern.compile("\\?").matcher(parameterMappedSql).replaceFirst("null /* " + entryParamKeyList.get(i).getProperty() + " */");
						} else if (value instanceof String) {
							strValue = value.toString();
							strValue = String.format("'%s'", strValue);
							parameterMappedSql = Pattern.compile("\\?").matcher(parameterMappedSql).replaceFirst(strValue);
						} else {
							strValue = value.toString();
							parameterMappedSql = parameterMappedSql.replaceFirst("\\?", strValue);
						}
					}
				}
			} else if (parameterObject instanceof String) {
				// case3) 파라미터의 객체 타입이 String인 경우
				// List<ParameterMapping> paramMapping = boundSql.getParameterMappings();
				strValue = String.format("'%s'", parameterObject);
				parameterMappedSql = Pattern.compile("\\?").matcher(parameterMappedSql).replaceFirst(strValue);
			} else {
				// case4) 파라미터 타입이 사용자 정의 클래스인 경우
				// List<Object> entryParamsValueList = getParameters(configuration, boundSql, parameterObject);
				List<ParameterMapping> entryParamKeyList = boundSql.getParameterMappings();
				Class<? extends Object> parameterClass = parameterObject.getClass();
				for (ParameterMapping mapping : entryParamKeyList) {
					String propVal = mapping.getProperty();
					Field field = null;
					try {
						field = parameterClass.getDeclaredField(propVal);
					} catch (NoSuchFieldException e) {
						// field = parameterClass.getField(propVal);
						parameterMappedSql = Pattern.compile("\\?").matcher(parameterMappedSql).replaceFirst("null /* inaccessable field(inherited java field) [" + propVal + "] */");
						continue;
					}
					field.setAccessible(true);
					Class<?> javaType = mapping.getJavaType();
					if (String.class == javaType || field.get(parameterObject) instanceof java.lang.String || field.get(parameterObject) instanceof java.util.Date) {
						if (field.get(parameterObject) instanceof java.util.Date) {
							strValue = String.format("'%s'", dateFormat.format(field.get(parameterObject)));
						} else {
							strValue = String.format("'%s'", field.get(parameterObject));
						}
						parameterMappedSql = Pattern.compile("\\?").matcher(parameterMappedSql).replaceFirst(strValue);
					} else {
						strValue = String.format("%s", field.get(parameterObject));
						parameterMappedSql = Pattern.compile("\\?").matcher(parameterMappedSql).replaceFirst(strValue);
					}
				}
			}
		} catch (Exception e) {
			// logger.error("[{}][{}][{}][LastMappingParam:{}],Exception:{}\n,ExceptionTrace:{}\n, SQL{}\n", sqlmapId, strValue, e.getMessage(), ExceptionUtil.getStackTrace(e), parameterMappedSql);
			logger.error("■■■■■■■■■■■■■■■■■■■■ SQL Exception (S) ■■■■■■■■■■■■■■■■■■■■");
			logger.error("[ErrMessage] " + e.getMessage());
			//logger.error("[StackTrace] " + ExceptionUtil.getStackTrace(e));
			logger.error("[SqlMapId] " + sqlmapId);
			logger.error("[Parameter] " + strValue);
			logger.error("[BindSQL] " + parameterMappedSql);
			logger.error("■■■■■■■■■■■■■■■■■■■■ SQL Exception (E) ■■■■■■■■■■■■■■■■■■■■");
		}

		parameterMappedSql = parameterMappedSql.replaceAll("\n\t\t", "\n");

		try {
			logger.info("\n\n" + "[SQL-ID] " + sqlmapId);
			logger.info(parameterMappedSql);

			long start = System.currentTimeMillis();
			result = invocation.proceed();
			long end = System.currentTimeMillis();

			if (result instanceof List) {
				resultCount = ((List) result).size();
				logger.info("[Affected Rows] " + resultCount + " Rows");
			} else if (result instanceof Integer) {
				resultCount = (Integer) result;
				logger.info("[Affected Rows] " + resultCount + " Rows");
			} else if (result instanceof java.lang.Object) {
				logger.info("[Affected Rows] 1 Rows");
			}
			logger.info("[Elapsed Time] " + (end - start) / 1000.0 + " Sec");

		} catch (Exception e) {
			//logger.error(ExceptionUtil.getMessage(e));
			throw e;
		}

		return result;
	}

	public List<Object> getParameters(Configuration configuration, BoundSql boundSql, Object mehtodParam) {
		List<Object> paramList = new ArrayList<Object>();
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		if (parameterMappings != null) {
			for (int i = 0; i < parameterMappings.size(); i++) {
				ParameterMapping parameterMapping = parameterMappings.get(i);
				if (parameterMapping.getMode() != ParameterMode.OUT) {
					Object value = null;
					String propertyName = parameterMapping.getProperty();
					if (boundSql.hasAdditionalParameter(propertyName)) {
						value = boundSql.getAdditionalParameter(propertyName);
					} else {
						MetaObject metaObject = configuration.newMetaObject(mehtodParam);
						value = metaObject.getValue(propertyName);
					}
					paramList.add(value);
				}
			}
		}
		return paramList;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {

	}
}