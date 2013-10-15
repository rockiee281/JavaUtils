package com.liyun.dataMinning;

import java.io.File;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public class ItemBaseCF {
	public static void recommenderModelEvaluation(DataModel model) throws Exception {
		RecommenderIRStatsEvaluator evaluator = new GenericRecommenderIRStatsEvaluator();
		RecommenderBuilder builder = new RecommenderBuilder() {
			char similarityPattern = 'T';// 'E' or 'P' or 'L' or 'T'

			@Override
			public Recommender buildRecommender(DataModel dm) throws TasteException {
				ItemSimilarity similarity = null;
				switch (similarityPattern) {
				case 'E': {
					similarity = new EuclideanDistanceSimilarity(dm);
				}
				case 'P': {
					similarity = new PearsonCorrelationSimilarity(dm);
				}
				case 'L': {
					similarity = new LogLikelihoodSimilarity(dm);
				}
				case 'T': {
					similarity = new TanimotoCoefficientSimilarity(dm);
				}
				}

				return new GenericBooleanPrefItemBasedRecommender(dm, similarity);

			}
		};
		System.out.println(evaluator.evaluate(builder, null, model, null, 8,
				GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0));

//		ItemSimilarity itemSimilarity = new TanimotoCoefficientSimilarity(model);
//		ItemBasedRecommender recommender = new GenericBooleanPrefItemBasedRecommender(model, itemSimilarity);

		// item id 3608
//		List<RecommendedItem> recommendations = recommender.recommendedBecause(2, 22, 8);
//				mostSimilarItems(145, 8);

//		Recommender cachingRecommender = new CachingRecommender(recommender);
//		List<RecommendedItem> recommendations = cachingRecommender.recommend(2, 5);
//		for (RecommendedItem recommendedItem : recommendations) {
//			System.out.println(recommendedItem);
//		}
	}

	public static void main(String[] args) throws Exception {
		DataModel model = new FileDataModel(new File(UserBaseCF.class.getClassLoader().getResource("dataset_view")
				.getFile()));
		recommenderModelEvaluation(model);
	}
}
