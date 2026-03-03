package 병사관리;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class UserSolution {
	static Map<Integer, Soldier> soldiers;
	static Map<Integer, List<Soldier>> teams;
	
	static class Soldier {
		int mID, mTeam, mScore;
		Soldier(int mID, int mTeam, int mScore) {
			this.mID = mID; this.mTeam = mTeam; this.mScore = mScore;
		}
	}
	
	public void init() {
		soldiers = new HashMap<>();
		teams = new HashMap<>();
	}
	
	public void hire(int mID, int mTeam, int mScore) {
		Soldier soldier = new Soldier(mID, mTeam, mScore); 
		soldiers.put(mID, soldier);
		
		List<Soldier> team = teams.get(mTeam);
		if(team != null) {
			team.add(soldier);
		} else {
			List<Soldier> list = new ArrayList<>();
			list.add(soldier);
			teams.put(mTeam, list);
		}
	}
	
	public void fire(int mID) {
		int mTeam = soldiers.remove(mID).mTeam;
		List<Soldier> team = teams.get(mTeam);
		for(int i = 0; i < team.size(); i++) {
			if(team.get(i).mID == mID) {
				team.remove(i);
				break;
			}
		}
	}
	
	public void updateSoldier(int mID, int mScore) {
		Soldier soldier = soldiers.get(mID);
		soldier.mScore = mScore;
	}

	public void updateTeam(int mTeam, int mChangeScore) {
		List<Soldier> team = teams.get(mTeam);
		for(Soldier soldier : team) {
			int nextScore = soldier.mScore + mChangeScore;
			soldier.mScore = Math.max(1, Math.min(5, nextScore));
		}
	}

	public int bestSoldier(int mTeam) {
		List<Soldier> team = teams.get(mTeam);
		int bestMID = Integer.MIN_VALUE;
		int bestMScore = Integer.MIN_VALUE;
		for(Soldier soldier : team) {
			if(soldier.mScore == bestMScore) {
				bestMID = Math.max(bestMID, soldier.mID);
			} else if(soldier.mScore > bestMScore) {
				bestMID = soldier.mID;
				bestMScore = soldier.mScore;
			}
		}
		
		return bestMID;
	}
}
