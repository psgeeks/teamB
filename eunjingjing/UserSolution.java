class UserSolution {
    // [1] 배열 기반 연결 리스트를 위한 Node 풀 (Pool)
    // hire(10만) + updateSoldier(10만) = 최대 20만 개의 노드가 생성될 수 있으므로 여유 있게 설정
    int[] nxt = new int[200005];
    int[] id = new int[200005];
    int[] v = new int[200005]; // 노드가 생성될 당시의 버전 저장
    
    // [2] 병사 상태 관리 배열
    int[] version = new int[100005]; // 해당 병사(mID)의 '최신 버전' 저장 (지연 삭제용)
    int[] team = new int[100005];    // 해당 병사(mID)의 소속팀 저장
    
    // [3] (팀, 평판 점수) 기준 2차원 단일 연결 리스트의 Head와 Tail
    int[][] head = new int[6][6];
    int[][] tail = new int[6][6];
    
    int nodeCnt;

    public void init() {
        nodeCnt = 0;
        // 각 테스트 케이스 시작 시 리스트 초기화
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 5; j++) {
                head[i][j] = 0;
                tail[i][j] = 0;
            }
        }
        // mID는 한 테스트 케이스 내에서 중복 고용되지 않으므로, 
        // version 배열은 굳이 매번 초기화하지 않고 증가시키는 방식으로 재사용 가능
    }
    
    public void hire(int mID, int mTeam, int mScore) {
        version[mID]++; // 병사의 버전을 갱신
        team[mID] = mTeam;
        
        // 새 노드 할당
        nodeCnt++;
        id[nodeCnt] = mID;
        v[nodeCnt] = version[mID]; // 갱신된 최신 버전 부여
        nxt[nodeCnt] = 0;
        
        // (팀, 점수) 리스트의 꼬리에 추가 (O(1))
        if (head[mTeam][mScore] == 0) {
            head[mTeam][mScore] = tail[mTeam][mScore] = nodeCnt;
        } else {
            nxt[tail[mTeam][mScore]] = nodeCnt;
            tail[mTeam][mScore] = nodeCnt;
        }
    }
    
    public void fire(int mID) {
        // 배열을 순회해서 지우지 않고 버전만 변경하여 기존 노드를 '유령(더미) 데이터'로 만듦 (O(1))
        version[mID]++; 
    }
    
    public void updateSoldier(int mID, int mScore) {
        // 기존 노드를 무효화하고 새로운 점수에 새 노드를 추가 (O(1))
        version[mID]++;
        
        nodeCnt++;
        id[nodeCnt] = mID;
        v[nodeCnt] = version[mID];
        nxt[nodeCnt] = 0;
        
        int mTeam = team[mID];
        
        if (head[mTeam][mScore] == 0) {
            head[mTeam][mScore] = tail[mTeam][mScore] = nodeCnt;
        } else {
            nxt[tail[mTeam][mScore]] = nodeCnt;
            tail[mTeam][mScore] = nodeCnt;
        }
    }
    
    public void updateTeam(int mTeam, int mChangeScore) {
        if (mChangeScore == 0) return;
        
        if (mChangeScore > 0) {
            // 점수가 오를 때는 5점부터 1점 순으로 역순 탐색하여 리스트가 두 번 합쳐지는 것 방지
            for (int s = 5; s >= 1; s--) {
                if (head[mTeam][s] == 0) continue;
                
                int nextS = s + mChangeScore;
                if (nextS > 5) nextS = 5; // 5보다 클 경우 5로 변경
                if (nextS == s) continue;
                
                // O(1) 리스트 꼬리 병합
                if (head[mTeam][nextS] == 0) {
                    head[mTeam][nextS] = head[mTeam][s];
                    tail[mTeam][nextS] = tail[mTeam][s];
                } else {
                    nxt[tail[mTeam][nextS]] = head[mTeam][s];
                    tail[mTeam][nextS] = tail[mTeam][s];
                }
                head[mTeam][s] = tail[mTeam][s] = 0; // 기존 리스트 비우기
            }
        } else {
            // 점수가 내릴 때는 1점부터 5점 순으로 탐색
            for (int s = 1; s <= 5; s++) {
                if (head[mTeam][s] == 0) continue;
                
                int nextS = s + mChangeScore;
                if (nextS < 1) nextS = 1; // 1보다 작을 경우 1로 변경
                if (nextS == s) continue;
                
                // O(1) 리스트 꼬리 병합
                if (head[mTeam][nextS] == 0) {
                    head[mTeam][nextS] = head[mTeam][s];
                    tail[mTeam][nextS] = tail[mTeam][s];
                } else {
                    nxt[tail[mTeam][nextS]] = head[mTeam][s];
                    tail[mTeam][nextS] = tail[mTeam][s];
                }
                head[mTeam][s] = tail[mTeam][s] = 0; // 기존 리스트 비우기
            }
        }
    }
    
    public int bestSoldier(int mTeam) {
        // 평판 점수가 가장 높은 병사 검색
        for (int s = 5; s >= 1; s--) {
            int maxId = 0;
            int curr = head[mTeam][s];
            
            while (curr != 0) {
                int cId = id[curr];
                // 노드의 버전과 병사의 최신 버전이 일치할 때만 유효한 데이터로 인정 (지연 삭제 로직)
                if (v[curr] == version[cId]) { 
                    if (cId > maxId) {
                        maxId = cId;
                    }
                }
                curr = nxt[curr];
            }
            
            // 해당 점수대에 유효한 병사가 한 명이라도 있다면, 그중 최대 ID를 반환하고 즉시 종료
            if (maxId != 0) return maxId;
        }
        return 0;
    }
}