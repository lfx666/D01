package com.dc.f01.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.dc.f01.data.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("gameDao")
public class GameDaoImpl extends BaseDaoImpl implements IGameDao{

	@Override
	public List<VitrualSet> queryVitrualSetList() throws Exception {
		String sql = "select gid,game_code,play,posi,content from FS_S20_VITRUAL_SET t where is_open = '1'";
		List<VitrualSet> list = getJdbcTemplate().query(sql, new RowMapper<VitrualSet>(){
			@Override
			public VitrualSet mapRow(ResultSet rs, int i) throws SQLException {
				VitrualSet vs = new VitrualSet();
				vs.setGid(rs.getLong("gid"));
				vs.setGameCode(rs.getString("game_code"));
				vs.setPlay(rs.getString("play"));
				vs.setPosi(rs.getString("posi"));
				vs.setContent(rs.getString("content"));
				return vs;
			}
		});
		if(list == null){
			list = new ArrayList<VitrualSet>();
		}
		return list;
	}

	@Override
	public List queryAIList() throws Exception {
		List<Object> paramList = new ArrayList<Object>();
		String sql = " select * from FS_S13_PLAYER t";

		Object obj[] = paramList.toArray();
		return this.queryDataByArray(sql.toString(),obj);
	}

	@Override
	public List<Game> queryGameList() throws Exception {
		String sql = "select game_code,game_name,locking_time from FS_S20_GAME t where t.status = '1'";
		List<Game> list = getJdbcTemplate().query(sql, new RowMapper<Game>(){
			@Override
			public Game mapRow(ResultSet rs, int i) throws SQLException {
				Game game = new Game();
				game.setGameCode(rs.getString("game_code"));
				game.setGameName(rs.getString("game_name"));
				game.setLockingTime(rs.getLong("locking_time"));
				return game;
			}
		});
		if(list == null){
			list = new ArrayList<Game>();
		}
		return list;
	}

	@Override
	public List<PlatformParam> queryPlatformParam() throws Exception {
		String sql = "select t.plat_name,t.plat_logo,t.admin_name,t.admin_img,t.cs_img,t.base_num,t.cs_qr_code,t.finance_qr_code,t.cs_name from fs_s20_platform t";
		List<PlatformParam> list = getJdbcTemplate().query(sql, new RowMapper<PlatformParam>(){
			@Override
			public PlatformParam mapRow(ResultSet rs, int i) throws SQLException {
				PlatformParam platformParam = new PlatformParam();
				platformParam.setPlatName(rs.getString("plat_name"));
				platformParam.setPlatLogo(rs.getString("plat_logo"));
				platformParam.setAdminName(rs.getString("admin_name"));
				platformParam.setAdminImg(rs.getString("admin_img"));
				platformParam.setCsImg(rs.getString("cs_img"));
				platformParam.setBaseNum(rs.getString("base_num"));
				platformParam.setCsQrCode(rs.getString("cs_qr_code"));
				platformParam.setFinanceQrCode(rs.getString("finance_qr_code"));
				platformParam.setCsName(rs.getString("cs_name"));
				return platformParam;
			}
		});
		if(list == null){
			list = new ArrayList<PlatformParam>();
		}
		return list;
	}

	@Override
	public List<BigAccount> queryBigACCParam() throws Exception {
		String sql = "select t.hf_accounts,t.hf_pwd,t.hf_addr from fs_s20_official t";
		List<BigAccount> list = getJdbcTemplate().query(sql, new RowMapper<BigAccount>(){
			@Override
			public BigAccount mapRow(ResultSet rs, int i) throws SQLException {
				BigAccount bigAccount = new BigAccount();
				bigAccount.setHfAccounts(rs.getString("hf_accounts"));
				bigAccount.setHfPwd(rs.getString("hf_pwd"));
				bigAccount.setHfAddr(rs.getString("hf_addr"));
				return bigAccount;
			}
		});
		if(list == null){
			list = new ArrayList<BigAccount>();
		}
		return list;
	}

	@Override
	public List<VGameParam> queryVgameParamList(String gameCode) throws Exception {
		String sql = "select t.start_time,t.end_time,t.min_interval,t.max_interval,t.robot_switch from fs_s20_vgame_param t where t.game_code='"+gameCode+"'";
		List<VGameParam> list = getJdbcTemplate().query(sql, new RowMapper<VGameParam>(){
			@Override
			public VGameParam mapRow(ResultSet rs, int i) throws SQLException {
				VGameParam vGameParam = new VGameParam();
				vGameParam.setRobotSwitch(rs.getString("robot_switch"));
				vGameParam.setStartTime(rs.getString("start_time"));
				vGameParam.setEndTime(rs.getString("end_time"));
				vGameParam.setMinInterval(rs.getString("min_interval"));
				vGameParam.setMaxInterval(rs.getString("max_interval"));
				return vGameParam;
			}
		});
		if(list == null){
			list = new ArrayList<VGameParam>();
		}
		return list;
	}

	@Override
	public List<AiRobot> queryGameAIList(String gameCode) throws Exception {
		String sql = "select gid,game_code,nick_name,img,min_bet,max_bet from FS_S20_ROBOT t where is_open = 'Y' and game_code='"+gameCode+"'";
		List<AiRobot> list = getJdbcTemplate().query(sql, new RowMapper<AiRobot>(){
			@Override
			public AiRobot mapRow(ResultSet rs, int i) throws SQLException {
				AiRobot ar = new AiRobot();
				ar.setGid(rs.getLong("gid"));
				ar.setNickName(rs.getString("nick_name"));
				ar.setGameCode(rs.getString("game_code"));
				ar.setImg(rs.getString("img"));
				ar.setMinBet(rs.getDouble("min_bet"));
				ar.setMaxBet(rs.getDouble("max_bet"));
				return ar;
			}
		});
		if(list == null){
			list = new ArrayList<AiRobot>();
		}
		return list;
	}


}
