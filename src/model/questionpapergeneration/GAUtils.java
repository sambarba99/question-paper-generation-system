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
	 * Initialise an array of individuals, which can be used to represent the population or offspring.
	 * 
	 * @param popSize  - the population size
	 * @param geneSize - the number of chromosomes in each individual's gene
	 * @return the array of individuals
	 */
	public Individual[] initialiseIndividualArray(int popSize, int geneSize) {
		Individual[] individuals = new Individual[popSize];
		for (int i = 0; i < popSize; i++) {
			individuals[i] = new Individual();
		}
		return individuals;
	}

	/**
	 * Randomise the genes of the individuals in a population.
	 * 
	 * @param population - the array of individuals whose genes will be randomised
	 * @param geneSize   - the number of questions to use (chromosomes per gene)
	 * @param questions  - list of questions to use when selecting random chromosomes
	 */
	public void randomisePopulationGenes(Individual[] population, int geneSize, List<Question> questions) {
		for (int i = 0; i < population.length; i++) {
			List<Question> questionsCopy = new ArrayList<>(questions);
			for (int j = 0; j < geneSize; j++) {
				int idx = RAND.nextInt(questionsCopy.size());
				population[i].getGene().set(j, questionsCopy.get(idx));
				questionsCopy.remove(idx); // avoid repeated questions in an Individual's gene
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
	 *                         will be selected to copy chromosomes from
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
					numTimesToAdd = initialSelection
						? (int) Math.abs(Math.floor(population[i].calculateFitness() * 100))
						: (int) Math.abs(Math.floor(offspring[i].calculateFitness() * 100));

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
						offspring[i].copyGene(randIndividual.getGene());
					} else {
						population[i].copyGene(randIndividual.getGene());
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
						offspring[i].copyGene(tournamentFittest.getGene());
					} else {
						population[i].copyGene(tournamentFittest.getGene());
					}
				}
				break;
			default:
				return;
		}
	}

	/**
	 * Perform k-point crossover on the offspring.
	 * 
	 * @param population    - the array representing the current population
	 * @param offspring     - the array representing the offspring set
	 * @param geneSize      - the number of questions to use (chromosomes per gene)
	 * @param crossoverRate - the crossover rate, ranging from 0 to 1 (inclusive)
	 * @param points        - the number of random crossover points (i.e. k)
	 */
	public void crossover(Individual[] population, Individual[] offspring, int geneSize, double crossoverRate) {
		for (int i = 0; i < population.length; i += 2) {
			if (RAND.nextDouble() < crossoverRate) {
				Individual temp = offspring[i];
				// TODO
			}
		}
	}

	/**
	 * Perform mutation on offspring.
	 * 
	 * @param offspring    - the array representing the offspring set
	 * @param geneSize     - the number of questions to use (chromosomes per gene)
	 * @param mutationRate - the mutation rate, ranging from 0 to 1 (inclusive)
	 */
	public void mutation(Individual[] offspring, int geneSize, double mutationRate) {
		for (int i = 0; i < offspring.length; i++) {
			for (int j = 0; j < geneSize; j++) {
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
	public List<Double> calculateTableFitnesses(Individual[] population) {
		double mean = 0, sum = 0;
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
