# 풀이
## 각 팀별 병사 리스트를 ArrayList로 구현하면?

- hire : O(1)
  - 병사를 뒤에 추가하면 된다.
- fire : O(n)
  - 병사 위치 찾기 & 병사 제거
  - 뒤에 있는 병사를 앞으로 한칸씩 이동
- updateSoldier : O(n)
  - 병사 위치 찾기 & 병사 점수 업데이트
- updateTeam : O(n)
  - 모든 병사들 점수 업데이트
- bestSoldier : O(n)
  - 병사들 탐색하며 최고 점수 값 찾기 

=> 시간 초과 발생

## 각 팀별 병사 리스트를 ArrayList로 구현하면?
### `soldiers[팀번호][점수] : 병사리스트`
팀 내부에서 1점-5점까지 병사들을 관리하는 링크드리스트 생성.
### `idToSoldiers[mID] : 병사 노드 주소`
fire, updateSoldier에서 mID로 병사 노드에 바로 접근하기 위해 따로 객체 관리 배열을 생성. 
- hire : O(1)
  - 팀번호, 점수에 해당하는 링크드리스트 맨 뒤에 삽입.
- fire : O(1)
  - idToSoldiers 배열로 바로 노드 접근
  - 앞, 뒤 노드 연결로 링크드리스트에서 해당 병사 제거
- updateSoldier : O(1)
  - 기존 병사 노드는 제거 (fire 호출)
  - 수정된 점수의 링크드리스트에 추가 (hire 호출)
- updateTeam : O(n)
  - 점수 증가, 감소 조건에 따라 링크드리스트 이동.
- bestSoldier : O(n) -> 호출 횟수가 작으므로 괜찮다.
  - 5점 병사 리스트부터 순회하며 탐색