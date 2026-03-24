package 광물운송;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

class UserSolution {
	
	class Camp implements Comparable<Camp> {
		int id, r, c, q;
		Camp(int id, int r, int c, int q) {
			this.id=id; this.r=r; this.c=c; this.q=q;
		}
		
		@Override
		public int compareTo(Camp o) {
			if (this.q != o.q) return Integer.compare(this.q, o.q);
			if (this.r != o.r) return Integer.compare(this.r, o.r);
			return Integer.compare(this.c, o.c);
		}
		
		boolean canConnect(Camp o) {
			return Math.abs(this.r-o.r) + Math.abs(this.c-o.c) <= l;
		}
	}
	
	Map<Integer, Integer> idToIdx = new HashMap<>();
	int index;
	
	@SuppressWarnings("unchecked")
	List<Camp>[][] map = new ArrayList[30][30];
	TreeSet<Camp> camps = new TreeSet<>();
	
	int[] rank = new int[20_000];
	int[] parent = new int[20_000];
	int[] quantity = new int[20_000];
	
	int l, n, len;
	
	int find(int x) {
		if (x == parent[x]) return x;
		return parent[x] = find(parent[x]);
	}
	
	boolean union(int a, int b) {
		a = find(a);
		b = find(b);
		
		if (a==b) return false;
		
		if (rank[a] < rank[b]) {int t=a; a=b; b=t;}
		
		parent[b] = a;
		quantity[a] += quantity[b];
		if (rank[a] == rank[b]) rank[a]++;
		
		return true;
	}
	
	void init(int L, int N){
		l=L; n=N;
		len=(N-1)/L+1; // 내림 
		
		index = 0;
		idToIdx.clear();
		camps.clear();
		
		for (int i=0; i<len; i++) {
			for (int j=0; j<len; j++) {
				if (map[i][j] == null) map[i][j] = new ArrayList<>();
				else map[i][j].clear();
			}
		}
	}
	
	int addBaseCamp(int mID, int mRow, int mCol, int mQuantity){
	    Camp newCamp = new Camp(mID, mRow, mCol, mQuantity);
	    camps.add(newCamp);

	    int newIdx = index++;
	    idToIdx.put(mID, newIdx);

	    parent[newIdx] = newIdx;
	    rank[newIdx] = 0;
	    quantity[newIdx] = mQuantity;

	    int br = mRow / l;
	    int bc = mCol / l;

	    for (int r = Math.max(0, br - 1); r <= Math.min(len - 1, br + 1); r++) {
	        for (int c = Math.max(0, bc - 1); c <= Math.min(len - 1, bc + 1); c++) {
	            for (Camp base : map[r][c]) {
	                if (newCamp.canConnect(base)) {
	                    union(newIdx, idToIdx.get(base.id));
	                }
	            }
	        }
	    }

	    map[br][bc].add(newCamp);

	    return quantity[find(newIdx)];
	}
	
	int findBaseCampForDropping(int K){
		
		for (Camp camp : camps) {
			int idx = idToIdx.get(camp.id);
			int pId = find(idx);
			if (quantity[pId] >= K) return camp.id;
		}
		
		return -1;
	}
}
