package com.dc.f01.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class BaseDaoImpl extends JdbcDaoSupport {
	@Autowired
	private DataSource dataSource;

	@PostConstruct
	public void injectDataSource() {
		super.setDataSource(dataSource);
	}

	// 通过array数组检索
	private List<Map<String, Object>> retrieveByArray(String sql,Object[] params, int startIndex, int endIndex) {
		StringBuilder sb = new StringBuilder();
		if (startIndex >= 0 && endIndex >= 0) {
			sb.append("SELECT * FROM(SELECT A.*, ROWNUM RN_ FROM (").append(sql).append(")");

			sb.append(" A WHERE ROWNUM <=").append(endIndex).append(" ) WHERE RN_ >=").append(startIndex);
		} else {
			sb.append(sql);
		}
		return this.getJdbcTemplate().queryForList(sb.toString(), params);
	}

	// 通过list数组检索
	private List<Map<String, Object>> retrieveByList(String sql, List params,
			int startIndex, int endIndex) {
		return this
				.retrieveByArray(sql,
						(params != null && params.size() > 0 ? params.toArray()
								: null), startIndex, endIndex);
	}

	// 通过Array数组检索数目
	private int retrieveCountByArray(String sql, Object[] params) {
		return this.getJdbcTemplate().queryForObject(
				"SELECT COUNT(1) FROM ( " + sql + " )", params, Integer.class);
	}

	// 通过list数组检索数目
	private int retrieveCountByList(String sql, List params) {
		return this.retrieveCountByArray(sql, (params != null
				&& params.size() > 0 ? params.toArray() : null));
	}

	/**
	 * 查询
	 * @param startIndex 不分页传-1
	 * @param endIndex 不分页传-1
	 * @param isCount 是否查询总数量
	 */
	protected Object queryDataByLt(String sql, List params, int startIndex,
			int endIndex, boolean isCount) {
		if (isCount) {
			return retrieveCountByList(sql, params);
		} else {
			return retrieveByList(sql, params, startIndex, endIndex);
		}
	}

	/**
	 * 查询
	 */
	protected Object queryDataByArray(String sql, Object[] params, int startIndex, int endIndex, boolean isCount) {
		if (isCount) {
			return retrieveCountByArray(sql, params);
		} else {
			return retrieveByArray(sql, params, startIndex, endIndex);
		}
	}

	protected Object queryDataByObjects(String sql, int startIndex, int endIndex, boolean isCount, Object... params) {
		if (isCount) {
			return retrieveCountByArray(sql, params);
		} else {
			return retrieveByArray(sql, params, startIndex, endIndex);
		}
	}

	protected List<Map<String, Object>> queryDataByLt(String sql, List params,
			int startIndex, int endIndex) {
		return retrieveByList(sql, params, startIndex, endIndex);
	}

	protected List<Map<String, Object>> queryDataByArray(String sql, Object[] params, int startIndex, int endIndex) {
		return retrieveByArray(sql, params, startIndex, endIndex);
	}

	protected List<Map<String, Object>> queryDataByLt(String sql, List params) {
		return retrieveByList(sql, params, -1, -1);
	}

	protected List<Map<String, Object>> queryDataByArray(String sql,
			Object[] params) {
		return retrieveByArray(sql, params, -1, -1);
	}

	protected List<Map<String, Object>> queryDataByObjects(String sql,
			Object... params) {
		return retrieveByArray(sql, params, -1, -1);
	}

	protected List<Map<String, Object>> queryDataByObjects(String sql,
			int startIndex, int endIndex, Object... params) {
		return retrieveByArray(sql, params, startIndex, endIndex);
	}

	protected int queryDataCountByObjects(String sql, Object... params) {
		return retrieveCountByArray(sql, params);
	}

	protected int queryDataCountByLt(String sql, List params) {
		return retrieveCountByList(sql, params);
	}

	protected int queryDataCountByArray(String sql, Object[] params) {
		return retrieveCountByArray(sql, params);
	}

	public Integer executeSQLUpdate(String sql, Object[] args) throws Exception {
		try {
			return getJdbcTemplate().update(sql, args);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<Map<String, Object>> querySQLPageList(String sql, Object[] args, Integer page,
			Integer rows) throws Exception {
		try {
			if (page == null || page < 1) {
				page = 1;
			}
			if (rows == null || rows < 1) {
				rows = 10;
			}
			int startIndex = (page - 1) * rows;
			int endIdex = startIndex + rows;
			return retrieveByArray(sql,args,startIndex,endIdex);
		} catch (Exception e) {
			throw  e;
		}
	}

}
