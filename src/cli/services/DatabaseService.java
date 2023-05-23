package cli.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import cli.config.AppConfig;
import fh.FinnaHaku;

/**
 * Functions for database inputs
 * @author Jansromi
 *
 */
public class DatabaseService {
	
	private static void writeInsertStatementToDb(String insertStatement) {
	    File out = new File("dbOutput.txt");
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(out, true))) {
	        writer.write(insertStatement);
	        writer.newLine();
	        writer.flush();
	        System.out.println("The INSERT statement has been appended to dbOutput.txt.");
	    } catch (IOException e) {
	        System.out.println("An error occurred while writing to dbOutput.txt: " + e.getMessage());
	    }
	}
	
	/**
	 * 
	 * @param author
	 * @param bookId
	 */
	public static void processAuthor(String author, int bookId) {
	    int authorId = FileService.getEntryIdFromFile(author, AppConfig.AUTHORS_FILE);
	    if (authorId != -1) {
	        writeInsertStatementToDb("INSERT INTO book_author (book_id, author_id) VALUES (" + bookId + ", " + authorId + ");");
	    } else {
	        int newAuthorId = FileService.generateNewEntryId(AppConfig.AUTHORS_FILE);
	        addAuthorToDatabase(newAuthorId, author);
	        writeInsertStatementToDb("INSERT INTO book_author (book_id, author_id) VALUES (" + bookId + ", " + newAuthorId + ");");
	    }
	}
	
	/**
	 * 
	 * @param topic
	 * @param bookId
	 */
	public static void processTopic(String topic, int bookId) {
	    int topicId = FileService.getEntryIdFromFile(topic, AppConfig.TOPICS_FILE);
	    if (topicId != -1) {
	        writeInsertStatementToDb("INSERT INTO book_topic (book_id, topic_id) VALUES (" + bookId + ", " + topicId + ");");
	    } else {
	        int newTopicId = FileService.generateNewEntryId(AppConfig.TOPICS_FILE);
	        addTopicToDatabase(newTopicId, topic);
	        writeInsertStatementToDb("INSERT INTO book_topic (book_id, author_id) VALUES (" + bookId + ", " + newTopicId + ");");
	    }
	}
	
	/**
	 * 
	 * @param genre
	 * @param bookId
	 */
	public static void processGenre(String genre, int bookId) {
		String genreId = FileService.getYKLIdFromFile(genre, AppConfig.GENRES_FILE);
	    if (genreId.equalsIgnoreCase("-1")) {
	    	String unknownGenreId = "-1";
	        writeInsertStatementToDb("INSERT INTO book_genre (book_id, genre_ykl_id) VALUES (" + bookId + ", " + unknownGenreId + ");");
	    } else {
	    	writeInsertStatementToDb("INSERT INTO book_genre (book_id, genre_ykl_id) VALUES (" + bookId + ", " + genreId + ");");
	    }
	}
	
	/**
	 * 
	 * @param publisher
	 * @param bookId
	 * @return
	 */
	public static int processPublisher(String publisher, int bookId) {
		int publisherId = FileService.getEntryIdFromFile(publisher, AppConfig.PUBLISHERS_FILE);
	    if (publisherId == -1) {
	    	publisherId = FileService.generateNewEntryId(AppConfig.PUBLISHERS_FILE);
	        addPublisherToDatabase(publisherId, publisher);
	        writeInsertStatementToDb("INSERT INTO book_publisher (book_id, publisher_id) VALUES (" + bookId + ", " + publisherId + ");");
	    } 
	    return publisherId;
	}
	
	public static void processBook(FinnaHaku fh, int bookId, int pub) {
	    String statement = "INSERT INTO book (id, title, publisher_id, isbn, release_year) VALUES (" + bookId + ", " + 
	    "'" + fh.getBookTitle() + "', " + 
	    pub + ", " +
	    "'" + fh.getIsbn() + "', " +
	    fh.getPublicationDates() + 
	    ");";

	    writeInsertStatementToDb(statement);
	}
	
	// MEDIATORS
	private static void addAuthorToDatabase(int authorId, String author) {
		FileService.addEntryToFile(authorId, author, AppConfig.AUTHORS_FILE);
	    writeInsertStatementToDb("INSERT INTO author (id, author_name, dob, dod) VALUES (" + authorId + ", '" + author + "', NULL, NULL);");
	}
	
	private static void addTopicToDatabase(int topicId, String topic) {
		FileService.addEntryToFile(topicId, topic, AppConfig.TOPICS_FILE);
	    writeInsertStatementToDb("INSERT INTO topic (id, topic_desc) VALUES (" + topicId + ", '" + topic + "');");
	}

	private static void addPublisherToDatabase(int publisherId, String publisher) {
		FileService.addEntryToFile(publisherId, publisher, AppConfig.PUBLISHERS_FILE);
	    writeInsertStatementToDb("INSERT INTO publisher (id, pub_name) VALUES (" + publisherId + ", '" + publisher + "');");
	}
	
}