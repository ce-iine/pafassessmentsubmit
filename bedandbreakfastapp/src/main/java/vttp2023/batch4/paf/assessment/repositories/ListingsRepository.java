package vttp2023.batch4.paf.assessment.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import vttp2023.batch4.paf.assessment.Utils;
import vttp2023.batch4.paf.assessment.models.Accommodation;
import vttp2023.batch4.paf.assessment.models.AccommodationSummary;

@Repository
public class ListingsRepository {
	
	// You may add additional dependency injections

	@Autowired
	private MongoTemplate template;

	/*
	     db.listings.aggregate([
     {$group:{_id:'$address.street'}},
    {
        $project: {
            "_id":1,
        }
    }
]);
	 */


	public List<String> getSuburbs(String country) {
		GroupOperation group = Aggregation.group("address.street");
        ProjectionOperation pOperation = Aggregation.project("_id");
        Aggregation pipeline = Aggregation.newAggregation(group, pOperation);
        List<Document> output = template.aggregate(pipeline,"listings", Document.class).getMappedResults();
		// System.out.println("SUBURB DOCUMENT LIST>>>>" +output);

		List<String> result = new ArrayList<>();
		for (Document d : output){
			String one = d.getString("_id");
            result.add(one);
		}
        // System.out.println("SUBURBS LIST>>>>" +result);
		return result;
	}

	/*
db.listings.aggregate([
    {
        $match: {
            'address.suburb':{$regex:"bondi", $options:"i"},
            price: {$lte: 800 },
            accommodates: {$gte: 2},
            min_nights: {$lte: 2}
        }
    }, {
        $sort: { price: -1 }
    },
    {
        $project: {
            "_id":1,
            "price": 1,
            "name": 1,
            "accommodates": 1,
        }
    }
]);
	 */
	public List<AccommodationSummary> findListings(String suburb, int persons, int duration, float priceRange) {

		// MatchOperation matchOps = Aggregation.match(Criteria.where("address.suburb").is(suburb)
        //     .and("accommodates").gte(persons)
        //     .and("price").lte(priceRange)
		// 	.and("min_nights").lte(duration));

			MatchOperation matchOps = Aggregation.match(Criteria.where("accommodates").gte(persons)
            .and("price").lte(priceRange)
			.and("min_nights").lte(duration));

        SortOperation sortBy = Aggregation.sort(Sort.by(Direction.DESC,"price"));
        ProjectionOperation pOperation = Aggregation.project("_id","price","name", "accommodates");

        Aggregation pipeline = Aggregation.newAggregation(matchOps, sortBy, pOperation);

        List<Document> output = template.aggregate(pipeline,"listings", Document.class).getMappedResults();
		System.out.println("SEARCHED LISTINGS>>>>" +output);

		List<AccommodationSummary> allListings = new ArrayList<>();

        for (Document d : output) {
			String id = d.getString("_id");
            String name = d.getString("name");
            float price = d.get("price", Number.class).floatValue();
            int accomodates = d.getInteger("accommodates");
			AccommodationSummary newSumm = new AccommodationSummary();
			newSumm.setId(id);
			newSumm.setName(name);
			newSumm.setPrice(price);
			newSumm.setAccomodates(accomodates);
            allListings.add(newSumm);
        }

		// System.out.println("ALL LISTINGS CONVERTED >>>>>" + allListings);
		return allListings; 
	}

	// IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
	// If this method is changed, any assessment task relying on this method will
	// not be marked
	public Optional<Accommodation> findAccommodatationById(String id) {
		Criteria criteria = Criteria.where("_id").is(id);
		Query query = Query.query(criteria);

		List<Document> result = template.find(query, Document.class, "listings");
		if (result.size() <= 0)
			return Optional.empty();

		return Optional.of(Utils.toAccommodation(result.getFirst()));
	}

}