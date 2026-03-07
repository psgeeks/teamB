# 1. 자료구조 설계

이 문제는 팀별, 점수별 병사의 접근이 필요하다.
팀과 점수는 1~5 사이의 숫자로 작기 때문에 
각각의 팀, 점수마다 리스트를 따로 만들어서 관리하면
효율적으로 병사들을 관리가능하다.

## 버킷

각 팀과 점수 조합마다 연결리스트를 둔다.

```

LinkedList soldiers[team][score]

```

총 버킷 수

```

5 × 5 = 25

```

각 버킷에는 해당 team과 score를 가진 병사가 들어간다.

---

## Soldier 노드

```

class Soldier {
int id
int team
Soldier prev
Soldier next
}

```

score는 저장하지 않고 점수는 **버킷 위치로 판단한다.**

---

## 병사 정보 배열

ID로 바로 접근하기 위해 배열 사용

```

Soldier soldierInfo[ID]

```

---

# 2. 연산 구현

## hire

```

1. Soldier 생성
2. soldiers[team][score]에 삽입
3. soldierInfo[id] 저장

```

시간복잡도 `O(1)`

---

## fire

```

1. soldierInfo[id]로 노드 찾기
2. 연결리스트에서 제거
3. soldierInfo[id] = null

```

시간복잡도 `O(1)`

---

## updateSoldier

```

fire(id)
hire(id, team, newScore)

```

시간복잡도 `O(1)`

---

## updateTeam

병사를 하나씩 이동하지 않는다.

```

리스트 자체를 이어붙인다. 

```

점수 증가

```

score = 4 → 1
newScore = min(5, score + change)

```

점수 감소

```

score = 2 → 5
newScore = max(1, score + change)

```

시간복잡도 `O(1)`

---

## bestSoldier

요구사항

```

가장 높은 score
동점이면 가장 큰 ID

```

방법

```

for score = 5 → 1
버킷 순회
max ID 찾기
존재하면 return

```

---

# 3. 핵심 아이디어

이 문제의 핵심

```

버킷 분할
+
연결리스트
+
ID direct access
+
리스트 병합

```

---

# 4. 최종 설계

```

25개의 버킷
각 버킷 연결리스트
ID → 노드 배열
updateTeam은 리스트 병합

```

이 설계로 모든 연산을 효율적으로 처리할 수 있다.
```
