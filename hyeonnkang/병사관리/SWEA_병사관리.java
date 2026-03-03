import java.util.*;

class 병사관리 {
	// soldiers[팀번호][점수] : 해당 병사 리스트 
	LinkedList[][] soldiers = new LinkedList[6][6];
	// idToSoldiers[mId] : soldiers에 저장된 병사 객체
	Soldier[] idToSoldiers = new Soldier[100005];
	
	class Soldier{
		int mID;
		int mTeam;
		int mScore;
		Soldier prev;
		Soldier next;
		
		Soldier(int mID, int mTeam, int mScore ){
			this.mID = mID;
			this.mTeam = mTeam;
			this.mScore = mScore;
		}
	}
	
	class LinkedList{
		Soldier head;
		Soldier tail;
		LinkedList(Soldier head, Soldier tail){
			this.head = head;
			this.tail = tail;
		}
	}
	
	public void init() {
		for(int t = 1; t <= 5; t++) {
			for(int score = 1; score <= 5; score++) {
				Soldier head = new Soldier(0, 0, 0);
				Soldier tail = new Soldier(0, 0, 0);
				head.next = tail;
				tail.prev = head;
				soldiers[t][score] = new LinkedList(head, tail);
			}
		}
	}
	
	public void hire(int mID, int mTeam, int mScore) {
		Soldier s = new Soldier(mID, mTeam, mScore);
		Soldier sp = soldiers[mTeam][mScore].tail.prev;
		sp.next = s;
		s.prev = sp;
		s.next = soldiers[mTeam][mScore].tail;
		soldiers[mTeam][mScore].tail.prev = s;
		
		// 아이디로 객체 바로 가리키도록 추가
		idToSoldiers[mID] = s;
	}
	
	public void fire(int mID) {
		Soldier target = idToSoldiers[mID]; // 삭제할 병사
		idToSoldiers[mID] = null;
		Soldier p = target.prev;
		Soldier n = target.next;
		p.next = n;
		n.prev = p;
	}
	public void updateSoldier(int mID, int mScore) {
		Soldier s = idToSoldiers[mID];
		// 기존에 있던거 제거
		fire(mID); 
		// 점수 변경하기
		s.mScore = mScore;		
		// 다시 리스트에 저장하기 
		hire(mID, s.mTeam, mScore);
	}

	public void updateTeam(int mTeam, int mChangeScore) {
	    if (mChangeScore == 0) return;

	    if (mChangeScore > 0) {
	        for (int score = 5; score >= 1; score--) {
	            int nextScore = score + mChangeScore;
	            if (nextScore > 5) nextScore = 5;
	            if (nextScore < 1) nextScore = 1;
	            if (score == nextScore) continue;

	            LinkedList src = soldiers[mTeam][score];
	            if (src.head.next == src.tail) continue; // empty

	            LinkedList dst = soldiers[mTeam][nextScore];

	            Soldier first = src.head.next;
	            Soldier last  = src.tail.prev;

	            // dst tail 앞에 붙이기
	            Soldier dstLast = dst.tail.prev;
	            dstLast.next = first;
	            first.prev = dstLast;

	            last.next = dst.tail;
	            dst.tail.prev = last;

	            // src 비우기
	            src.head.next = src.tail;
	            src.tail.prev = src.head;
	        }
	    } else { // mChangeScore < 0
	        for (int score = 1; score <= 5; score++) {
	            int nextScore = score + mChangeScore;
	            if (nextScore > 5) nextScore = 5;
	            if (nextScore < 1) nextScore = 1;
	            if (score == nextScore) continue;

	            LinkedList src = soldiers[mTeam][score];
	            if (src.head.next == src.tail) continue;

	            LinkedList dst = soldiers[mTeam][nextScore];

	            Soldier first = src.head.next;
	            Soldier last  = src.tail.prev;

	            Soldier dstLast = dst.tail.prev;
	            dstLast.next = first;
	            first.prev = dstLast;

	            last.next = dst.tail;
	            dst.tail.prev = last;

	            src.head.next = src.tail;
	            src.tail.prev = src.head;
	        }
	    }
	}

	public int bestSoldier(int mTeam) {
		int maxID = -1;
		for(int score = 5; score >= 1; score--) {
			Soldier s = soldiers[mTeam][score].head.next;
			while(s != soldiers[mTeam][score].tail) {
				maxID = Math.max(maxID, s.mID);
				s = s.next;
			}
			if(maxID != -1) return maxID;
		}
		
		return maxID;
	}
}
