package com.liyun.dataMinning;

import java.io.File;
import java.util.List;

import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.common.RandomUtils;

public class ItemBaseCF {
	public static void recommenderModelEvaluation(DataModel model) throws Exception {
		RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		RandomUtils.useTestSeed();
		// RecommenderBuilder builder = new RecommenderBuilder() {
		// // =============ʵ���������===============
		// // 1.K���� or ��ֵ����
		// // ���ڣ�K��
		// // ��ֵ���ڣ�threshold��
		// // 2.���ƶ�����Euclidean �� Pearson �� Log-likelihood �� Tanimoto
		// char similarityPattern = 'E';// 'E' or 'P' or 'L' or 'T'
		//
		// @Override
		// public Recommender buildRecommender(DataModel dm) throws
		// TasteException {
		// ItemSimilarity similarity = null;
		// switch (similarityPattern) {
		// case 'E': {
		// similarity = new EuclideanDistanceSimilarity(dm);
		// }
		// case 'P': {
		// similarity = new PearsonCorrelationSimilarity(dm);
		// }
		// case 'L': {
		// similarity = new LogLikelihoodSimilarity(dm);
		// }
		// case 'T': {
		// similarity = new TanimotoCoefficientSimilarity(dm);
		// }
		// }
		//
		// return new GenericItemBasedRecommender(dm, similarity);
		// }
		// };

		ItemSimilarity itemSimilarity = new TanimotoCoefficientSimilarity(model);
		ItemBasedRecommender recommender = new GenericBooleanPrefItemBasedRecommender(model, itemSimilarity);

		// item id 3608
		List<RecommendedItem> recommendations = recommender.mostSimilarItems(3608, 5);
		// Recommender cachingRecommender = new CachingRecommender(recommender);
		// List<RecommendedItem> recommendations =
		// cachingRecommender.recommend(5, 5);
		for (RecommendedItem recommendedItem : recommendations) {
			System.out.println(recommendedItem);
		}
	}

	public static void main(String[] args) throws Exception {
		DataModel model = new FileDataModel(new File(UserBaseCF.class.getClassLoader().getResource("mydata.txt")
				.getFile()));
		recommenderModelEvaluation(model);
	}
}
