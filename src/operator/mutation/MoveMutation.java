package operator.mutation;

import chromosome.BitStringChromosome;
import chromosome.Chromosome;
import chromosome.IndexListChromosome;
import map.Grid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

// move mutation: pindahin satu fire station ke sel kosong lain, diulang sebanyak "moves" kali.
// jumlah fire station-nya tetap p, jadi nggak usah repair. ini induknya MoveOneMutation sama MoveTwoMutation.
public class MoveMutation implements MutationOperator {

    private final int moves; // mau mindahin fire station berapa kali

    protected MoveMutation(int moves) {
        this.moves = moves;
    }

    @Override
    public Chromosome mutate(Chromosome c, Grid grid, Random random) {
        // kalau nggak ada fire station, atau semua sel kosong udah keisi, ya nggak ada tempat buat pindah
        if (grid.p == 0 || grid.p == grid.numEmpty) {
            return c.copy();
        }

        // pilih jalur sesuai representasinya; getGenes()/decode() ngasih salinan, jadi kromosom aslinya aman nggak berubah
        if (c instanceof BitStringChromosome) {
            return mutateBits(((BitStringChromosome) c).getGenes(), grid, random);
        }
        if (c instanceof IndexListChromosome) {
            return mutateIndices(c.decode(), grid, random);
        }
        throw new IllegalArgumentException("representasi kromosom tidak dikenal");
    }

    // bit string: satu bit 1 acak dijadiin 0, satu bit 0 acak dijadiin 1
    private Chromosome mutateBits(int[] genes, Grid grid, Random random) {
        // ulangi pindahnya sebanyak moves
        for (int move = 0; move < moves; move++) {
            // pisahin posisi bit 1 (ada fire station) sama bit 0 (sel kosong yang masih bebas), urut dari kecil
            int[] selected = new int[grid.p];
            int[] zeroPositions = new int[grid.numEmpty - grid.p];
            int ones = 0;
            int zeros = 0;
            for (int i = 0; i < genes.length; i++) {
                if (genes[i] == 1) {
                    selected[ones++] = i;
                } else {
                    zeroPositions[zeros++] = i;
                }
            }

            // pilih fire station yang mau dipindah plus lokasi barunya, terus tukar bitnya
            int onePosition = selected[random.nextInt(selected.length)];
            int zeroPosition = zeroPositions[random.nextInt(zeroPositions.length)];
            genes[onePosition] = 0;
            genes[zeroPosition] = 1;
        }
        return BitStringChromosome.fromGenes(grid, genes);
    }

    // index list: satu gen acak diganti sel kosong yang belum kepakai, habis itu diurutin lagi
    private Chromosome mutateIndices(int[] genes, Grid grid, Random random) {
        // ulangi pindahnya sebanyak moves
        for (int move = 0; move < moves; move++) {
            // tandain sel yang lagi jadi fire station
            boolean[] used = new boolean[grid.numEmpty];
            for (int gene : genes) {
                used[gene] = true;
            }

            // kumpulin sel kosong yang belum kepakai buat calon lokasi baru
            List<Integer> available = new ArrayList<>();
            for (int i = 0; i < grid.numEmpty; i++) {
                if (!used[i]) {
                    available.add(i);
                }
            }

            // kalau nggak ada calon lokasi, udahan aja mutasinya
            if (available.isEmpty()) {
                break;
            }

            // pilih gen yang mau dipindah, terus ganti pakai salah satu calon lokasi
            int genePosition = random.nextInt(grid.p);
            genes[genePosition] = available.get(random.nextInt(available.size()));
        }

        // index list harus selalu urut, jadi diurutin dulu
        Arrays.sort(genes);
        return IndexListChromosome.fromGenes(grid, genes);
    }
}
