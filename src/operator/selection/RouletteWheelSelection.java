package operator.selection;

import ga.Individual;
import java.util.List;
import java.util.Random;

// roulette wheel selection: makin gede fitness-nya, makin gede peluang kepilih (fitness wajib positif)
public class RouletteWheelSelection implements SelectionOperator {

    @Override
    public Individual select(List<Individual> population, Random random) {
        // jumlahin semua fitness
        double totalFitness = 0.0;
        for (Individual individual : population) {
            totalFitness += individual.getFitness();
        }

        // jaga-jaga kalau total fitness-nya aneh pilih acak biasa aja
        if (totalFitness <= 0.0 || Double.isInfinite(totalFitness) || Double.isNaN(totalFitness)) {
            return population.get(random.nextInt(population.size()));
        }

        // spin the roulette, ambil titik acak antara 0 sampai total fitness
        double target = random.nextDouble() * totalFitness;

        // cek potongan roulette satu-satu; yang potongannya kena target, dia yang menang
        double cumulativeFitness = 0.0;
        for (Individual individual : population) {
            cumulativeFitness += individual.getFitness();
            if (cumulativeFitness >= target) {
                return individual;
            }
        }

        // jaga-jaga kalau pembulatan floating point bikin target sedikit kelewatan dari jumlah kumulatif
        return population.get(population.size() - 1);
    }
}
