# 최종 구현 구조 분석

## 📌 자료구조 설계

### 1️⃣ Node 클래스

```java
class Node {
    int id;
    Node prev, next;

    Node(int id) {
        this.id = id;
    }
}
```

- 병사 ID만 저장
- 점수는 저장하지 않음
- 양방향 연결 리스트 구조

---

### 2️⃣ 2차원 버킷 구조

```java
Node[][] head = new Node[6][6];
Node[][] tail = new Node[6][6];
```

- `[팀][점수]` 구조
- 팀: 1~5
- 점수: 1~5
- 각 팀-점수마다 독립적인 이중 연결 리스트 존재

---

### 3️⃣ O(1) 접근을 위한 배열

```java
Node[] allSoldiers = new Node[100001];
int[] team = new int[100001];
```

- `allSoldiers[mID]` → 해당 병사의 노드 바로 접근
- `team[mID]` → 병사의 팀 정보 저장 (팀은 변하지 않음)

👉 `fire`, `updateSoldier`를 O(1)에 처리 가능

---

# 연산별 동작 원리

---

## 1️⃣ init()

- 모든 팀(1~5), 점수(1~5)에 대해
- 더미 head / tail 노드 생성
- 서로 연결

👉 빈 리스트 상태 초기화

---

## 2️⃣ hire(mID, mTeam, mScore)

### 동작
- 새로운 노드 생성
- 해당 팀-점수 리스트의 **tail 바로 앞에 삽입**

### 시간복잡도
👉 O(1)

---

## 3️⃣ fire(mID)

### 동작
- 노드를 직접 찾아서
- `prev.next` 와 `next.prev` 연결

```java
soldier.prev.next = soldier.next;
soldier.next.prev = soldier.prev;
```

👉 중간에서 바로 제거 가능

### 시간복잡도
👉 O(1)

---

## 4️⃣ updateSoldier(mID, mScore)

### 동작
1. 기존 리스트에서 제거 (`fire`)
2. 새 점수 버킷 tail 앞에 삽입

👉 개별 병사 점수 변경도 O(1)

---

## 5️⃣ updateTeam(mTeam, mChangeScore)

### 🔥 핵심 최적화

병사를 하나씩 이동하지 않음

대신:

> 리스트를 통째로 이어 붙인다

---

### 점수가 올라가는 경우

```java
for (int i = 5; i >= 1; i--)
```

- 높은 점수부터 처리
- 덮어쓰기 방지

---

### 점수가 내려가는 경우

```java
for (int i = 1; i <= 5; i++)
```

- 낮은 점수부터 처리

---

### moveNodes()

```java
private void moveNodes(int team, int fromScore, int toScore)
```

#### 동작 과정

1️⃣ from 리스트의 첫 노드 ~ 마지막 노드 구함  
2️⃣ to 리스트의 tail 앞에 연결  
3️⃣ from 리스트는 비워버림

```java
// 1. 연결
toTarget.next = fromFirst;
fromFirst.prev = toTarget;

// 2. tail 연결
fromLast.next = tail[team][toScore];
tail[team][toScore].prev = fromLast;

// 3. from 초기화
head[team][fromScore].next = tail[team][fromScore];
tail[team][fromScore].prev = head[team][fromScore];
```

### 시간복잡도

👉 병사 수와 무관  
👉 점수는 1~5 고정  
👉 **O(1)**

이게 이 문제의 핵심

---

## 6️⃣ bestSoldier(mTeam)

### 동작

1. 점수 5 → 1 순회
2. 해당 버킷이 비어있지 않으면
3. 내부에서 가장 큰 ID 탐색

```java
while (cur != tail[mTeam][i]) {
    if (maxID < cur.id) maxID = cur.id;
}
```

### 시간복잡도

- 최악: 한 점수 버킷에 병사가 몰린 경우 O(N)
- 하지만 문제 조건상 허용

---

# 구현 중 어려웠던 점

## 🔹 Doubly Linked List 구현

- 연결 순서가 까다로움
- prev / next 설정 순서 중요
- tail 앞 삽입 구조 이해 필요

하지만 해결

---

# 추가 개선 아이디어 (Lazy 방식)

현재는 `fire()`에서 바로 제거

하지만 다른 방식도 가능:

- 삭제 시 실제 제거하지 않고
- `dead` 플래그만 표시
- `bestSoldier` 호출 시 정리

👉 Single Linked List + Lazy 삭제도 가능

---

# 최종 정리

## ❌ 실패 원인

- 병사를 개별적으로 업데이트하려는 접근
- 배열 기반 사고에서 벗어나지 못함

## ✅ 성공 포인트

- 점수를 병사 객체에 저장하지 않음
- 팀 × 점수별로 버킷화
- 병사 이동이 아니라 **리스트를 이동**
- 연결 리스트의 포인터 조작으로 해결

---

# 배운 점

- 대량 업데이트가 있는 문제는
  - **구조를 먼저 설계해야 한다**
- 문제의 병목을 먼저 찾는 것이 중요

---