package operator.selection;

import ga.Individual;
import java.util.List;
import java.util.Random;

// tournament selection: comot k kandidat acak (boleh kepilih dobel), yang fitness-nya paling tinggi menang
public class TournamentSelection implements SelectionOperator {

    private final int k; // jumlah peserta turnamen

    public TournamentSelection(int k) {
        // turnamen minimal harus ada satu peserta dong
        if (k < 1) {
            throw new IllegalArgumentException("ukuran turnamen harus minimal 1");
        }
        this.k = k;
    }

    @Override
    public Individual select(List<Individual> population, Random random) {
        // comot k kandidat acak, pegang yang fitness-nya paling tinggi sejauh ini
        Individual best = null;
        for (int i = 0; i < k; i++) {
            Individual candidate = population.get(random.nextInt(population.size()));
            // juaranya cuma ganti kalau kandidat beneran lebih bagus (kalau seri, juara lama tetap)
            if (best == null || candidate.getFitness() > best.getFitness()) {
                best = candidate;
            }
        }
        return best;
    }
}
