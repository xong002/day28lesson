package practice.day28lesson.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class TVShowsRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<String> getAllTypes() {
        List<String> result = mongoTemplate.findDistinct(new Query(), "type", "tvshows", String.class);
        System.out.println(result.isEmpty());
        return result;
    }

    /*
     * db.tv_shows.aggregate([
     * {
     * $match: {language: {$regex: 'japan', $options: 'i'}}
     * }
     * ])
     */
    public List<Document> findShowsByLanguage(String lang) {
        MatchOperation langMatch = Aggregation.match(
                Criteria.where("language").regex(lang, "i"));
        Aggregation pipeline = Aggregation.newAggregation(langMatch);
        AggregationResults<Document> result = mongoTemplate.aggregate(pipeline, "tvshows", Document.class);
        return result.getMappedResults();
    }

    /*
     * db.tvshows.aggregate([
     * {
     * $project: {
     * _id: 1, id: 1,
     * title: {
     * $concat: [ '$name', '-', {$toString: '$runtime'}]
     * }
     * }
     * }
     * ])
     * 
     */
    public List<Document> getTitleSummary() {
        // ProjectionOperation summarizeTitle = Aggregation
        // .project("_id", "id")
        // .and("name")
        // .as("title");

        ProjectionOperation summarizeTitle = Aggregation
                .project("_id", "id")
                .and(
                        AggregationExpression.from(
                                MongoExpression.create("$concat: [ '$name', '-', {$toString: '$runtime'}]") // using
                                                                                                            // exact
                                                                                                            // same code
                                                                                                            // used in
                                                                                                            // Mongo
                        ))
                .as("title");

        Aggregation pipeline = Aggregation.newAggregation(summarizeTitle);
        return mongoTemplate.aggregate(pipeline, "tvshows", Document.class).getMappedResults();
    }

    public List<Document> getGenresStats(){
        UnwindOperation unwindGenres = Aggregation.unwind("genres");
        GroupOperation groupByGenres = Aggregation.group("genres")
        .count().as("count")
        .avg("runtime").as("averageRuntime")
        .push("name").as("titles");

        SortOperation sortById = Aggregation.sort(Sort.by(Direction.ASC, "_id"));

        Aggregation pipeline = Aggregation.newAggregation(unwindGenres, groupByGenres, sortById);

        return mongoTemplate.aggregate(pipeline, "tvshows", Document.class).getMappedResults();
    }

}
