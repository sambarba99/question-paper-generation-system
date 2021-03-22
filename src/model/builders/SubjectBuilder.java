package model.builders;

import java.time.LocalDateTime;

import model.persisted.Subject;

/**
 * This class utilises the builder pattern, and is used to build persisted Subject objects.
 *
 * @author Sam Barba
 */
public class SubjectBuilder {

	private int id;

	private String title;

	private LocalDateTime dateCreated;

	public SubjectBuilder() {
	}

	public SubjectBuilder withId(int id) {
		this.id = id;
		return this;
	}

	public SubjectBuilder withTitle(String title) {
		this.title = title;
		return this;
	}

	public SubjectBuilder withDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Subject build() {
		return new Subject(id, title, dateCreated);
	}
}
