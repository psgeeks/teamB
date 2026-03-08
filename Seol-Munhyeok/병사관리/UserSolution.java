package 병사관리;

class UserSolution {
	
	static class Node {
		int id;
		Node prev, next;
	}
	
	final int MAX_TEAM = 5;
	final int MAX_SCORE = 5;
	final int MAX_ID = 100_000;
	
	Node[][] head = new Node[MAX_TEAM + 1][MAX_SCORE + 1];
	Node[][] tail = new Node[MAX_TEAM + 1][MAX_SCORE + 1];
	
	Node[] nodeOf = new Node[MAX_ID + 1];
	int[] teamOf = new int[MAX_ID + 1];
	int[] scoreOf = new int[MAX_ID + 1];
	
	public void init() {
		// 간편한 구현을 위한 더미 노드 추가
		for (int t = 1; t <= MAX_TEAM; t++) {
			for (int s = 1; s <= MAX_SCORE; s++) {
				head[t][s] = new Node();
				tail[t][s] = new Node();
				
				head[t][s].next = tail[t][s];
				tail[t][s].prev = head[t][s];
			}
		}
	}
	
	private void insertBefore(Node nextNode, Node newNode) {
		Node prevNode = nextNode.prev;
		newNode.next = nextNode;
		newNode.prev = prevNode;
		prevNode.next = newNode;
		nextNode.prev = newNode;
	}
	
	private void remove(Node targetNode) {
		targetNode.prev.next = targetNode.next;
		targetNode.next.prev = targetNode.prev;
	}
	
	public void hire(int mID, int mTeam, int mScore) {
		Node node = new Node();
		node.id = mID;
		
		nodeOf[mID] = node;
		teamOf[mID] = mTeam;
		scoreOf[mID] = mScore;
		
		// 연결 리스트 맨 끝에 삽입
		insertBefore(tail[mTeam][mScore], node);
	}
	
	public void fire(int mID) {
		Node targetNode = nodeOf[mID];
		remove(targetNode);
	}
	
	public void updateSoldier(int mID, int mScore) {
		Node targetNode = nodeOf[mID];
		int team = teamOf[mID];
		remove(targetNode);  // 기존에 있던 버킷에서 삭제
		
		insertBefore(tail[team][mScore], targetNode);  // 새로운 버킷에 추가
		scoreOf[mID] = mScore;
	}

	private int cut(int score) {
		if (score > 5) return 5;
		if (score < 1) return 1;
		return score;
	}
	
	// dstTail 뒤에 srcHead...srcTail 까지의 전체 리스트를 이어 붙임
	private void appendBucket(Node dstTail, Node srcHead, Node srcTail) {
		// src가 비어 있으면 아무것도 안함
		if (srcHead.next == srcTail) return;
		
		Node first = srcHead.next;  // 더미 노드가 아닌 실제 데이터가 있는 노드
		Node last = srcTail.prev;
		Node prev = dstTail.prev;
		
		// dst 뒤에 src 전체 붙이기
		prev.next = first;
		first.prev = prev;
		
		last.next = dstTail;
		dstTail.prev = last;
		
		// src 비우기
		srcHead.next = srcTail;
		srcTail.prev = srcHead;
	}
	
	public void updateTeam(int mTeam, int mChangeScore) {
		// 점수 이동을 위한 임시 버킷 생성
		Node[] tempHead = new Node[MAX_SCORE + 1];
		Node[] tempTail = new Node[MAX_SCORE + 1];
		
		for (int s = 1; s <= MAX_SCORE; s++) {
			tempHead[s] = new Node();
			tempTail[s] = new Node();
			
			tempHead[s].next = tempTail[s];
			tempTail[s].prev = tempHead[s];
		}
		
		// 기존 버킷들을 temp 버킷으로 재배치
		for (int s = 1; s <= MAX_SCORE; s++) {
			int ns = cut(s + mChangeScore);
			appendBucket(tempTail[ns], head[mTeam][s], tail[mTeam][s]);
		}
		
		// temp 버킷 내용을 다시 실제 버킷으로 옮김
		for (int s = 1; s <= MAX_SCORE; s++) {
			appendBucket(tail[mTeam][s], tempHead[s], tempTail[s]);
		}
	}

	public int bestSoldier(int mTeam) {
		int bestMaxID = 0;
		for (int s = MAX_SCORE; s >= 1; s--) {
			Node cur = head[mTeam][s].next;
			while (cur != tail[mTeam][s]) {
				bestMaxID = Math.max(bestMaxID, cur.id);
				cur = cur.next;
			}
			if (bestMaxID != 0) return bestMaxID;
		}
		
		return 0;
	}
}
