package model.questionpapergeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import model.persisted.Question;

/**
 * Represents an individual question paper. The chromosome of an individual is a list of questions (i.e. each question
 * is a gene). Fitness is calculated by comparing the chromosome to: the user-selected skill level of the paper; and the
 * selected minutes required for the paper - all done utilising a statistical method (see calculateFitness).
 *
 * @author Sam Barba
 */
public class Individual {

	private List<Question> genes;

	private int paperSkillLvl;

	private int paperMinsRequired;

	public Individual(int paperSkillLvl, int paperMinsRequired) {
		this.paperSkillLvl = paperSkillLvl;
		this.paperMinsRequired = paperMinsRequired;
		this.genes = new ArrayList<>();
	}

	public List<Question> getGenes() {
		return genes;
	}

	public void setGenes(List<Question> genes) {
		this.genes.clear();
		this.genes.addAll(genes);
	}

	/**
	 * Check if chromosome contains a question (gene).
	 * 
	 * @param gene - the question to check
	 * @return whether or not the question exists in the gene
	 */
	public boolean containsGene(Question gene) {
		return genes.stream().anyMatch(q -> q.getId() == gene.getId());
	}

	/**
	 * Calculate the fitness of an individual paper.
	 * 
	 * @return the fitness of the individual
	 */
	public double calculateFitness() {
		List<Integer> skillLvls = genes.stream().map(q -> q.getSkillLevel().getIntVal()).collect(Collectors.toList());
		List<Integer> minsRequired = genes.stream().map(Question::getMinutesRequired).collect(Collectors.toList());

		double meanSkillLvl = skillLvls.stream().mapToDouble(s -> s).average().getAsDouble();
		double totalMinsRequired = minsRequired.stream().mapToDouble(t -> t).reduce(0, Double::sum);

		// calculate standard deviations for each attribute
		double stDevSkillLvls = standardDeviation(skillLvls);
		double stDevMinsRequied = standardDeviation(minsRequired);

		// calculate distance between user-selected values and generated values
		double skillLvlDist = Math.abs(paperSkillLvl - meanSkillLvl);
		double minsRequiredDist = Math.abs(paperMinsRequired - totalMinsRequired);

		/*
		 * 1. The higher the standard deviations calculated above, the better, because a good range is needed of
		 * easier-to-harder questions.
		 * 
		 * 2. The closer the mean skill level to the user-selected skill level, the better. Same with total minutes
		 * required. I.e., the smaller the calculated distances above (skillLvlDist and minsRequiredDist), the better.
		 * 
		 * Hence, the fitness can be calculated as follows:
		 */
		return stDevSkillLvls + stDevMinsRequied - skillLvlDist - minsRequiredDist;
	}

	/**
	 * Calculate standard deviation for a list of values (e.g. list of skill levels) which will help determine paper
	 * fitness. It is the square root of the variance (which itself is the mean of the squared differences).
	 * 
	 * @param values - the list of values
	 * @return the standard deviation of the values
	 */
	private double standardDeviation(List<Integer> values) {
		double variance = 0;
		double mean = values.stream().mapToDouble(v -> v).average().getAsDouble();

		for (Integer v : values) {
			variance += (v - mean) * (v - mean);
		}
		variance /= values.size();

		return Math.sqrt(variance);
	}

	/**
	 * Find if the chromosome contains all possible questions (genes).
	 * 
	 * @param questions - the set of questions to check
	 * @return if all questions in the set are also in the chromosome
	 */
	public boolean containsAllPossibleGenes(List<Question> questions) {
		List<Integer> geneIds = genes.stream().map(Question::getId).collect(Collectors.toList());
		List<Integer> questionIds = questions.stream().map(Question::getId).collect(Collectors.toList());
		return geneIds.containsAll(questionIds);
	}
}
