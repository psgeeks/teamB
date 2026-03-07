class UserSolution {
	class LinkedList{
		Soldier head, tail;
		public LinkedList() {
			head = new Soldier();
			tail = new Soldier();

			head.next = tail;
			tail.prev = head;
		}

		void addSoldier(Soldier n) {
			n.prev = tail.prev;
			n.next = tail;

			tail.prev.next = n;
			tail.prev = n;
		}

		void addSoldiers(LinkedList list) {
			if (list.head.next == list.tail) return;

			Soldier first = list.head.next;
			Soldier last = list.tail.prev;

			first.prev = this.tail.prev;
			last.next = this.tail;
			this.tail.prev.next = first;
			this.tail.prev = last;

			list.head.next = list.tail;
			list.tail.prev = list.head;
		}
	}

	class Soldier {
		int ID, team;
		Soldier prev, next;

		public Soldier(){}
		public Soldier(int ID, int team) {
			this.ID = ID;
			this.team = team;
		}
	}

	LinkedList[][] soldiers;
	Soldier[] soldierInfo = new Soldier[100_001];

	public void init() {
		soldiers = new LinkedList[6][6];
		for (int i=1; i<=5; i++) {
			for (int j=1; j<=5; j++) {
				soldiers[i][j] = new LinkedList();
			}
		}
	}
	public void hire(int mID, int mTeam, int mScore) {
		Soldier hired = new Soldier(mID, mTeam);

		soldiers[mTeam][mScore].addSoldier(hired);
		soldierInfo[mID] = hired;
	}
	public void fire(int mID) {
		Soldier fired = soldierInfo[mID];
		soldierInfo[mID] = null;

		fired.prev.next = fired.next;
		fired.next.prev = fired.prev;

		fired.prev = null;
		fired.next = null;
	}
	public void updateSoldier(int mID, int mScore) {
		int team = soldierInfo[mID].team;

		fire (mID);
		hire (mID, team, mScore);
	}

	public void updateTeam(int mTeam, int mChangeScore) {
		if (mChangeScore==0) return;

		if (mChangeScore>0) {
			for (int score=4; score>=1; score--) {
				int nScore = (score + mChangeScore)>=5 ? 5 : (score + mChangeScore);
				soldiers[mTeam][nScore].addSoldiers(soldiers[mTeam][score]);
			}
		} else {
			for (int score=2; score<=5; score++) {
				int nScore = (score + mChangeScore)<=1 ? 1 : (score + mChangeScore);
				soldiers[mTeam][nScore].addSoldiers(soldiers[mTeam][score]);
			}
		}
	}

	public int bestSoldier(int mTeam) {
		boolean exist = false;
		int maxID = 0;
		for (int score=5; score>=1; score--) {
			if (exist) break;
			for (Soldier s = soldiers[mTeam][score].head.next; s!= soldiers[mTeam][score].tail; s = s.next) {
				if (s.ID > maxID) {
					maxID = s.ID;
					exist = true;
				}
			}
		}

		return maxID;
	}
}
