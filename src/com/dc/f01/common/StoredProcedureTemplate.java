package com.dc.f01.common;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import oracle.jdbc.OracleTypes;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.core.SqlReturnType;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.support.AbstractSqlTypeValue;
import org.springframework.jdbc.object.StoredProcedure;

import com.alibaba.druid.support.spring.DruidNativeJdbcExtractor;

/**
 * Name:StoredProcedureTemplate Description:
 */
public class StoredProcedureTemplate extends StoredProcedure {

	private HashMap<String, Object> map = new HashMap<String, Object>();

	public StoredProcedureTemplate() {
		super();

	}

	public void setSQL(String sql){
		super.setSql(sql);
	}

	public HashMap getMap() {
		return this.map;
	}

	public void setValue(String key, Object obj) {
		map.put(key, obj);
	}

	public Map execute() {
		if (this.getSql() == null || this.getSql().equals(""))
			return null;
		this.compile();
		return execute(map);
	}

	public void setVarcharParam(String param,String value) {
		this.declareParameter(new SqlParameter(param, OracleTypes.VARCHAR));
		map.put(param, value);
	}
	public void setNumberParam(String param,Number value) {
		this.declareParameter(new SqlParameter(param, OracleTypes.NUMBER));
		map.put(param, value);
	}
	public void setDoubleParam(String param,Double value) {
		this.declareParameter(new SqlParameter(param, OracleTypes.DOUBLE));
		map.put(param, value);
	}

	public void setIntegerParam(String param,int value) {
		this.declareParameter(new SqlParameter(param, Types.INTEGER));
		map.put(param, value);
	}

	public void setLongParam(String param,Long value) {
		this.declareParameter(new SqlParameter(param, Types.BIGINT));
		map.put(param, value);
	}

	public void setVarcharOutParam(String param) {
		/*this.declareParameter(new SqlOutParameter(param, Types.VARCHAR));
		map.put(param, value);*/
		super.declareParameter(new SqlOutParameter(param, OracleTypes.VARCHAR));

	}

	public void setDoubleOutParam(String param,String value) {
		this.declareParameter(new SqlOutParameter(param, Types.DOUBLE));
		map.put(param, value);
	}

	public void setIntegerOutParam(String param,int value) {
		this.declareParameter(new SqlOutParameter(param, Types.INTEGER));
		map.put(param, value);
	}

	public void setNumberOutParam(String param,Number value) {
		this.declareParameter(new SqlOutParameter(param, OracleTypes.NUMBER));
		map.put(param, value);
	}

	public void setInParam(String param, int valueType) {
		this.declareParameter(new SqlParameter(param, valueType));
	}

	public void setOutParam(String param, int valueType) {
		this.declareParameter(new SqlOutParameter(param, valueType));

	}

	public void setReturnParam(String param, RowMapper<?> rowMapper) {
		this.declareParameter(new SqlReturnResultSet(param, rowMapper));
	}

	public void setTypeOutParam(String name,String typeName,SqlReturnType type) {
		this.declareParameter(new SqlOutParameter(name, OracleTypes.STRUCT,
				typeName, type));
	}

	public void setTypeInParam(String param,String typeName,final Object[] objs) throws SQLException {

		SqlTypeValue value = new AbstractSqlTypeValue() {

			@Override
			protected Object createTypeValue(Connection conn, int sqlType, String typeName)
					throws SQLException {
				Connection nativeCon = new DruidNativeJdbcExtractor().getNativeConnection(conn);
				StructDescriptor itemDescriptor = new StructDescriptor(typeName, nativeCon);
				Struct item = new STRUCT(itemDescriptor, nativeCon, objs);
				return item;
			}
		};
		this.declareParameter(new SqlParameter(param, OracleTypes.STRUCT,
				typeName));
		map.put(param, value);
	}
	///////////////////////
	public void setTypeInParamm(String param,String typeName,SqlTypeValue typeValue) throws SQLException {
		this.declareParameter(new SqlParameter(param, OracleTypes.STRUCT,
				typeName));
		map.put(param, typeValue);
	}
}
