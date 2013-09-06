package com.liyun.dataMinning;

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.SingularValueDecomposition;

public class SVD {
	public static void main(String[] args) {
		Matrix m = null;
		SingularValueDecomposition svd = new SingularValueDecomposition(m);
		svd.getS();
	}
}
