import java.util.*;

class UserSolution
{
	int N;
	int[] mPopulation;
	int[] mDegree; // mDegree[0] : 0과 1 사이 도로 수 
	int[] mSum; // 이동도로 시간에 대한 구간합 저장 
	int[] mPrefixSum; // 인구 수 에 대한 누적합 저장 
	
	PriorityQueue<Road> pq;
	
	class Road{
		int idx;
		int weight;
		Road(int idx, int weight){
			this.idx = idx;
			this.weight = weight;
		}
	}
	
	void init(int N, int mPopulation[])
	{
		this.N = N;
		this.mPopulation = mPopulation;
		
		// 도로 수 저장
		mDegree = new int[N-1];
		Arrays.fill(mDegree, 1);
		
		// 구간합 저장하기
		mSum = new int[4*N];
		initMSum(1, 0, N-2);
		
		// 누적합 계산
		mPrefixSum = new int[N];
		mPrefixSum[0] = mPopulation[0];
		for(int i = 1; i < N; i++) {
			mPrefixSum[i] = mPrefixSum[i-1] + mPopulation[i];
		}
		
		// 가장 긴 도로를 빠르게 찾기 위해 우선순위큐에 도로 정보 넣기 
		pq = new PriorityQueue<>((a, b)->{
			if(a.weight != b.weight) return b.weight - a.weight;
			return a.idx - b.idx;
		});
		for(int i = 0; i < N-1; i++) {
			pq.add(new Road(i, mPopulation[i]+mPopulation[i+1]));
		}
	}
	
	void initMSum(int idx, int start, int end) {
		if(start == end) {
			mSum[idx] = mPopulation[start] + mPopulation[start+1];
			return;
		}
		int mid = (start+end)/2;
		initMSum(2*idx, start, mid);
		initMSum(2*idx+1, mid+1, end);
		mSum[idx] = mSum[idx*2]+mSum[idx*2+1];
	}
	
	int expand(int M)
	{
		int res = 0;
		while(M-- > 0) {
			// 현재 가장 이동시간이 긴 도로 
			Road maxRoad = pq.poll();
			
			// mSum update 수행해야함.
			// maxIdx 도로의 차수를 하나 늘린다.
			mDegree[maxRoad.idx]++;
			updateMSum(1, 0, N-2, maxRoad.idx);
			res = (mPopulation[maxRoad.idx] + mPopulation[maxRoad.idx+1]) / mDegree[maxRoad.idx];
			// 우선순위큐에 업데이트 정보 추가'
			pq.add(new Road(maxRoad.idx, res));
		}
		return res;
	}
	
	void updateMSum(int idx, int start, int end, int target) {
		if(end < target || start > target) return;
		
		if(start == end && start == target) {
			mSum[idx] = (mPopulation[start]+mPopulation[start+1]) / mDegree[start];
			return;
		}
		int mid = (start + end) / 2;
		updateMSum(2*idx, start, mid, target);
		updateMSum(2*idx+1, mid+1, end, target);
		mSum[idx] = mSum[2*idx] + mSum[2*idx+1];
	}
	
	int calculate(int mFrom, int mTo)
	{
		if(mFrom > mTo) {
			int tmp = mFrom;
			mFrom = mTo;
			mTo = tmp;
		}
		
		return getMSum(1, 0, N-2, mFrom, mTo-1);
	}
	
	int getMSum(int idx, int start, int end, int tStart, int tEnd) {

		if(end < tStart || start > tEnd) return 0;
				
		if(start >= tStart && end <= tEnd) {
			return mSum[idx];
		}
		
		int mid = (start + end) / 2;
		return getMSum(2*idx, start, mid, tStart, tEnd) + getMSum(2*idx+1, mid+1, end, tStart, tEnd);
	}
	
	int divide(int mFrom, int mTo, int K)
	{
		if(mFrom > mTo) {
			int tmp = mFrom;
			mFrom = mTo;
			mTo = tmp;
		}
		
		
		// 선거구의 최대 인구 
		int start = 1, end = mPrefixSum[N-1];
		int res = 0;
		while(start <= end) {
			// 선거구의 최대 인구 수 
			int mid = (start+end)/2;
			
			int cnt = 1; // 현재 선거구 개수 
			int sum = 0; // 현재 인구 수 
			for(int i = mFrom; i <= mTo; i++) {
				if(sum + mPopulation[i] > mid) {
					cnt++;
					sum = 0;
				}
				sum += mPopulation[i];
			}
			
			if(cnt <= K) {
				res = mid;
				end = mid-1;
			}else {
				start = mid+1;
			}
		}
		
		return res;
	}
}