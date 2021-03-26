package model.questionpapergeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import model.persisted.Question;

/**
 * Represents an individual question paper. The chromosome of an individual is a list of questions (i.e. each question
 * is a gene).
 *
 * @author Sam Barba
 */
public class Individual {

	private List<Question> genes;

	private double fitness;

	private int userSelectedSkillLvl;

	private int userSelectedMarks;

	private int userSelectedTimeReq;

	public Individual(int userSelectedSkillLvl, int userSelectedMarks, int userSelectedTimeReq) {
		this.userSelectedSkillLvl = userSelectedSkillLvl;
		this.userSelectedMarks = userSelectedMarks;
		this.userSelectedTimeReq = userSelectedTimeReq;
		this.genes = new ArrayList<>();
		this.fitness = 0;
	}

	public List<Question> getGenes() {
		return genes;
	}

	public void copyGenes(List<Question> genes) {
		this.genes = new ArrayList<>(genes); // shallow copy
	}

	public double getFitness() {
		return fitness;
	}

	/**
	 * Calculate the fitness of an individual paper.
	 */
	public void calculateFitness() {
		List<Integer> skillLvlValues = genes.stream()
			.map(q -> q.getSkillLevel().getIntVal())
			.collect(Collectors.toList());
		List<Integer> markValues = genes.stream().map(Question::getMarks).collect(Collectors.toList());
		List<Integer> timesReqValues = genes.stream().map(Question::getTimeRequiredMins).collect(Collectors.toList());

		int meanSkillLvl = (int) Math.round(skillLvlValues.stream().mapToDouble(s -> s).average().getAsDouble());
		int totalMarks = markValues.stream().mapToInt(m -> m).reduce(0, Integer::sum);
		int totalTimeReq = timesReqValues.stream().mapToInt(t -> t).reduce(0, Integer::sum);

		// calculate differences between user-selected values and generated values
		int skillLvlDiff = Math.abs(userSelectedSkillLvl - meanSkillLvl);
		int marksDiff = Math.abs(userSelectedMarks - totalMarks);
		int timeReqDiff = Math.abs(userSelectedTimeReq - totalTimeReq);

		// calculate standard deviations for each attribute
		double stDevSkill = standardDeviation(skillLvlValues);
		double stDevMarks = standardDeviation(markValues);
		double stDevTime = standardDeviation(timesReqValues);

		double marksSkew = skewnessCoefficient(markValues);

		/*
		 * 1. The closer the mean skill level to the user-selected skill level, the better. Same with total marks and
		 * total time required. I.e., the smaller the calculated differences above, the better.
		 * 
		 * 2. The higher the standard deviations calculated above, the better, because we want a good range of
		 * easy-to-difficult questions.
		 * 
		 * 3. The smaller the 'skewness' of the marks, the better, as this represents symmetry in the distribution of
		 * questions in the paper.
		 * 
		 * Hence, the fitness can be calculated as follows:
		 */
		fitness = stDevMarks + stDevTime + stDevSkill - skillLvlDiff - marksDiff - timeReqDiff - marksSkew;
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
		double sumSquares = values.stream().mapToDouble(v -> v * v).reduce(0, Double::sum);

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
