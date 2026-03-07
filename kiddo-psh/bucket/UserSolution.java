class UserSolution {
	LinkedList[][] soldiers;
	Node[] soldierInfo;

	class LinkedList {
		Node head, tail;
		LinkedList() {
			head = new Node();
			tail = new Node();

			head.next = tail;
			tail.prev = head;
		}

		void add(Node n) {
			n.prev = tail.prev;
			n.next = tail;
			tail.prev.next = n;
			tail.prev = n;
		}

		void addList(LinkedList list) {
			if (list.isEmpty()) return;

			Node first = list.head.next;
			Node last = list.tail.prev;

			first.prev = this.tail.prev;
			last.next = this.tail;

			this.tail.prev.next = first;
			this.tail.prev = last;

			list.head.next = list.tail;
			list.tail.prev = list.head;
		}

		boolean isEmpty(){
			return this.head.next == tail;
		}
	}

	class Node {
		int ID, team;
		Node prev, next;

		Node() {}
		Node(int ID, int team) {
			this.ID = ID;
			this.team = team;
		}

		void delete() {
			this.prev.next = this.next;
			this.next.prev = this.prev;

			this.prev = null;
			this.next = null;
		}
	}

	public void init() {
		soldierInfo = new Node[100_001];
		soldiers = new LinkedList[6][6];
		for (int i=1; i<=5; i++) {
			for (int j=1; j<=5; j++) {
				soldiers[i][j] = new LinkedList();
			}
		}
	}
	public void hire(int mID, int mTeam, int mScore) {
		Node hired = new Node(mID, mTeam);
		soldiers[mTeam][mScore].add(hired);
		soldierInfo[mID] = hired;
	}
	public void fire(int mID) {
		Node fired = soldierInfo[mID];
		soldierInfo[mID] = null;
		fired.delete();
	}
	public void updateSoldier(int mID, int mScore) {
		Node updated = soldierInfo[mID];
		fire(mID);
		hire(mID, updated.team, mScore);
	}

	public void updateTeam(int mTeam, int mChangeScore) {
		if (mChangeScore==0) return;

		if (mChangeScore>0) {
			for (int score=4; score>=1; score--) {
				int nScore = Math.min(5, score+mChangeScore);
				soldiers[mTeam][nScore].addList(soldiers[mTeam][score]);
			}
		} else {
			for (int score=2; score<=5; score++) {
				int nScore = Math.max(1, score+mChangeScore);
				soldiers[mTeam][nScore].addList(soldiers[mTeam][score]);
			}
		}
	}

	public int bestSoldier(int mTeam) {
		int maxID = 0;
		for (int score=5; score>=1; score--) {
			Node cur = soldiers[mTeam][score].head.next;
			Node end = soldiers[mTeam][score].tail;
			for (;cur!=end; cur=cur.next) {
				if (cur.ID > maxID) {
					maxID = cur.ID;
				}
			}
			if (maxID != 0) break;
		}
		return maxID;
	}
}
