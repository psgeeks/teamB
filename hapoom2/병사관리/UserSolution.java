package day0308;
class UserSolution {
	static class Node{
		int id,ver;
		Node next;
		
		Node(int id, int ver){
			this.id = id;
			this.ver = ver;
		}
	}
	
	static final int MAX_ID = 100000;
	
	int team[]; 
	int version[];
	
	Node head[][],tail[][]; //[team][score]
	
	public void init() {
		team = new int[MAX_ID + 1];
		version = new int[MAX_ID + 1];
		
		head = new Node[6][6];
		tail = new Node[6][6];
		
		for(int i=1;i<=5;i++) {
			for(int j=1;j<=5;j++) {
				head[i][j] = new Node(0,0);
				tail[i][j] = head[i][j];
			}
		}
	}
	
	void add(int id, int t, int s) {
		Node node = new Node(id, version[id]);
		tail[t][s].next = node;
		tail[t][s] = node;
	}
	
	public void hire(int mID, int mTeam, int mScore) {
		team[mID] = mTeam;
		version[mID]++;
		add(mID,mTeam,mScore);
	}
	
	public void fire(int mID) {
		version[mID] = -1;
	}
	
	//고유번호가 mID인 병사의 평판 점수를 mScore로 변경(mID 항상 있음)
	public void updateSoldier(int mID, int mScore) {
		version[mID]++;
		add(mID, team[mID], mScore);
	}
	
	//소속팀이 mTeam인 병사들의 평판 점수를 모두 변경
	public void updateTeam(int mTeam, int mChangeScore) {
		Node nh[] = new Node[6];
		Node nt[] = new Node[6];
		
		for(int s = 1; s <= 5 ; s++) {
			nh[s]= new Node(0,0);
			nt[s] = nh[s];
		}
		
		for(int s=1;s<=5;s++) {
			int ns = s + mChangeScore;
			if(ns<1) ns=1;
			if(ns>5) ns=5;
			
			if(head[mTeam][s].next != null) {
				nt[ns].next = head[mTeam][s].next;
				nt[ns] = tail[mTeam][s];
			}
		}
		for(int s=1;s<=5;s++) {
			head[mTeam][s] = nh[s];
			tail[mTeam][s] = nt[s];
		}
	}
	
	//소속팀이 mTeam인 병사들 중 평판 점수가 가장 높은 병사의 고유번호를 반환
	public int bestSoldier(int mTeam) {
		for(int s=5;s>=1;s--) {
			int best = 0;
			Node cur = head[mTeam][s].next;
			while(cur!=null) {
				int id = cur.id;
				if(version[id]==cur.ver) {
					if(id>best) best = id;
				}
				cur = cur.next;
			}
			if(best != 0) return best;
		}
		return 0;
	}
}
