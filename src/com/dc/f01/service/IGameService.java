package com.dc.f01.service;

import java.util.List;
import java.util.Map;

import com.dc.f01.data.*;

public interface IGameService {


	/**
	 * 虚拟投注规则
	 * @return
	 * @throws Exception
	 */
	public List<VitrualSet> queryVitrualSetList() throws Exception;

	/**
	 * 机器人列表
	 * @return
	 * @throws Exception
	 */
	public List  queryAIList() throws Exception;

	/**
	 * 获取游戏列表
	 * @return
	 * @throws Exception
	 */
	public List<Game> queryGameList()throws Exception;
	/**
	 * 获取平台参数
	 * @return
	 * @throws Exception
	 */
	public List<PlatformParam> queryPlatformParam()throws Exception;
	/**
	 * 获取平台引用的账号
	 * @return
	 * @throws Exception
	 */
	public List<BigAccount> queryBigACCParam()throws Exception;

	List<VGameParam> queryVgameParamList(String gameCode)throws Exception;

	public List<AiRobot> queryGameAIList(String gameCode)throws Exception;
}
