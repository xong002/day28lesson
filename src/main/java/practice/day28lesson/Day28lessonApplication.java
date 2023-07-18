package practice.day28lesson;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import practice.day28lesson.repository.TVShowsRepository;

@SpringBootApplication
public class Day28lessonApplication implements CommandLineRunner{

	@Autowired
	private TVShowsRepository repo;

	public static void main(String[] args) {
		SpringApplication.run(Day28lessonApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		// List<String> allTypes = repo.getAllTypes();
		// System.out.println(allTypes.get(0));
		
		// List<Document> result = repo.findShowsByLanguage("eng");
		// result.stream().forEach(System.out::println);
		// for(Document d : result){
		// 	System.out.println(d);
		// }

		List<Document> result = repo.getGenresStats();
		result.stream().forEach(System.out::println);
		for(Document d : result){
			System.out.println(d);
		}
	}

}
