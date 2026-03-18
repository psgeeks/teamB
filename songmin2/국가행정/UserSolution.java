package swea;

import java.util.PriorityQueue;

//174,844 kb
//메모리
//654 ms
//실행시간
class UserSolution
{
	//calculate를 log N에 하기 위한 펜윅 트리
	class Fenwick{
		int[] tree;
		int size;
		
		public Fenwick(int size) {
			tree = new int[size + 1];
			this.size = size;
		}
		void update(int idx, int diff) {
			while(idx <= size) {
				tree[idx] += diff;
				idx += (-idx & idx);
			}
		}
		int sum(int idx) {
			int result = 0;
			while(idx > 0) {
				result += tree[idx];
				idx -= (-idx & idx);
			}
			return result;
		}
	}
	// edge는 idx와 idx + 1 사이의 간선 갯수 저장
	int n;
	int[] population;
	int[] edge = new int[10001];
	Fenwick myFenwick;
	// expand 대상 선정을 위한 pq
	PriorityQueue<int[]> pq = new PriorityQueue<>((o1, o2)->{
		if(o1[0] != o2[0]) return o2[0] - o1[0];
		return o1[1] - o2[1];
	});
	
	void init(int N, int mPopulation[])
	{
		n = N;
		population = mPopulation;
		myFenwick = new Fenwick(n);
		pq.clear();
		
		for(int i = 0; i < n - 1; i++) {
			edge[i] = 1;
			int time = (population[i] + population[i + 1]) / edge[i];
			myFenwick.update(i + 1, time);
			pq.add(new int[] {time, i});
		}
	}
	// 간선 w = 양쪽 인구 힙 / 간선 수
	// 1개가 추가되면 새로운 w = 양쪽 인구 힙 / (간선 수 + 1)
	int expand(int M)
	{
		int[] cur = null;
		for(int i = 0; i < M; i++) {
			cur = pq.poll();
			int oldT = cur[0];
			int newT = (population[cur[1]] + population[cur[1] + 1]) / (edge[cur[1]] + 1);
			
			myFenwick.update(cur[1] + 1, newT - oldT);
			edge[cur[1]]++;
			
			cur[0] = newT;
			pq.add(cur);
		}		
		return (population[cur[1]] + population[cur[1] + 1]) / edge[cur[1]];
	}
	
	int calculate(int mFrom, int mTo)
	{
		if(mFrom > mTo) {int temp = mFrom; mFrom = mTo; mTo = temp;}
		
		return myFenwick.sum(mTo) - myFenwick.sum(mFrom);
	}
	
	// Parametric Search (최소 : 인구가 가장 많은 구역, 최대 : 모든 구역 인구의 합)
	// 선거구 최대 인구를 mid로 해보기
	// 가능 > 최대인구 더 낮춰보기
	// 불가능 > 최대인구 더 높이기
	int divide(int mFrom, int mTo, int K)
	{
		if(mFrom > mTo) {int temp = mFrom; mFrom = mTo; mTo = temp;}
		
		int start = 0;
		int end = 0;
		
		for(int i = mFrom; i <= mTo; i++) {
			if(start < population[i]) start = population[i];
			end += population[i];
		}
		
		int result = end;
		
		while(start <= end) {
			int mid = (start + end) / 2;
			if(canDiv(mFrom, mTo, K, mid)) {
				result = mid;
				end = mid - 1;
			}
			else {
				start = mid + 1;
			}
		}
		return result;
	}
	// 구역 인구 제한을 limit로 둘 때 몇개의 구역으로 나누어 짐?
	// 구분한 구역 갯수가 K를 넘어가면 false
	boolean canDiv(int mFrom, int mTo, int K, int limit) {
		
		int cnt = 1;
		int cur = 0;
		for(int i = mFrom; i <= mTo; i++) {
			int curP = population[i];
			if(cur + curP > limit) {
				cnt++;
				cur = curP;
				if(K < cnt) return false;
			}
			else cur += curP;
		}
		return true;
	}
}