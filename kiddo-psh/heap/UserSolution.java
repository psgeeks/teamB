
import java.util.TreeSet;

class UserSolution
{
	class City implements Comparable<City> {
		int index, dist;
		City (int index, int dist) {
			this.index = index;
			this.dist = dist;
		}
		
		@Override
		public int compareTo(City o) {
			if (this.dist != o.dist) return Integer.compare(o.dist, this.dist);
			return Integer.compare(this.index, o.index);
		}
	}
	
	TreeSet<City> set = new TreeSet<>();
	
	int n;
	int[] population = new int[10_000]; // population[i] = i번 도시 인구 수
	int[] time = new int[10_000]; // 	   time[i] = i번 <-> i+1번 도시 이동 시간
	int[] roads = new int[10_000]; //	   roads[i] = i번 <-> i+1번 도시 연결 도로 차수
	
	void init(int N, int mPopulation[])
	{
		n = N;
		set.clear();
		
		for (int i=0; i<N; i++) {
			population[i] = mPopulation[i];
		}
		
		for (int i=0; i<N-1; i++) {
			roads[i] = 1;
			time[i] = (population[i] + population[i+1]) / roads[i];
			set.add(new City(i, time[i]));
		}
		
		return;
	}

	int expand(int M)
	{
		int ret = 0;
		while(M-->0) {
			City city = set.pollFirst();
			
			int idx = city.index;
			roads[idx]++; // 차선 확장 
			time[idx] = (population[idx] + population[idx+1]) / roads[idx]; // 거리 갱신
			city.dist = time[idx];
			
			ret = city.dist;
			
			set.add(city);
		}
		
		return ret;
	}
	
	int calculate(int mFrom, int mTo)
	{
		if (mFrom > mTo) {
			int temp = mFrom;
			mFrom = mTo;
			mTo = temp;
		}
		
		int ret = 0;
		for (int i=mFrom; i<mTo; i++) {
			ret += time[i];
		}
		return ret;
	}
	
	int divide(int mFrom, int mTo, int K)
	{
		int left = 0, right = 10_000_000;
		while (left < right) {
			int mid = (left+right)/2;
			
			if (canDivide(mFrom, mTo, mid, K)) {
				right = mid;
			} else {
				left = mid+1;
			}
		}
		
		return left;
	}
	
	boolean canDivide(int from, int to, int limit, int k) {
		int sum = 0;
		int count = 1;
		
		for (int i=from; i<=to; i++) {
			if (sum + population[i] > limit) {
				sum = population[i];
				count++;
			} else {
				sum += population[i];
			}
			
			if (count > k) return false;
		}
		return true;
	}
}