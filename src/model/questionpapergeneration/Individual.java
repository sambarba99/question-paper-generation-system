package model.questionpapergeneration;

import model.persisted.Question;

/**
 * Represents an individual question paper. The gene of an individual is a list of questions (i.e. each question is a
 * chromosome).
 *
 * @author Sam Barba
 */
public class Individual {

	private Question[] genes;

	private double fitness;

	public Individual(int numGenes) {
		this.genes = new Question[numGenes];
		this.fitness = 0;
	}

	public Question[] getGenes() {
		return genes;
	}

	public void copyGenes(Question[] genes) {
		for (int i = 0; i < genes.length; i++) {
			this.genes[i] = genes[i];
		}
	}

	public double calculateFitness() {
		/*
		 * magic
		 */
		return fitness;
	}
}
