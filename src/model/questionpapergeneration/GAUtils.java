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
	 * Determine the optimal number of questions (genes) in a question paper, given list of possible questions to use.
	 * 
	 * @param questions - list of possible questions to include in paper
	 * @return calculated number of questions
	 */
	public int calculateChromosomeSize(List<Question> questions) {
		double totalMarks = questions.stream().mapToDouble(Question::getMarks).reduce(0, Double::sum);
		double totalTimeRequired = questions.stream().mapToDouble(Question::getTimeRequiredMins).reduce(0, Double::sum);
		double totalSkillLvl = questions.stream()
			.mapToDouble(q -> q.getSkillLevel().getIntVal())
			.reduce(0, Double::sum);

		double meanMarks = totalMarks / questions.size();
		double meanTimeReq = totalTimeRequired / questions.size();
		double meanSkillLvl = totalSkillLvl / questions.size();

		// this determines the number of questions to have, given the mean number of marks per question
		double numQsGivenAvMarks = totalMarks / meanMarks;
		// this determines the number of questions to have, given the mean time taken (mins) per question
		double numQsGivenAvTimeReq = totalTimeRequired / meanTimeReq;
		// this determines the number of questions to have, given the mean skill level per question
		double numQsGivenAvSkillLvl = totalSkillLvl / meanSkillLvl;

		// we need the geometric mean of the 3 calculated doubles, which is the cube root of their product
		return (int) Math.round(Math.cbrt(numQsGivenAvMarks * numQsGivenAvTimeReq * numQsGivenAvSkillLvl));
	}

	/**
	 * Initialise an array of individuals, which can be used to represent the population or offspring.
	 * 
	 * @param popSize              - the population size
	 * @param numGenes             - the number of genes in each individual's chromosome
	 * @param userSelectedSkillLvl - the user-selected (mean) skill level of the paper
	 * @param userSelectedTimeReq  - the user-selected time required (mins) for the paper
	 * @return the array of individuals
	 */
	public Individual[] initialiseIndividualArray(int popSize, int numGenes, int userSelectedSkillLvl,
		int userSelectedTimeReq) {

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
				population[i].getGenes().set(j, questionsCopy.get(idx));
				questionsCopy.remove(idx); // avoid repeated questions in an Individual's chromosome
			}
			population[i].calculateFitness();
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
				int numTimesToAdd;

				for (int i = 0; i < popSize; i++) {
					numTimesToAdd = initialSelection ? (int) Math.abs(Math.round(population[i].getFitness() * 100))
						: (int) Math.abs(Math.round(offspring[i].getFitness() * 100));

					// the fitter the individual, the more it gets added, so the higher the chance of getting picked
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
						offspring[i].copyGenes(randIndividual.getGenes());
					} else {
						population[i].copyGenes(randIndividual.getGenes());
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
						.max(Comparator.comparing(Individual::getFitness))
						.get();

					if (initialSelection) {
						offspring[i].copyGenes(tournamentFittest.getGenes());
					} else {
						population[i].copyGenes(tournamentFittest.getGenes());
					}
				}
				break;
			default:
				return;
		}
	}

	/**
	 * Perform single-point crossover on the offspring set.
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
				Individual newOffspring = crossoverToCreateOffspring(offspring[i], offspring[i + 1],
					userSelectedSkillLvl, userSelectedTimeReq);

				// replace only if fitter
				if (newOffspring.getFitness() > offspring[i].getFitness()) {
					offspring[i].copyGenes(newOffspring.getGenes());
					offspring[i].calculateFitness();
				}
			}
		}
	}

	/**
	 * Create the 2 possible offspring from 2 parents, and return the fittest one.
	 * 
	 * @param p1                   - the first parent
	 * @param p2                   - the second parent
	 * @param userSelectedSkillLvl - the user-selected (mean) skill level of the paper
	 * @param userSelectedTimeReq  - the user-selected time required (mins) for the paper
	 * @return the fittest offspring of the parents
	 */
	private Individual crossoverToCreateOffspring(Individual p1, Individual p2, int userSelectedSkillLvl,
		int userSelectedTimeReq) {

		int crossoverPoint = RAND.nextInt(p1.getGenes().size());

		// TODO

		Individual offspring1 = new Individual(userSelectedSkillLvl, userSelectedTimeReq);
		Individual offspring2 = new Individual(userSelectedSkillLvl, userSelectedTimeReq);

		offspring1.calculateFitness();
		offspring2.calculateFitness();
		return offspring1.getFitness() > offspring2.getFitness() ? offspring1 : offspring2;
	}

	/**
	 * Perform mutation on the offspring.
	 * 
	 * @param offspring    - the array representing the offspring set
	 * @param numGenes     - the number of questions to use (genes per chromosome)
	 * @param mutationRate - the mutation rate, ranging from 0 to 1 (inclusive)
	 * @param questions    - the set of questions to choose from, ensuring question isn't already in Individual
	 */
	public void mutation(Individual[] offspring, int numGenes, double mutationRate, List<Question> questions) {
		for (int i = 0; i < offspring.length; i++) {
			for (int j = 0; j < numGenes; j++) {
				if (RAND.nextDouble() < mutationRate) {
					// TODO
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
		double mean = 0, sum = 0;
		double highest = population[0].getFitness();
		double lowest = highest;

		for (int i = 0; i < population.length; i++) {
			double f = population[i].getFitness();
			sum += f;
			if (f > highest) {
				highest = f;
			}
			if (f < lowest) {
				lowest = f;
			}
		}
		mean = sum / population.length;
		return Arrays.asList(mean, highest, lowest);
	}

	/**
	 * Find the fittest individual (question paper) of a population.
	 * 
	 * @param population - the population to traverse
	 * @return the individual representing the best question paper
	 */
	public Individual findFittest(Individual[] population) {
		return Arrays.stream(population).max(Comparator.comparing(Individual::getFitness)).get();
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
