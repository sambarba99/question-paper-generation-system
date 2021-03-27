package model.questionpapergeneration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import model.persisted.Question;

/**
 * This class is a singleton, the use of which is to perform evolutionary methods such as selection and mutation, when
 * generating question papers.
 *
 * @author Sam Barba
 */
public class GAUtils {

	private static GAUtils instance;

	private static final Random RAND = new Random();

	/**
	 * Determine the optimal number of questions (genes) in a question paper, given list of possible questions to use,
	 * and user-defined paper parameters.
	 * 
	 * @param questions            - list of possible questions to include in paper
	 * @param userSelectedSkillLvl - the user-selected (mean) skill level of the final paper
	 * @param userSelectedTimeReq  - the user-selected approx. time required (mins) for the final paper
	 * @return calculated number of questions
	 */
	public int calculateChromosomeSize(List<Question> questions, int userSelectedSkillLvl, int userSelectedTimeReq) {
		double meanTimeRequired = questions.stream()
			.filter(q -> q.getSkillLevel().getIntVal() == userSelectedSkillLvl)
			.mapToDouble(Question::getTimeRequiredMins)
			.average()
			.getAsDouble();

		/*
		 * Determine the number of questions (genes) to have, given the mean time per question of the user-selected
		 * skill level
		 */
		double numQsGivenTimeReq = (double) userSelectedTimeReq / meanTimeRequired;

		int numQsRounded = (int) Math.round(numQsGivenTimeReq);
		if (numQsRounded > questions.size()) {
			numQsRounded -= (questions.size() - numQsRounded);
		}

		return numQsRounded;
	}

	/**
	 * Initialise an array of individuals, which can be used to represent the population or offspring.
	 * 
	 * @param popSize              - the population size
	 * @param userSelectedSkillLvl - the user-selected (mean) skill level of the paper
	 * @param userSelectedTimeReq  - the user-selected time required (mins) for the paper
	 * @return the array of individuals
	 */
	public Individual[] initialiseIndividualArray(int popSize, int userSelectedSkillLvl, int userSelectedTimeReq) {
		Individual[] individuals = new Individual[popSize];
		for (int i = 0; i < popSize; i++) {
			individuals[i] = new Individual(userSelectedSkillLvl, userSelectedTimeReq);
		}
		return individuals;
	}

	/**
	 * Randomise the genes of the individuals in a population.
	 * 
	 * @param population - the array of individuals whose genes will be randomised
	 * @param numGenes   - the number of questions to use (genes per chromosome)
	 * @param questions  - list of questions to use when selecting random genes
	 */
	public void randomisePopulationGenes(Individual[] population, int numGenes, List<Question> questions) {
		for (int i = 0; i < population.length; i++) {
			List<Question> questionsCopy = new ArrayList<>(questions);
			for (int j = 0; j < numGenes; j++) {
				int idx = RAND.nextInt(questionsCopy.size());
				population[i].getGenes().add(questionsCopy.get(idx));
				questionsCopy.remove(idx); // avoid repeated questions in an Individual's chromosome
			}
		}
	}

	/**
	 * Perform selection either to generate the offspring, or the next population.
	 * 
	 * @param population       - the array of individuals in the current population
	 * @param offspring        - the array of individuals in the current offspring set
	 * @param selectionType    - the type of selection to use, i.e. tournament or roulette wheel
	 * @param tournamentSize   - number of individuals in a tournament (if this selection is used), the fittest of which
	 *                         will be selected to copy genes from
	 * @param initialSelection - whether or not this is the first selection process (determines if either the offspring
	 *                         set is generated, or the next population)
	 */
	public void selection(Individual[] population, Individual[] offspring, SelectionType selectionType,
		int tournamentSize, boolean initialSelection) {

		int popSize = population.length;

		switch (selectionType) {
			case ROULETTE_WHEEL:
				// populate roulette wheel based on each individual's fitness
				List<Individual> rouletteWheel = new ArrayList<>();

				for (int i = 0; i < popSize; i++) {
					int numTimesToAdd = initialSelection
						? (int) Math.abs(Math.round(population[i].calculateFitness() * 100))
						: (int) Math.abs(Math.round(offspring[i].calculateFitness() * 100));

					// the fitter the individual, the more it gets added, so the higher the chance of selection
					for (int n = 0; n < numTimesToAdd; n++) {
						if (initialSelection) {
							rouletteWheel.add(population[i]);
						} else {
							rouletteWheel.add(offspring[i]);
						}
					}
				}

				// select random individuals from wheel
				for (int i = 0; i < popSize; i++) {
					Individual randIndividual = rouletteWheel.get(RAND.nextInt(rouletteWheel.size()));
					if (initialSelection) {
						offspring[i].setGenes(randIndividual.getGenes());
					} else {
						population[i].setGenes(randIndividual.getGenes());
					}
				}
				break;
			case TOURNAMENT:
				for (int i = 0; i < popSize; i++) {
					List<Individual> tournamentIndividuals = new ArrayList<>();
					for (int n = 0; n < tournamentSize; n++) {
						if (initialSelection) {
							tournamentIndividuals.add(population[RAND.nextInt(popSize)]);
						} else {
							tournamentIndividuals.add(offspring[RAND.nextInt(popSize)]);
						}
					}

					Individual tournamentFittest = tournamentIndividuals.stream()
						.max(Comparator.comparing(Individual::calculateFitness))
						.get();

					if (initialSelection) {
						offspring[i].setGenes(tournamentFittest.getGenes());
					} else {
						population[i].setGenes(tournamentFittest.getGenes());
					}
				}
				break;
			default:
				return;
		}
	}

