package com.dc.f01.dao;

import java.util.List;

import com.dc.f01.data.*;

public interface IGameDao {

	public List<VitrualSet> queryVitrualSetList() throws Exception;

	public List  queryAIList()throws Exception;

	public List<Game> queryGameList()throws Exception;

	public List<PlatformParam> queryPlatformParam()throws Exception;

	public List<BigAccount> queryBigACCParam()throws Exception;

	public List<VGameParam> queryVgameParamList(String gameCode)throws Exception;

	public List<AiRobot> queryGameAIList(String gameCode)throws Exception;
}
