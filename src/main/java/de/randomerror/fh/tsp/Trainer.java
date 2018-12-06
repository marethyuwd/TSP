package de.randomerror.fh.tsp;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Trainer {
    private static Random r = new Random(4242);
    private Graph graph;
    private List<Path> population;
    private final int PSIZE = 100;
    private final int MUTATION = 1;
    private final int REPRODUCTION = 200;
    private final int TURNEYSIZE = 7;


    private int generation = 0;

    private List<Path> bestOfEachGeneration = new LinkedList<>();

    public Trainer(Graph graph) {
        this.graph = graph;
    }

    /**
     * @param path
     * @return anti-fitness
     */
    public Path fitness(Path path) {
        path.setLength(graph.getPathLength(path));
        return path;
    }

    public Path mutateSwap(Path path) {
        Path child = path.dulicate();
        for (int i = 0; i < MUTATION; i++) {
            int randomIndex1, randomIndex2;
            randomIndex1 = r.nextInt(path.solution.size());
            randomIndex2 = r.nextInt(path.solution.size());
            child.swap(randomIndex1, randomIndex2);
        }
        return child;
    }

    public Path mutateInvert(Path path) {
        Path child = path.dulicate();
        int upperBound, lowerBound;
        int size = path.solution.size();
        upperBound = 1 + r.nextInt(size);
        int partlength = (int) (Math.random() * (size));
        lowerBound = Math.max((upperBound - partlength), 0);
        Collections.reverse(child.solution.subList(lowerBound, upperBound));
        return child;

    }

    public Path mutate(Path path) {
        if (Math.random() < 0.2) {
            return mutateSwap(path);
        }
        return mutateInvert(path);
    }

    public List<Path> recombine() {
        List<Path> selectedParents = parentSelectionTurnament();

        return Graph.sliding(selectedParents, 2)
                .map(parents -> {
                    int crossover = r.nextInt(population.size());
                    List<Node> firstNodes = parents.get(0)
                            .solution.stream()
                            .limit(crossover)
                            .collect(Collectors.toList());

                    parents.get(1).solution.stream()
                            .skip(crossover)
                            .forEach(node -> {
                                while (firstNodes.contains(node)) {
                                    int nodeIndex = parents.get(0).solution.indexOf(node);
                                    node = parents.get(1).solution.get(nodeIndex);
                                }
                                firstNodes.add(node);
                            });

                    Path child = new Path();

                    child.solution = new LinkedList<>(firstNodes);

                    return child;
                })
                .map(this::mutate)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private List<Path> parentSelectionRandom() {
        return IntStream.range(0, REPRODUCTION)
                .mapToObj(i -> r.nextInt(population.size()))
                .map(parentIndex -> population.get(parentIndex))
                .collect(Collectors.toList());
    }

    private List<Path> parentSelectionTurnament() {
        List<Path> chickenDinner = new ArrayList<>();
        for (int i = 0; i < REPRODUCTION; i++) {
            Path winner = r.ints(TURNEYSIZE, 0, population.size())
                    .mapToObj(value -> population.get(value))
                    .min(Comparator.comparing(Path::getLength))
                    .orElseThrow(() -> new RuntimeException("no path"));
            chickenDinner.add(winner);
        }
        return chickenDinner;
    }

    public void train() {
        List<Path> children = recombine();
        population = childSelectionRank(children);

        Path best = population.get(0);
        graph.setDisplayPath(best);
        bestOfEachGeneration.add(best);
        generation++;
    }

    private List<Path> childSelectionBest(List<Path> children) {
        return Stream.concat(population.stream(), children.stream())
                .map(this::fitness)
                .sorted(Comparator.comparing(Path::getLength))
                .limit(PSIZE)
                .collect(Collectors.toList());
    }

    private List<Path> childSelectionRank(List<Path> children) {
        List<Double> probabilities = new ArrayList<>();
        List<Path> generation = Stream.concat(population.stream(), children.stream())
                .map(this::fitness)
                .sorted(Comparator.comparing(Path::getLength))
                .collect(Collectors.toList());
        for (int i = 0; i < children.size() - 1; i++) {
            double prob = 2.0 / generation.size() * (1 - ((double) i / generation.size() - 1));
            prob += i == 0 ? 0 : probabilities.get(i - 1);
            probabilities.add(prob);
        }
        double dist = 1.0 / PSIZE;
        double start = r.nextDouble() * dist;

        List<Path> winners = new ArrayList<>();
        for (int j = 0; j < PSIZE; j++) {
            for (int k = 0; k < probabilities.size() - 1; k++) {
                if (start < probabilities.get(k)) {
                    winners.add(generation.get(k));
                    break;
                }
            }
            start += dist;
        }
        return winners;
    }

    public void initPopulation() {
        population = new LinkedList<>();
        bestOfEachGeneration = new LinkedList<>();
        for (int i = 0; i < PSIZE; i++) {
            LinkedList<Node> nodeList = new LinkedList<>(graph.getNodes());
            Collections.shuffle(nodeList, r);
            Path p = new Path();
            p.solution = nodeList;
            fitness(p);
            population.add(p);
        }
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public List<Path> getPopulation() {
        return population;
    }

    public void setPopulation(List<Path> population) {
        this.population = population;
    }

    public List<Path> getBestOfEachGeneration() {
        return bestOfEachGeneration;
    }

    public void setBestOfEachGeneration(List<Path> bestOfEachGeneration) {
        this.bestOfEachGeneration = bestOfEachGeneration;
    }
}
