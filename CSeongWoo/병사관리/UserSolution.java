package sweaPro.solution_병사관리;

class UserSolution {
	// 군인 mID 저장 배열
	static Node soldierArr[] = new Node[100001];

	static Node nodes[] = new Node[100001]; // 군인을 가져올 배열 풀
	// mTeam은 1부터 5까지.. 거기서 평판 점수도 5까지니까 * 5..
	// 한개의 team 안에 5개의 연결 리스트 필요.
	// soldierArr[i]의 next, prev는 이 mTeam과 연결됨.
	static Node mTeams[][][] = new Node[5][5][2]; // 5*5 크기로 [num][score]에는 각각 head, tail 있음.
	int nodeCnt;

	// 프로그램 시작 시 최초 1회 실행
	public UserSolution() {
		for (int i = 0; i < 100001; i++)
			nodes[i] = new Node();
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				// 초기화
				// 얘는 절대로 값이 덮어씌워지면 안됨. 얘 next부터가 새로 들어올 노드
				mTeams[i][j][0] = new Node(); // head
				mTeams[i][j][1] = new Node(); // tail
			}
		}
	}

	public void init() {
		nodeCnt = 0;

	}

	// 고유번호 mID 병사 영입.
	public void hire(int mID, int mTeam, int mScore) {

		// nodes 풀에서 객체 가져오기
		Node newSoldier = nodes[nodeCnt++];
		mTeam--;
		mScore--;
		newSoldier.mID = mID;
		newSoldier.mTeam = mTeam;
		
		//  Head와 Tail 노드 가져오기
		Node head = mTeams[mTeam][mScore][0];
		Node tail = mTeams[mTeam][mScore][1];

		if (head.next == null) {
			head.next = tail; // 내 다음은 tail
			tail.prev = head; // 내 앞은 prev
		}
		
		// Tail 바로 앞에 새로운 병사를 삽입
		Node prevNode = tail.prev; 

		newSoldier.next = tail; // 새 병사의 뒤는 Tail
		newSoldier.prev = prevNode; // 새 병사의 앞은 기존의 마지막 병사

		prevNode.next = newSoldier; // 기존 마지막 병사의 뒤를 새 병사로
		tail.prev = newSoldier; // Tail의 앞을 새 병사로
		soldierArr[mID] = newSoldier;
	}

	// 고유번호 mID 병사 해고
	public void fire(int mID) {
		if (soldierArr[mID].next != null) { // 다음 노드의 이전은 내가 아니라 내 이전 노드
			soldierArr[mID].next.prev = soldierArr[mID].prev;
		}
		if (soldierArr[mID].prev != null) {
			soldierArr[mID].prev.next = soldierArr[mID].next;
		}
		soldierArr[mID] = null;
	}

	// 해당 팀 team[i][score]의 병사를 team[i][mScore]로 편입
	public void updateSoldier(int mID, int mScore) {
		Node currNode = soldierArr[mID];
		if (currNode.next != null) { // 내 노드 뒤의 앞은 내 노드의 앞
			currNode.next.prev = soldierArr[mID].prev;
		}
		if (currNode.prev != null) { // 내 노드의 앞의 뒤는 내 노드의 뒤
			currNode.prev.next = soldierArr[mID].next;
		}
		Node tail =  mTeams[soldierArr[mID].mTeam][mScore][1].next;
		Node prevTail = tail.prev;
		currNode.next = tail;
		currNode.prev = prevTail;
		prevTail.next = currNode;
	}

	public void updateTeam(int mTeam, int mChangeScore) {
		if (mChangeScore < 0) {
			for (int i = 0; i < 5; i++) {
				// 갱신된 점수가 0보다 작으면 0
				int changeScore = i + mChangeScore;
				if (changeScore <= 0)
					changeScore = 0;
				Node currHead = mTeams[mTeam][i][0].next;
				Node goalTail = mTeams[mTeam][changeScore][1];
				currHead.prev = goalTail;
				goalTail.next = currHead;
			}
		} else {
			for (int i = 4; i >= 0; i--) {
				// 갱신된 점수가 4보다 크면 4
				int changeScore = i + mChangeScore;
				if (changeScore >= 4)
					changeScore = 4;
				Node currHead = mTeams[mTeam][i][0].next;
				Node goalTail = mTeams[mTeam][changeScore][1];
				currHead.prev = goalTail;
				goalTail.next = currHead;
			}
		}
	}

	public int bestSoldier(int mTeam) {
		int maxNum = 0;
		for(int i = 4; i >= 0; i--) {
			Node head = mTeams[mTeam][i][0];
			Node tail = mTeams[mTeam][i][1];
			Node p = head.next; 
			while (p != null) {
				maxNum = Math.max(maxNum, p.mID);
				p = p.next;
			}
		}
		return 0;
	}

	class Node {
		int mID; // 이거 말곤 필요없음
		int mTeam;
		Node prev;
		Node next;

		Node() {
		}
	}
}
