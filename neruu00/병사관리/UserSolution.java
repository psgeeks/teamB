package 병사관리;

import 병사관리.UserSolution.Node;

class UserSolution {
	static Node[] soldiers = new Node[100001];
	static Node[][] head = new Node[6][6]; // mTeam * mScore
	static Node[][] tail = new Node[6][6]; // mTeam * mScore

	static class Node {
		int mID, mTeam;
		Node prev, next;

		Node() {}

		// mScore은 배열의 x축 정보로 알 수 있기에 따로 저장하지 않음
		// mTeam은 updateSoldier()에서 필요해서 저장함
		Node(int mID, int mTeam) {
			this.mID = mID;
			this.mTeam = mTeam;
		}
		
		void remove() {
			if (prev != null) prev.next = next;
			if (next != null) next.prev = prev;
			prev = next = null;
		}
	}

	private void merge(int mTeam, int fromScore, int toScore) {
		// 리스트에 원소가 없을 때는 바로 리턴
		if (head[mTeam][fromScore].next == tail[mTeam][fromScore]) return;

		// 리스트의 첫 원소와 마지막 원소를 미리 저장
		Node first = head[mTeam][fromScore].next;
		Node last = tail[mTeam][fromScore].prev;

		// fromScore 배열을 초기화 함
		head[mTeam][fromScore].next = tail[mTeam][fromScore];
		tail[mTeam][fromScore].prev = head[mTeam][fromScore];

		// toScore 리스트의 마지막 원소 불러옴
		Node targetPrev = tail[mTeam][toScore].prev;

		// toScore 리스트의 마지막 원소에 fromScore의 첫 원소를 이어 붙임
		targetPrev.next = first;
		first.prev = targetPrev;

		// 병합이 끝난 toScore 리스트의 마지막 원소 다음에 toScore의 tail을 연결함
		last.next = tail[mTeam][toScore];
		tail[mTeam][toScore].prev = last;
	}

	public void init() {
		for (int i = 1; i <= 5; i++) {
			for (int j = 1; j <= 5; j++) {
				head[i][j] = new Node();
				tail[i][j] = new Node();
				head[i][j].next = tail[i][j];
				tail[i][j].prev = head[i][j];
			}
		}
	}

	public void hire(int mID, int mTeam, int mScore) {
		// 객체가 한 번도 생성되지 않은 인덱스의 경우 객체를 생성해줌
		// 이미 객체가 생성 됐을 경우 mScore만 새로 기입
		// mID는 인덱스와 동일하니 따로 변경하지 않음
		if(soldiers[mID] == null) soldiers[mID] = new Node(mID, mTeam);
		else soldiers[mID].mTeam = mTeam;

		// 리스트의 마지막 요소로 soldier를 추가
		Node prev = tail[mTeam][mScore].prev;
		prev.next = soldiers[mID];
		soldiers[mID].prev = prev;
		soldiers[mID].next = tail[mTeam][mScore];
		tail[mTeam][mScore].prev = soldiers[mID];
	}

	public void fire(int mID) {
		soldiers[mID].remove();
		soldiers[mID] = null;
	}

	public void updateSoldier(int mID, int mScore) {
		int mTeam = soldiers[mID].mTeam;
		fire(mID);
		hire(mID, mTeam, mScore);
	}

	public void updateTeam(int mTeam, int mChangeScore) {
		// 점수 리스트 별로 각각 점수를 변경 한 후 변경된 점수에 해당하는 리스트 뒤에 붙여 넣음
		
		// 팀 점수가 증가하는 경우
		if (mChangeScore > 0) {
			// 5는 어차피 증가해도 5니까 볼 필요 없음
			// 큰 score의 리스트 먼저 봐야 중복 점수 변경이 없음
			for (int score = 4; score >= 1; score--) {
				int nextScore = Math.min(5, score + mChangeScore);
				merge(mTeam, score, nextScore);
			}
		} 
		
		// 팀 점수가 감소하는 경우
		else {
			// 1는 어차피 감소해도 1이니 볼 필요 없음
			// 작은 score의 리스트 먼저 봐야 중복 점수 변경이 없음
			for (int score = 2; score <= 5; score++) {
				int nextScore = Math.max(1, score + mChangeScore);
				merge(mTeam, score, nextScore);
			}
		}
	}

	public int bestSoldier(int mTeam) {
		for (int s = 5; s >= 1; s--) {
			Node curr = head[mTeam][s].next;
			if (curr == tail[mTeam][s]) continue;

			int bestID = Integer.MIN_VALUE;
			while (curr != tail[mTeam][s]) {
				bestID = Math.max(bestID, curr.mID);
				curr = curr.next;
			}
			if (bestID != Integer.MIN_VALUE) return bestID;
		}
		return -1;
	}
}