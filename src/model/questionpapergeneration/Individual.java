package model.questionpapergeneration;

import java.util.List;

import model.persisted.Question;

/**
 * Represents an individual question paper. The gene of an individual is a list of questions (i.e. each question is a
 * chromosome).
 *
 * @author Sam Barba
 */
public class Individual {

	private Question[] gene;

	private double fitness;

	public Individual(int geneSize) {
		this.gene = new Question[geneSize];
		this.fitness = 0;
	}

	public Question[] getGene() {
		return gene;
	}

	public void copyGene(Question[] gene) {
		for (int i = 0; i < gene.length; i++) {
			this.gene[i] = gene[i];
		}
	}

	public double calculateFitness() {
		/*
		 * magic
		 */
		return fitness;
	}

	/**
	 * Calculate standard deviation for a list of values (e.g. skill level or no. marks) which will help determine paper
	 * fitness. The higher the standard deviation, the better, because we want a good range of questions.
	 * 
	 * @param values - the list of values
	 * @return the standard deviation of the values
	 */
	private double standardDeviation(List<Double> values) {
		double mean = values.stream().reduce(0d, Double::sum) / values.size();
		double sumSquares = values.stream().map(v -> v * v).reduce(0d, Double::sum);

		return 0;
	}
}
