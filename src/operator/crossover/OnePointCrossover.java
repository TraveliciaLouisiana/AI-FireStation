package operator.crossover;

import chromosome.BitStringChromosome;
import chromosome.Chromosome;
import chromosome.IndexListChromosome;
import chromosome.Repair;
import map.Grid;
import java.util.Random;

// one-point crossover: potong di satu titik acak, bagian depan dari parent a, sisanya dari parent b, terus di-repair
public class OnePointCrossover implements CrossoverOperator {

    @Override
    public Chromosome crossover(Chromosome a, Chromosome b, Grid grid, Random random) {
        // kalau bit string: kromosomnya sepanjang numEmpty bit
        if (a instanceof BitStringChromosome && b instanceof BitStringChromosome) {
            int[] genesA = ((BitStringChromosome) a).getGenes();
            int[] genesB = ((BitStringChromosome) b).getGenes();
            int[] child = new int[grid.numEmpty];

            // cari titik potong 0..numEmpty (0 = semuanya dari b, numEmpty = semuanya dari a)
            int cut = random.nextInt(grid.numEmpty + 1);

            // salin bitnya: sebelum titik potong ambil dari a, dari titik potong ke belakang ambil dari b
            for (int i = 0; i < child.length; i++) {
                child[i] = (i < cut) ? genesA[i] : genesB[i];
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

            // cari titik potong 0..p
            int cut = random.nextInt(grid.p + 1);

            // salin indeksnya: sebelum titik potong ambil dari a, dari titik potong ke belakang ambil dari b
            for (int i = 0; i < grid.p; i++) {
                child[i] = (i < cut) ? genesA[i] : genesB[i];
            }

            // hasil gabungannya bisa ada indeks dobel, jadi dibenerin biar tetap p indeks yang beda-beda
            child = Repair.repairIndices(child, grid, random);
            return IndexListChromosome.fromGenes(grid, child);
        }

        // dua parent-nya wajib pakai representasi yang sama, kalau nggak ya error
        throw new IllegalArgumentException("crossover harus menggunakan representasi kromosom yang sama");
    }
}
