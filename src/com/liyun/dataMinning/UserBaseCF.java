package com.liyun.dataMinning;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.AveragingPreferenceInferrer;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class UserBaseCF {
	public static void main(String[] args) throws IOException, Exception {
		DataModel dataModel = new FileDataModel(new File(UserBaseCF.class.getClassLoader().getResource("mydata.txt")
				.getFile()));
		UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(dataModel);
		userSimilarity.setPreferenceInferrer(new AveragingPreferenceInferrer(dataModel));

		UserNeighborhood neighborhood = new NearestNUserNeighborhood(3, userSimilarity, dataModel);

		Recommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, userSimilarity);
		Recommender cachingRecommender = new CachingRecommender(recommender);
		List<RecommendedItem> recommendations = cachingRecommender.recommend(5, 5);
		for (RecommendedItem recommendedItem : recommendations) {
			System.out.println(recommendedItem);
		}
		
	}
}
