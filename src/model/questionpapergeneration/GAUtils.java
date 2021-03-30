package model.questionpapergeneration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import model.persisted.Question;

import view.utils.Constants;

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
	 * @param questions         - list of possible questions to include in paper
	 * @param paperSkillLvl     - the user-selected (mean) skill level of the final paper
	 * @param paperMinsRequired - the user-selected approx. duration (mins) for the final paper
	 * @return calculated number of questions
	 */
	public int calculateChromosomeLength(List<Question> questions, int paperSkillLvl, int paperMinsRequired) {
		double meanMinsRequired = questions.stream()
			.filter(q -> q.getSkillLevel().getIntVal() == paperSkillLvl)
			.mapToDouble(Question::getMinutesRequired)
			.average()
			.getAsDouble();

		if ((int) meanMinsRequired == 0) {
			/*
			 * If no questions exist of the user-selected paper skill level, then take the mean across ALL existing
			 * skill levels.
			 */
			meanMinsRequired = questions.stream().mapToDouble(Question::getMinutesRequired).average().getAsDouble();
		}

		/*
		 * Determine the number of questions (genes) to have, given the mean minutes per question.
		 */
		int numQuestions = (int) Math.floor((double) paperMinsRequired / meanMinsRequired);

		return numQuestions > questions.size() ? questions.size() : numQuestions;
	}

	/**
	 * Initialise an array of individuals, which can be used to represent the population or offspring.
	 * 
	 * @param paperSkillLvl     - the user-selected (mean) skill level of the paper
	 * @param paperMinsRequired - the user-selected minutes required for the paper
	 * @return the array of individuals
	 */
	public Individual[] initialiseIndividualArray(int paperSkillLvl, int paperMinsRequired) {
		Individual[] individuals = new Individual[Constants.POP_SIZE];
		for (int i = 0; i < Constants.POP_SIZE; i++) {
			individuals[i] = new Individual(paperSkillLvl, paperMinsRequired);
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
		List<Question> questionsCopy = new ArrayList<>();

		for (Individual individual : population) {
			questionsCopy.clear();
			questionsCopy.addAll(questions);

			for (int j = 0; j < numGenes; j++) {
				// question is deleted after use, to avoid repeated genes in a chromosome
				Question randGene = questionsCopy.remove(RAND.nextInt(questionsCopy.size()));
				individual.getGenes().add(randGene);
			}
		}
	}

	/**
	 * Perform selection either to generate the offspring, or the next population.
	 * 
	 * @param population - the array of individuals in the current population
	 * @param offspring  - the array of individuals in the current offspring set
	 */
	public void selection(Individual[] population, Individual[] offspring) {
		switch (Constants.SELECTION_TYPE) {
			case ROULETTE_WHEEL:
				// populate roulette wheel based on each individual's fitness
				List<Individual> rouletteWheel = new ArrayList<>();

				for (int i = 0; i < Constants.POP_SIZE; i++) {
					int numTimesToAdd = (int) Math.abs(Math.round(population[i].calculateFitness() * 100));

					// the fitter the individual, the more it gets added, so the higher the chance of selection
					for (int n = 0; n < numTimesToAdd; n++) {
						rouletteWheel.add(population[i]);
					}
				}

				// select random individuals from wheel
				for (int i = 0; i < Constants.POP_SIZE; i++) {
					Individual randIndividual = rouletteWheel.get(RAND.nextInt(rouletteWheel.size()));
					offspring[i].setGenes(randIndividual.getGenes());
				}
				break;
			default: // TOURNAMENT
				for (int i = 0; i < Constants.POP_SIZE; i++) {
					List<Individual> tournamentIndividuals = new ArrayList<>();

					for (int n = 0; n < Constants.TOURNAMENT_SIZE; n++) {
						tournamentIndividuals.add(population[RAND.nextInt(Constants.POP_SIZE)]);
					}

					Individual tournamentFittest = tournamentIndividuals.stream()
						.max(Comparator.comparing(Individual::calculateFitness))
						.get();

					offspring[i].setGenes(tournamentFittest.getGenes());
				}
		}
	}

	/**
	 * Perform crossover on pairs of individuals in the offspring set at random, depending on the crossover rate.
	 * 
	 * @param offspring         - the array representing the offspring set
	 * @param paperSkillLvl     - the user-selected (mean) skill level of the paper
	 * @param paperMinsRequired - the user-selected minutes required for the paper
	 */
	public void crossover(Individual[] offspring, int paperSkillLvl, int paperMinsRequired) {
		for (int i = 0; i < Constants.POP_SIZE; i += 2) {
			if (RAND.nextDouble() < Constants.CROSSOVER_RATE && i < Constants.POP_SIZE - 1) {
				/*
				 * In each iteration, 2 possible offspring are found by calling recombineGenesToMakeOffspring twice, but
				 * switching the parents around. The fittest of the 2 is then kept.
				 */
				Individual newOffspring1 = recombineGenes(offspring[i], offspring[i + 1], paperSkillLvl,
					paperMinsRequired);

				Individual newOffspring2 = recombineGenes(offspring[i + 1], offspring[i], paperSkillLvl,
					paperMinsRequired);

				// replace with fittest of the 2 new offspring, only if fitter than current offspring
				Individual fittestOffspring = findFittest(new Individual[] { newOffspring1, newOffspring2 });

				if (fittestOffspring.calculateFitness() > offspring[i].calculateFitness()) {
					offspring[i].setGenes(fittestOffspring.getGenes());
				}
			}
		}
	}

	/**
	 * Perform a modified uniform crossover on parents p1 and p2 (modified because: repeated questions in the offspring
	 * must try to be avoided; and because the selection of genes from the fitter parent is biased, in order to ensure
	 * more selection from their genotype).
	 * 
	 * @param p1                - the first parent
	 * @param p2                - the second parent
	 * @param paperSkillLvl     - the user-selected (mean) skill level of the paper
	 * @param paperMinsRequired - the user-selected minutes required for the paper
	 * @return a uniform crossover-generated offspring
	 */
	private Individual recombineGenes(Individual p1, Individual p2, int paperSkillLvl, int paperMinsRequired) {
		/*
		 * The higher the selection bias for parent 1, the more genes from them to add to the offspring's chromosome.
		 */
		double probChooseP1 = calculateP1selectionBias(p1, p2);
		int numGenesFromP1 = (int) Math.round(probChooseP1 * p1.getGenes().size());

		Map<Integer, Question> p1genes = new HashMap<>();
		Map<Integer, Question> p2genes = new HashMap<>();

		for (int i = 0; i < numGenesFromP1; i++) {
			Question p1gene = p1.getGenes().get(i);
			p1genes.put(p1gene.getId(), p1gene);
		}

		for (Question p2gene : p2.getGenes()) {
			if (p1genes.size() + p2genes.size() < p1.getGenes().size() && !p1genes.containsKey(p2gene.getId())) {
				p2genes.put(p2gene.getId(), p2gene);
			}
		}

		Individual offspring = new Individual(paperSkillLvl, paperMinsRequired);

		offspring.getGenes().addAll(p1genes.values());
		offspring.getGenes().addAll(p2genes.values());

		return offspring;
	}

	/**
	 * Calculate probability of selecting p1 instead of p2 to use in recombination. The fitter p1, the more likely it is
	 * to be chosen; likewise for p2.
	 * 
	 * E.g. p1fit = -7, p2fit = -12
	 * 
	 * probChooseP1 = 1 - (1 / (-7 - -12)) = 0.8
	 * 
	 * E.g. p1fit = -20, p2fit = -15
	 * 
	 * probChooseP1 = 1 / (-15 - -20) = 0.2
	 */
	private double calculateP1selectionBias(Individual p1, Individual p2) {
		double p1fit = p1.calculateFitness();
		double p2fit = p2.calculateFitness();

		double probChooseP1;

		if (p1fit > p2fit) {
			probChooseP1 = 1 - (1 / (p1fit - p2fit));
		} else if (p1fit < p2fit) {
			probChooseP1 = 1 / (p2fit - p1fit);
		} else { // same fitness
			return 0.5;
		}

		if (probChooseP1 > 1 || probChooseP1 < 0) {
			// means that difference in fitnesses is very small, so can return 0.5
			return 0.5;
		} else {
			return probChooseP1;
		}
	}

	/**
	 * Perform mutation on the offspring.
	 * 
	 * @param offspring - the array representing the offspring set
	 * @param questions - the set of questions to choose from, ensuring question isn't already in Individual
	 */
	public void mutation(Individual[] offspring, List<Question> questions) {
		int numGenes = offspring[0].getGenes().size();

		for (int i = 0; i < Constants.POP_SIZE; i++) {
			if (offspring[i].containsAllPossibleGenes(questions)) {
				/*
				 * Cannot mutate because there would be a duplicate gene, so move on to next offspring
				 */
				continue;
			}

			List<Question> questionsCopy = new ArrayList<>();
			questionsCopy.addAll(questions);

			for (int j = 0; j < numGenes; j++) {
				if (RAND.nextDouble() < Constants.MUTATION_RATE && !offspring[i].containsAllPossibleGenes(questions)) {
					/*
					 * Ensure offspring to mutate doesn't already contain gene, so remove random question instead of
					 * using questionsCopy.get
					 */
					Question randGene = questionsCopy.remove(RAND.nextInt(questionsCopy.size()));

					while (offspring[i].containsGene(randGene)) {
						randGene = questionsCopy.remove(RAND.nextInt(questionsCopy.size()));
					}

					int idx = RAND.nextInt(numGenes);
					offspring[i].getGenes().set(idx, randGene);
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

		for (int i = 0; i < Constants.POP_SIZE; i++) {
			double f = population[i].calculateFitness();
			sum += f;
			if (f > highest) {
				highest = f;
			}
			if (f < lowest) {
				lowest = f;
			}
		}

		double mean = sum / Constants.POP_SIZE;

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
