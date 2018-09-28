package com.dc.f01.service;

import java.util.List;

import com.dc.f01.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dc.f01.dao.IGameDao;

@Service("gameService")
public class GameServiceImpl implements IGameService{
	@Autowired
	private IGameDao gameDao;
	
	@Override
	public List<VitrualSet> queryVitrualSetList() throws Exception {
		return gameDao.queryVitrualSetList();
	}

	@Override
	public List  queryAIList() throws Exception {
		return gameDao.queryAIList();
	}

	@Override
	public List<Game> queryGameList() throws Exception {
		return gameDao.queryGameList();
	}

	@Override
	public List<PlatformParam> queryPlatformParam() throws Exception {
		return gameDao.queryPlatformParam();
	}

	@Override
	public List<BigAccount> queryBigACCParam() throws Exception {
		return gameDao.queryBigACCParam();
	}

	@Override
	public List<VGameParam> queryVgameParamList(String gameCode) throws Exception {
		return gameDao.queryVgameParamList( gameCode);
	}

	@Override
	public List<AiRobot> queryGameAIList(String gameCode) throws Exception {
		return gameDao.queryGameAIList( gameCode);
	}

}
