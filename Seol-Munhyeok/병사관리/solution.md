# 사고 과정
* `init()`에서 초기 자료구조를 어떻게 잡는게 좋을지 고민
* `mID`가 unique 함이 보장되어 있으니까 key로 사용할 수 있겠다. -> 배열에 매핑 정보를 저장하자
* `hire()`, `fire()` : 추가 및 삭제 연산이니까 자료구조만 잘 정의하면 쉬움. 삭제도 연결 리스트 쓰면 O(1)에 가능
* `updateSoldier()` : `mID`로 병사를 찾고 그 병사의 정보만 수정하면 되네 -> O(1) 가능
* `updateTeam()` : 소속팀이 `mTeam`인 모든 병사를 어떻게 다 찾고 변경하지? -> 일단 찾는데만 O(N)이라 무조건 느릴 거 같은데 (여기서 많이 고민) 
* `bestSoldier()` : 이거만 호출 횟수가 100이네? -> 이거는 O(N) 순회해도 괜찮겠다.

오랫동안 머리를 굴려봐도 `updateTeam()`을 O(N) 미만으로 줄일 방법이 떠오르지 않아 다른 사람 풀이를 참고해서 **버킷**의 아이디어를 알게되었고 이를 기반으로 구현 시작함

# 자료 구조 및 `init()`
```java
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
```
* 맨 뒤에 삽입을 쉽게하려고 `tail` 도입 및 `prev`를 가지는 **양방향 연결리스트**로 구현
  * 단방향으로 하면 삭제나 삽입할 때 이전 노드를 찾기 위해서 O(N) 순회를 해야해서 비효율적 
* 이 문제의 핵심적인 통찰은 `mTeam`이나 `mScore` 값의 범위가 적으니까 5 * 5 이차원 배열로 해서 각각의 연결리스트를 가지도록 구현해야 한다는 것.
* LinkedList 클래스를 만들어서 배열에 LinkedList를 넣도록 구현할까 처음에 생각하였으나  `head`와 `tail`만 알면 되니까 `head`와 `tail` 배열로만 구현
* 바로바로 `mID`로 노드와 팀 정보를 얻기 위해서 `nodeOf[]`와 `teamOf[]` 배열로 관리함.
  *  `scoreOf[]` 는 사실 할당만하고 조회하는 부분이 없어서 사실 필요없는 구현이었음.
* 처음에 연결리스트를 null로 두는 구현도 가능하지만, 이렇게 구현하면 null 처리를 따로해줘야하는 단점이 있어서 `head`와 `tail`에 더미 노드를 두는 식으로 구현하였음.
  * 이렇게 하면 별도로 null 예외 처리를 할 필요가 없어서 코드가 단순해지는 장점이 있음.
  * 그러나 항상 실제 데이터의 위치는 `head.next` ~ `tail.prev` 사이에 있다는 점을 기억하고 있어야 함.
 
# `hire()`, `fire()`
```java
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
```
* 연결리스트에서 `nextNode` 뒤에 `newNode`를 삽입하는 `insertBefore(Node nextNode, Node newNode)`과 해당 노드를 삭제하는 `remove(Node targetNode)`를 따로 구현
* `hire()`와 `fire()`에서 두 함수를 적절히 호출하고 `nodeOf[]`와 `teamOf[]` 에도 이를 기록함.
 
# `updateSoldier()`
```java
public void updateSoldier(int mID, int mScore) {
	Node targetNode = nodeOf[mID];
	int team = teamOf[mID];
	remove(targetNode);  // 기존에 있던 버킷에서 삭제
	
	insertBefore(tail[team][mScore], targetNode);  // 새로운 버킷에 추가
	scoreOf[mID] = mScore;
}
```
*  score update 시, 기존에 있던 버킷에서 삭제하고 변경된 score에 맞는 버킷에 새로 추가하는 방식으로 구현
 
# `updateTeam()`
```java
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
```

* `updateTeam()` 구현을 실제로 모든 노드를 하나하나 이동하는 것이 아닌, 포인터만 변경하는 방식으로 O(1)에 처리
* 점수 이동 시 기존에 있던 버킷이 영향을 받지 않게 하기위해 임시 버킷인 `temp`를 사용
* `updateTeam()`에서 가장 중요한 핵심적인 함수는 `appendBucket()`
* `appendBucket(Node dstTail, Node srcHead, Node srcTail)`은 `dstTail`의 뒤에 `srcHead...srcTail` 까지의 연결리스트를 이어붙이는 함수
* 이때 더미 노드를 사용하는 구현이므로 실제로 의미있는 데이터는 `head.next` 부터 `tail.prev` 사이에 있다는 점에 주의하여 구현

# `bestSoldier()`
```java
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
```
* 문제에서 `bestSoldier()`의 호출 횟수는 100 미만으로 명시되어 있어서 위와 같이 O(N) 로직으로도 충분히 가능함.
* 점수가 큰 병사를 찾는 거니까 버킷에서 `score`가 큰 병사부터 역순으로 탐색. 그 중 `id`가 가장 큰 병사를 반환.
