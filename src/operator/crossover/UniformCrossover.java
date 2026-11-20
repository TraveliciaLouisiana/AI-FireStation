package operator.crossover;

import chromosome.BitStringChromosome;
import chromosome.Chromosome;
import chromosome.IndexListChromosome;
import chromosome.Repair;
import map.Grid;
import java.util.Random;

// uniform crossover: tiap gen anak diambil acak dari parent a atau b, terus di-repair
public class UniformCrossover implements CrossoverOperator {

    @Override
    public Chromosome crossover(Chromosome a, Chromosome b, Grid grid, Random random) {
        // kalau bit string: kromosomnya sepanjang numEmpty bit
        if (a instanceof BitStringChromosome && b instanceof BitStringChromosome) {
            int[] genesA = ((BitStringChromosome) a).getGenes();
            int[] genesB = ((BitStringChromosome) b).getGenes();
            int[] child = new int[grid.numEmpty];

            // tiap bit lempar koin aja: true ambil dari a, false ambil dari b
            for (int i = 0; i < child.length; i++) {
                child[i] = random.nextBoolean() ? genesA[i] : genesB[i];
            }

            // jumlah bit 1-nya bisa jadi nggak pas p, jadi dibenerin dulu
            child = Repair.repairBits(child, grid, random);
            return BitStringChromosome.fromGenes(grid, child);
        }

        // kalau index list: kromosomnya isi p indeks sel kosong yang udah urut
        if (a instanceof IndexListChromosome && b instanceof IndexListChromosome) {
            int[] genesA = a.decode();
            int[] genesB = b.decode();
            int[] child = new int[grid.p];

            // tiap posisi indeks lempar koin juga: true ambil dari a, false ambil dari b
            for (int i = 0; i < grid.p; i++) {
                child[i] = random.nextBoolean() ? genesA[i] : genesB[i];
            }

            // hasil gabungannya bisa ada indeks dobel, jadi dibenerin biar tetap p indeks yang beda-beda
            child = Repair.repairIndices(child, grid, random);
            return IndexListChromosome.fromGenes(grid, child);
        }

        // dua parent-nya wajib pakai representasi yang sama, kalau nggak ya error
        throw new IllegalArgumentException("crossover harus menggunakan representasi kromosom yang sama");
    }
}
