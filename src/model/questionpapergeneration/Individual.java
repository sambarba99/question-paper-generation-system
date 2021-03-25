package model.questionpapergeneration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import model.persisted.Question;

/**
 * Represents an individual question paper. The gene of an individual is a list of questions (i.e. each question is a
 * chromosome).
 *
 * @author Sam Barba
 */
public class Individual {

	private List<Question> gene;

	private double fitness;

	public Individual() {
		this.gene = new ArrayList<>();
		this.fitness = 0;
	}

	public List<Question> getGene() {
		return gene;
	}

	public void copyGene(List<Question> gene) {
		this.gene = new ArrayList<>(gene);
	}

	/**
	 * Calculate the fitness of an individual paper.
	 * 
	 * @return fitness of the question paper individual
	 */
	public double calculateFitness() {
		List<Question> questions = new ArrayList<>(gene);
		/*
		 * Sort in ascending order of marks, in order to calculate the skewness and 'increase coefficients'.
		 */
		questions.sort(Comparator.comparing(Question::getMarks));

		List<Integer> marks = questions.stream().map(Question::getMarks).collect(Collectors.toList());
		List<Integer> timesRequired = questions.stream()
			.map(Question::getTimeRequiredMins)
			.collect(Collectors.toList());
		List<Integer> skillLvls = questions.stream()
			.map(q -> q.getSkillLevel().getIntVal())
			.collect(Collectors.toList());

		double marksSkew = skewnessCoefficient(marks);

		double stDevMarks = standardDeviation(marks);
		double stDevTime = standardDeviation(timesRequired);
		double stDevSkill = standardDeviation(skillLvls);

		int increaseMarks = increaseCoefficient(marks);
		int increaseTimeReq = increaseCoefficient(timesRequired);
		int increaseSkillLvl = increaseCoefficient(skillLvls);

		/*
		 * The closer the skewness of the marks is to 0, the better, as this represents symmetry in the distribution of
		 * questions in the paper.
		 * 
		 * The higher the standard deviations calculated above, the better, because we want a good range of
		 * easy-to-difficult questions.
		 * 
		 * The higher the increase coefficient (above) of these values, the better, because we want the questions to get
		 * harder through the paper.
		 * 
		 * Hence, the fitness can be calculated as follows:
		 */
		fitness = stDevMarks + stDevTime + stDevSkill + increaseMarks + increaseTimeReq + increaseSkillLvl - marksSkew;

		return fitness;
	}

	/**
	 * Calculate standard deviation for a list of values (e.g. skill level or no. marks) which will help determine paper
	 * fitness.
	 * 
	 * @param values - the list of values
	 * @return the standard deviation of the values
	 */
	private double standardDeviation(List<Integer> values) {
		double mean = values.stream().mapToDouble(v -> v).average().getAsDouble();
		double sumSquares = values.stream().map(v -> v * v).reduce(0, Integer::sum);

		double stDevSquared = sumSquares / values.size() - (mean * mean);

		return Math.sqrt(stDevSquared);
	}

	/**
	 * Find the coefficient of skewness of a list of values, being: (mean - mode) / (standard deviation of values).
	 * 
	 * @param values - the list of values
	 * @return the skewness coefficient
	 */
	private double skewnessCoefficient(List<Integer> values) {
		double mean = values.stream().mapToDouble(v -> v).average().getAsDouble();
		double mode = mode(values);
		double stDev = standardDeviation(values);

		return Math.abs((mean - mode) / stDev);
	}

	/**
	 * Find how much the values in a list generally increase. A high result means the list generally increases; opposite
	 * for a low result.
	 * 
	 * @param values - the list to examine
	 * @return number representing how much the values generally increase
	 */
	private int increaseCoefficient(List<Integer> values) {
		if (values.isEmpty() || values.size() == 1) {
			return 0;
		}

		int count = 0;
		for (int i = 0; i < values.size() - 2; i++) {
			if (values.get(i + 1) > values.get(i)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Find the mode of a list of values.
	 * 
	 * @param values - the list to examine
	 * @return mode - the mode of the list
	 */
	private int mode(List<Integer> values) {
		int max = values.stream().mapToInt(v -> v).max().getAsInt();
		int[] count = new int[max + 1];

		for (Integer v : values) {
			count[v]++;
		}

		int mode = 0;
		int modeCount = count[0];
		for (int i = 1; i < count.length; i++) {
			if (count[i] > modeCount) {
				modeCount = count[i];
				mode = i;
			}
		}

		return mode;
	}
}