	/**
	 * Perform crossover on the offspring set at random, depending on the crossover rate.
	 * 
	 * @param offspring            - the array representing the offspring set
	 * @param crossoverRate        - the crossover rate, ranging from 0 to 1 (inclusive)
	 * @param userSelectedSkillLvl - the user-selected (mean) skill level of the paper
	 * @param userSelectedTimeReq  - the user-selected time required (mins) for the paper
	 */
	public void crossover(Individual[] offspring, double crossoverRate, int userSelectedSkillLvl,
		int userSelectedTimeReq) {

		for (int i = 0; i < offspring.length; i += 2) {
			if (RAND.nextDouble() < crossoverRate && i < offspring.length - 1) {
				/*
				 * In each iteration, 2 possible offspring are found by calling recombineGenesToMakeOffspring twice, but
				 * switching the parents around. The fittest of the 2 is then kept.
				 */
				Individual newOffspring1 = recombineGenes(offspring[i], offspring[i + 1], userSelectedSkillLvl,
					userSelectedTimeReq);

				Individual newOffspring2 = recombineGenes(offspring[i + 1], offspring[i], userSelectedSkillLvl,
					userSelectedTimeReq);

				// replace with fittest of the 2 new offspring, only if fitter than current offspring
				Individual fittestOf2 = findFittest(new Individual[] { newOffspring1, newOffspring2 });

				if (fittestOf2.calculateFitness() > offspring[i].calculateFitness()) {
					offspring[i].setGenes(fittestOf2.getGenes());
				}
			}
		}
	}

	/**
	 * Perform a modified uniform crossover on parents p1 and p2 (modified because: repeated questions in the offspring
	 * must be avoided; and because the selection of genes from the fitter parent is biased, in order to ensure more
	 * selection from their genotype).
	 * 
	 * @param p1                   - the first parent
	 * @param p2                   - the second parent
	 * @param userSelectedSkillLvl - the user-selected (mean) skill level of the paper
	 * @param userSelectedTimeReq  - the user-selected time required (mins) for the paper
	 * @return a uniform crossover-generated offspring
	 */
	private Individual recombineGenes(Individual p1, Individual p2, int userSelectedSkillLvl, int userSelectedTimeReq) {
		double p1fit = p1.calculateFitness();
		double p2fit = p2.calculateFitness();

		/*
		 * Add bias to selection of p1's genes: if p1 is fitter, more genes will be selected from it; otherwise, more
		 * from p2.
		 */
		double probUseP1genes = p1fit / (p1fit + p2fit);

		Individual offspring = new Individual(userSelectedSkillLvl, userSelectedTimeReq);

		Question selectedGene;

		while (offspring.getGenes().size() < p1.getGenes().size()) {
			for (int i = 0; i < p1.getGenes().size(); i++) {
				if (RAND.nextDouble() < probUseP1genes) {
					selectedGene = p1.getGenes().get(i);
				} else {
					selectedGene = p2.getGenes().get(i);
				}

				// ensure no repeated questions
				if (!offspring.containsGene(selectedGene)) {
					offspring.getGenes().add(selectedGene);
				}
			}
		}

		return offspring;
	}

	/**
	 * Perform mutation on the offspring.
	 * 
	 * @param offspring    - the array representing the offspring set
	 * @param mutationRate - the mutation rate, ranging from 0 to 1 (inclusive)
	 * @param questions    - the set of questions to choose from, ensuring question isn't already in Individual
	 */
	public void mutation(Individual[] offspring, double mutationRate, List<Question> questions) {
		int numGenes = offspring[0].getGenes().size();

		for (int i = 0; i < offspring.length; i++) {
			List<Question> questionsCopy = new ArrayList<>(questions);

			for (int j = 0; j < numGenes; j++) {
				if (RAND.nextDouble() < mutationRate) {
					int questionIdx = RAND.nextInt(questionsCopy.size());
					Question randGene = questions.get(questionIdx);

					// ensure offspring to mutate doesn't already contain question
					while (offspring[i].containsGene(randGene)) {
						questionIdx = RAND.nextInt(questionsCopy.size());
						randGene = questions.get(questionIdx);
					}

					int idx = RAND.nextInt(numGenes);
					offspring[i].getGenes().set(idx, randGene);

					// remove to deny selection of the same question in future of inner for-loop
					questionsCopy.remove(questionIdx);
				}
			}
		}
	}

	/**
	 * Calculate the mean, highest, and lowest fitnesses to write to CSV file.
	 * 
	 * @param population - the current population of which to calculate the fitnesses
	 * @return list representing a CSV row, containing the mean, highest and lowest fitness of the generation
	 */
	public List<Double> getTableFitnesses(Individual[] population) {
		double sum = 0;
		double highest = population[0].calculateFitness();
		double lowest = highest;

		for (int i = 0; i < population.length; i++) {
			double f = population[i].calculateFitness();
			sum += f;
			if (f > highest) {
				highest = f;
			}
			if (f < lowest) {
				lowest = f;
			}
		}

		double mean = sum / population.length;

		return Arrays.asList(mean, highest, lowest);
	}

	/**
	 * Find the fittest individual (question paper) of a population.
	 * 
	 * @param population - the population to traverse
	 * @return the individual representing the best question paper
	 */
	public Individual findFittest(Individual[] population) {
		return Arrays.stream(population).max(Comparator.comparing(Individual::calculateFitness)).get();
	}

	public synchronized static GAUtils getInstance() {
		if (instance == null) {
			instance = new GAUtils();
		}
		return instance;
	}

	private GAUtils() {
	}
}
