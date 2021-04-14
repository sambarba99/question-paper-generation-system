package model.persisted;

import java.time.LocalDateTime;

/**
 * Represents a subject.
 *
 * @author Sam Barba
 */
public class Subject {

	private int id;

	private String title;

	private LocalDateTime dateCreated;

	public Subject() {
	}

	public Subject(int id, String title, LocalDateTime dateCreated) {
		this.id = id;
		this.title = title;
		this.dateCreated = dateCreated;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Override
	public String toString() {
		return title + " (ID " + id + ")";
	}
}
