package model.questionpapergeneration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
	 * @param numGenes - the number of genes in each individual
	 * @return the array of individuals
	 */
	public Individual[] initialiseIndividualArray(int popSize, int numGenes) {
		Individual[] individuals = new Individual[popSize];
		for (int i = 0; i < popSize; i++) {
			individuals[i] = new Individual(numGenes);
		}
		return individuals;
	}

	/**
	 * Randomise the genes of the individuals in a population.
	 * 
	 * @param population   - the array of individuals whose genes will be randomised
	 * @param allQuestions - list of all existing questions, to use when making genes
	 */
	public void randomisePopulationGenes(Individual[] population, List<Question> allQuestions) {
		int numGenes = population[0].getGenes().length;
		for (int i = 0; i < population.length; i++) {
			for (int j = 0; j < numGenes; j++) {
				/*
				 * ???
				 */
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
		int size = population.length;

		switch (selectionType) {
			case ROULETTE_WHEEL:
				// populate roulette wheel based on each individual's fitness
				List<Individual> rouletteWheel = new ArrayList<>();
				int numTimesToAdd;

				for (int i = 0; i < size; i++) {
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
				for (int i = 0; i < size; i++) {
					Individual randIndividual = rouletteWheel.get(RAND.nextInt(rouletteWheel.size()));
					if (initialSelection) {
						offspring[i].copyGenes(randIndividual.getGenes());
					} else {
						population[i].copyGenes(randIndividual.getGenes());
					}
				}
				break;
			case TOURNAMENT:
				for (int i = 0; i < size; i++) {
					List<Individual> tournamentIndividuals = new ArrayList<>();
					for (int n = 0; n < tournamentSize; n++) {
						if (initialSelection) {
							tournamentIndividuals.add(population[RAND.nextInt(size)]);
						} else {
							tournamentIndividuals.add(offspring[RAND.nextInt(size)]);
						}
					}

					Individual tournamentFittest = tournamentIndividuals.stream()
						.max(Comparator.comparing(Individual::calculateFitness))
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
	 * Perform k-point crossover on the offspring.
	 * 
	 * @param population    - the array representing the current population
	 * @param offspring     - the array representing the offspring set
	 * @param crossoverRate - the crossover rate, ranging from 0 to 1 (inclusive)
	 * @param points        - the number of random crossover points (i.e. k)
	 */
	public void crossover(Individual[] population, Individual[] offspring, double crossoverRate, int points) {
		int numGenes = population[0].getGenes().length;
		int swapStart, swapEnd;

		for (int i = 0; i < population.length; i += 2) {
			if (RAND.nextDouble() < crossoverRate) {
				List<Integer> crossoverPoints = getRandCrossPoints(points, numGenes);

				Individual temp = offspring[i];

				for (int c = 0; c < crossoverPoints.size() - 1; c++) {
					if (c % 2 == 0) {
						swapStart = crossoverPoints.get(c);
						swapEnd = crossoverPoints.get(c + 1);
						for (int j = swapStart; j < swapEnd; j++) {
							offspring[i].getGenes()[j] = offspring[i + 1].getGenes()[j];
							offspring[i + 1].getGenes()[j] = temp.getGenes()[j];
						}
					}
				}
			}
		}
	}

	/**
	 * Generate a list of random crossover points.
	 * 
	 * @param points   - the number of points to create
	 * @param numGenes - the number of genes per chromosome of an individual
	 * @return a list of random indices
	 */
	private List<Integer> getRandCrossPoints(int points, int numGenes) {
		List<Integer> possiblePoints = new ArrayList<>();
		for (int i = 1; i < numGenes; i++) {
			possiblePoints.add(i);
		}

		List<Integer> crossoverPoints = new ArrayList<>();
		for (int i = 0; i < points; i++) {
			int randIndex = RAND.nextInt(possiblePoints.size());
			int point = possiblePoints.remove(randIndex);
			crossoverPoints.add(point);
		}
		crossoverPoints.add(0);
		crossoverPoints.add(numGenes);
		Collections.sort(crossoverPoints);
		return crossoverPoints;
	}

	/**
	 * Perform mutation on offspring.
	 * 
	 * @param offspring    - the array representing the offspring set
	 * @param mutationRate - the mutation rate, ranging from 0 to 1 (inclusive)
	 */
	public void mutation(Individual[] offspring, double mutationRate) {
		int numGenes = offspring[0].getGenes().length;
		for (int i = 0; i < offspring.length; i++) {
			for (int j = 0; j < numGenes; j++) {
				if (RAND.nextDouble() < mutationRate) {
					/*
					 * ???
					 */
				}
			}
		}
	}

	/**
	 * Calculate the average, highest, and lowest fitnesses to write to CSV file.
	 * 
	 * @param population - the current population of which to calculate the fitnesses
	 * @return list representing a CSV row, containing the average, highest and lowest fitness of the generation
	 */
	public List<Double> calculateTableFitnesses(Individual[] population) {
		double average = 0, sum = 0;
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
		average = sum / population.length;
		return Arrays.asList(average, highest, lowest);
	}

	/**
	 * Find the fittest individual (question paper) of a population.
	 * 
	 * @param population - the population to examine
	 * @return the individual representing the best question paper
	 */
	public Individual findFittest(Individual[] population) {
		Individual fittest = population[0];
		double highestFitness = population[0].calculateFitness();

		for (int i = 0; i < population.length; i++) {
			double f = population[i].calculateFitness();
			if (f > highestFitness) {
				highestFitness = f;
				fittest = population[i];
			}
		}
		return fittest;
	}

	/**
	 * To assure only one and the same instance of this class every time.
	 * 
	 * @return instance of GAUtils
	 */
	public synchronized static GAUtils getInstance() {
		if (instance == null) {
			instance = new GAUtils();
		}
		return instance;
	}

	private GAUtils() {
	}
}
