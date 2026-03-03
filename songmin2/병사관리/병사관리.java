class 병사관리 {
    //병사 정보를 저장하는 더블 링크드 리스트 노드 클래스
    class Node {
        int id;
        Node prev, next;

        Node(int id) {
            this.id = id;
        }
    }

    // head: 각 팀별, 점수별 리스트의 머리, tail: 꼬리
    // [팀][점수]
    Node[][] head = new Node[6][6];
    Node[][] tail = new Node[6][6];

    // 고유번호(mID)로 노드 객체에 즉시 접근하기 위한 배열 (O(1) 접근용)
    Node[] allSoldiers = new Node[100001];
    
    // 팀은 안변함, updateSoldier O(1)용
    int[] team = new int[100001];

    //각 팀(1~5)과 점수(1~5) 노드를 생성하고 연결한다.
    public void init() {
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 5; j++) {
                head[i][j] = new Node(0);
                tail[i][j] = new Node(0);
                head[i][j].next = tail[i][j];
                tail[i][j].prev = head[i][j];
            }
        }
    }

    
    //새로운 병사를 고용하여 해당 팀과 점수에 추가한다.
    public void hire(int mID, int mTeam, int mScore) {
        Node soldier = new Node(mID);
        allSoldiers[mID] = soldier;
        team[mID] = mTeam;

        // 해당 팀/점수 리스트의 마지막 노드와 tail 사이에 삽입 (tail은 항상 꼬리여야 함)
        Node last = tail[mTeam][mScore].prev;
        tail[mTeam][mScore].prev = soldier;
        last.next = soldier;
        soldier.prev = last;
        soldier.next = tail[mTeam][mScore];
    }

    //고유번호를 이용해 병사를 리스트에서 제거한다.
    public Node fire(int mID) {
        Node soldier = allSoldiers[mID];
        // 이전 노드와 다음 노드를 연결 (중간 끊어냄)
        soldier.prev.next = soldier.next;
        soldier.next.prev = soldier.prev;
        return soldier;
    }

    //특정 병사의 점수를 변경
    public void updateSoldier(int mID, int mScore) {
        // 기존 리스트에서 제거 후 반환
        Node node = fire(mID);
        int mTeam = team[mID];

        // 새로운 점수 tail 앞에 다시 삽입
        Node last = tail[mTeam][mScore].prev;
        tail[mTeam][mScore].prev = node;
        last.next = node;
        node.prev = last;
        node.next = tail[mTeam][mScore];
    }

    
    //팀 전체의 점수를 일괄 변경한다. 
    //리스트의 노드를 하나씩 옮기지 않고, 링크드 리스트를 통째로 이어 붙임
    //예) 2->3점 갱신의 경우 3점 리스트 끝에 2점 리스트 이어붙이고 2점 리스트 가르키던 포인터 초기화
    public void updateTeam(int mTeam, int mChangeScore) {
        if (mChangeScore == 0) return;

        // 점수가 오르는 경우: 높은 점수부터 처리하여 데이터 덮어쓰기 방지
        if (mChangeScore > 0) {
            for (int i = 5; i >= 1; i--) {
                int nextScore = Math.min(5, i + mChangeScore);
                if (nextScore == i) continue;
                moveNodes(mTeam, i, nextScore);
            }
        } 
        // 점수가 낮아지는 경우: 낮은 점수부터 처리
        else {
            for (int i = 1; i <= 5; i++) {
                int nextScore = Math.max(1, i + mChangeScore);
                if (nextScore == i) continue;
                moveNodes(mTeam, i, nextScore);
            }
        }
    }
    // fromScore 버킷에 있는 모든 병사를 toScore 버킷의 끝으로 이동시킨다.
    private void moveNodes(int team, int fromScore, int toScore) {
        // 이동시킬 리스트가 비어있으면 리턴
        if (head[team][fromScore].next == tail[team][fromScore]) return;

        Node fromFirst = head[team][fromScore].next;
        Node fromLast = tail[team][fromScore].prev;
        Node toTarget = tail[team][toScore].prev;

        // 1. toScore 리스트의 마지막 노드와 fromScore의 첫 노드 연결
        toTarget.next = fromFirst;
        fromFirst.prev = toTarget;

        // 2. fromScore의 마지막 노드와 toScore의 tail 연결
        fromLast.next = tail[team][toScore];
        tail[team][toScore].prev = fromLast;

        // 3. 기존 fromScore 리스트 비우기 (초기화)
        head[team][fromScore].next = tail[team][fromScore];
        tail[team][fromScore].prev = head[team][fromScore];
    }

    
    // 팀 내에서 최고 점수를 가진 병사 중 ID가 가장 큰 병사를 찾는다.
    public int bestSoldier(int mTeam) {
        int maxID = 0;
        // 높은 점수 버킷(5)부터 순회
        for (int i = 5; i >= 1; i--) {
            if (head[mTeam][i].next == tail[mTeam][i]) continue;

            Node cur = head[mTeam][i].next;
            // 해당 점수 버킷 내에서 가장 큰 ID 탐색
            while (cur != tail[mTeam][i]) {
                if (maxID < cur.id) maxID = cur.id;
                cur = cur.next;
            }

            if (maxID != 0) return maxID;
        }
        return 0;
    }
}