package ga; // class ini berada di package ga

import chromosome.*; // pakai semua class kromosom (BitString, IndexList, Repair, Chromosome)
import map.Grid; // pakai class Grid dari package map
import operator.crossover.*; // pakai operator crossover (OnePoint, Uniform)
import operator.mutation.*; // pakai operator mutasi (MoveOne, MoveTwo)
import operator.selection.*; // pakai operator seleksi (Tournament, RouletteWheel)
import java.util.*; // pakai ArrayList, Collections, List, Random

/* class GeneticAlgorithmEngine: menjalankan siklus genetic algorithm untuk masalah penempatan fire station */
public class GeneticAlgorithmEngine { 

    private final Random random; // menyimpan objek random generator
    private final Grid grid; // menyimpan referensi objek peta grid
    private final int[][] distanceMatrix; // menyimpan matriks jarak dari bfs
    private final GAConfig config; // menyimpan konfigurasi hyperparameter
    private final List<Individual> population; // menyimpan daftar individu dalam populasi
    private int evaluationCount; // menghitung jumlah evaluasi fitness yang sudah dilakukan
    private long startTimeMillis; // mencatat waktu mulai eksekusi dalam milidetik
    private int generationCount; // menghitung jumlah generasi yang sudah dijalankan (untuk laporan CSV)
    private String stopReason = "BUDGET_HABIS"; // alasan berhenti: BUDGET_HABIS, STAGNAN, atau WAKTU_HABIS
    // operator genetik yang dipilih dari config (package operator)
    private final SelectionOperator selection;
    private final CrossoverOperator crossover;
    private final MutationOperator mutation;

    // membuat mesin ga dengan grid, matriks jarak hasil bfs, random seed, dan konfigurasi
    public GeneticAlgorithmEngine(Random random, Grid grid, int[][] distanceMatrix, GAConfig config) { // constructor utama
        if (random == null || grid == null || distanceMatrix == null || config == null) { // validasi parameter null
            throw new IllegalArgumentException("random, grid, distancematrix, dan config tidak boleh null"); // lempar error jika ada yang null
        } 
        if (grid.p < 0 || grid.p > grid.numEmpty) { // validasi jumlah fire station
            throw new IllegalArgumentException("jumlah fire station tidak sesuai jumlah sel kosong"); // lempar error jika tidak valid
        } 
        this.random = random; // mengisi variabel random
        this.grid = grid; // mengisi variabel grid
        this.distanceMatrix = distanceMatrix; // mengisi variabel distanceMatrix
        this.config = config; // mengisi variabel config
        this.population = new ArrayList<>(); // menginisialisasi list populasi kosong
        this.evaluationCount = 0; // set hitungan evaluasi awal ke nol
        this.selection = createSelection(config);
        this.crossover = createCrossover(config);
        this.mutation = createMutation(config);
        validateDistanceMatrix(); // memanggil method untuk validasi ukuran matriks jarak
    }

    // membuat operator seleksi sesuai SelectionType di config
    private static SelectionOperator createSelection(GAConfig config) {
        if (config.getSelectionType() == GAConfig.SelectionType.ROULETTE_WHEEL) {
            return new RouletteWheelSelection();
        }
        return new TournamentSelection(config.getTournamentSize());
    }

    // membuat operator crossover sesuai CrossoverType di config
    private static CrossoverOperator createCrossover(GAConfig config) {
        if (config.getCrossoverType() == GAConfig.CrossoverType.ONE_POINT) {
            return new OnePointCrossover();
        }
        return new UniformCrossover();
    }

    // membuat operator mutasi sesuai MutationType di config
    private static MutationOperator createMutation(GAConfig config) {
        if (config.getMutationType() == GAConfig.MutationType.MOVE_TWO) {
            return new MoveTwoMutation();
        }
        return new MoveOneMutation();
    }

    // memastikan matriks bfs memiliki satu baris per rumah dan satu kolom per sel kosong
    private void validateDistanceMatrix() { // method validasi matriks
        if (distanceMatrix.length != grid.numHouses) { // cek apakah jumlah baris sesuai jumlah rumah
            throw new IllegalArgumentException("jumlah baris distancematrix tidak sesuai jumlah rumah"); // error jika baris salah
        } 
        for (int[] row : distanceMatrix) { // perulangan mengecek tiap baris matriks
            if (row == null || row.length != grid.numEmpty) { // cek apakah baris null atau kolom tidak sesuai sel kosong
                throw new IllegalArgumentException("jumlah kolom distancematrix tidak sesuai jumlah sel kosong"); // error jika kolom salah
            } 
        } 
    } 

    // membuat satu kromosom acak sesuai jenis representasi yang dipilih di gaconfig 
    private Chromosome createRandomChromosome() { // method membuat kromosom acak
        if (config.getRepresentationType() == GAConfig.RepresentationType.BIT_STRING) { // cek apakah pakai bit string
            return new BitStringChromosome(grid, random); // kembalikan kromosom bit string
        } 
        return new IndexListChromosome(grid, random); // jika bukan, kembalikan index list chromosome
    } 

    // membuat populasi awal, setiap individu yang dibuat dihitung sebagai satu evaluasi
    public void initializePopulation() { // method inisialisasi populasi
        population.clear(); // bersihkan populasi lama
        evaluationCount = 0; // reset hitungan evaluasi
        int initialSize = Math.min(config.getPopulationSize(), config.getEvaluationBudget()); // tentukan ukuran awal populasi

        for (int i = 0; i < initialSize; i++) { // perulangan sebanyak ukuran populasi awal
            Chromosome chromosome = createRandomChromosome(); // buat kromosom acak baru
            population.add(new Individual(chromosome, distanceMatrix, grid)); // buat individu dan masukkan ke populasi
            evaluationCount++; // tambah hitungan evaluasi fitness
        } 
        Collections.sort(population); // urutkan populasi berdasarkan fitness terbaik
    } 

    // menjalankan ga hingga budget, stagnasi, atau batas waktu tercapai
    public Individual run() { // method utama menjalankan GA
        startTimeMillis = System.currentTimeMillis(); // catat waktu mulai program
        initializePopulation(); // panggil inisialisasi populasi awal
        generationCount = 0; // reset hitungan generasi
        stopReason = "BUDGET_HABIS"; // anggap berhenti karena budget kecuali ditemukan alasan lain

        if (population.isEmpty()) { // cek apakah populasi kosong
            throw new IllegalStateException("populasi kosong, tidak ada individu yang dapat dievaluasi"); // lempar error jika kosong
        } 

        int stagnantGenerations = 0; // inisialisasi penghitung generasi stagnan
        double bestCost = population.get(0).getCost(); // ambil cost terbaik dari individu pertama

        // setiap putaran menghasilkan generasi baru selama masih ada budget dan waktu
        while (evaluationCount < config.getEvaluationBudget() && !isTimeLimitReached()) { // perulangan utama ga
            List<Individual> newPopulation = new ArrayList<>(); // buat penampung populasi baru

            // elitisme menyalin individu terbaik tanpa menghitung fitness ulang
            int eliteCount = (int) Math.floor(
                    config.getElitismPercentage() * config.getPopulationSize()); // hitung jumlah individu elit
            eliteCount = Math.min(eliteCount, population.size()); // pastikan tidak melebihi ukuran populasi
            for (int i = 0; i < eliteCount; i++) { // perulangan memasukkan individu elit
                newPopulation.add(population.get(i).copy()); // salin individu elit ke populasi baru
            } 

            // buat anak sampai populasi penuh atau salah satu batas berhenti tercapai
            while (newPopulation.size() < config.getPopulationSize()
                    && evaluationCount < config.getEvaluationBudget()
                    && !isTimeLimitReached()) { // perulangan pengisian anak
                Individual parent1 = selectParent(); // pilih parent pertama
                Individual parent2 = selectParent(); // pilih parent kedua
                Chromosome child; // siapkan variabel untuk anak

                // crossover dilakukan berdasarkan probabilitas pc
                if (random.nextDouble() < config.getCrossoverRate()) { // cek apakah terjadi crossover
                    child = crossover.crossover(
                            parent1.getChromosome(), parent2.getChromosome(), grid, random); // lakukan crossover
                } else { 
                    child = parent1.getChromosome().copy(); // salin saja dari parent pertama
                } 

                // mutasi dilakukan berdasarkan probabilitas pm
                if (random.nextDouble() < config.getMutationRate()) { // cek apakah terjadi mutasi
                    child = mutation.mutate(child, grid, random); // lakukan mutasi pada anak
                } 

                // validasi akhir mencegah kromosom invalid masuk ke populasi
                if (!child.isValid()) { // cek apakah anak tidak valid
                    if (child instanceof BitStringChromosome) { // jika tipe bit string
                        int[] repairedBits = Repair.repairBits(((BitStringChromosome) child).getGenes(), grid, random); // perbaiki bit
                        child = BitStringChromosome.fromGenes(grid, repairedBits); // buat kromosom bit baru dari hasil perbaikan
                    } else { 
                        int[] repairedIndices = Repair.repairIndices(child.decode(), grid, random); // perbaiki index
                        child = IndexListChromosome.fromGenes(grid, repairedIndices); // buat kromosom index baru dari hasil perbaikan
                    } 
                } 

                // pembuatan individual menghitung cost dan fitness tepat satu kali
                newPopulation.add(new Individual(child, distanceMatrix, grid)); // buat individu baru dan masukkan ke populasi baru
                evaluationCount++; // tambah hitungan evaluasi
            } 

            // tidak ada evaluasi baru berarti budget atau waktu sudah habis
            if (newPopulation.isEmpty()) { // cek jika populasi baru kosong
                break; // keluar dari perulangan utama
            } 

            population.clear(); // kosongkan populasi lama
            population.addAll(newPopulation); // masukkan populasi baru
            Collections.sort(population); // urutkan populasi berdasarkan fitness
            generationCount++; // satu generasi selesai

            // stagnasi dihitung dari perubahan cost terbaik antargenerasi
            double currentBestCost = population.get(0).getCost(); // ambil cost terbaik saat ini
            if (currentBestCost < bestCost - 1.0e-12) { // cek apakah ada perbaikan cost
                bestCost = currentBestCost; // update cost terbaik
                stagnantGenerations = 0; // reset hitungan stagnasi
            } else { // jika tidak ada perbaikan
                stagnantGenerations++; // tambah hitungan stagnasi
            } 

            // nilai 0 pada stagnationlimit berarti pemeriksaan stagnasi dinonaktifkan
            if (config.getStagnationLimit() > 0
                    && stagnantGenerations >= config.getStagnationLimit()) { // cek apakah batas stagnasi tercapai
                stopReason = "STAGNAN"; // catat alasan berhenti
                break; // keluar dari perulangan utama
            } 
        } 

        if (stopReason.equals("BUDGET_HABIS") && evaluationCount < config.getEvaluationBudget()) { // berhenti padahal budget belum habis
            stopReason = "WAKTU_HABIS"; // berarti penyebabnya batas waktu
        } 
        return population.get(0); // kembalikan individu terbaik dari hasil akhir
    } 

    // memilih parent memakai operator seleksi yang dipilih di konfigurasi
    private Individual selectParent() { // method pemilihan parent
        return selection.select(population, random); // delegasikan ke operator seleksi
    }

    // mengembalikan true jika batas waktu aktif dan sudah terlampaui 
    private boolean isTimeLimitReached() { // method pengecekan batas waktu
        long limit = config.getTimeLimitMillis(); // ambil batasan waktu dari config
        return limit > 0 && System.currentTimeMillis() - startTimeMillis >= limit; // cek apakah waktu sekarang melebihi limit
    } 

    public int getGenerationCount() { return generationCount; } // getter jumlah generasi
    public String getStopReason() { return stopReason; } // getter alasan berhenti

    public int getEvaluationCount() { // getter untuk jumlah evaluasi
        return evaluationCount; // kembalikan nilai evaluation count
    } 

    public List<Individual> getPopulation() { // getter untuk list populasi
        return Collections.unmodifiableList(population); // kembalikan list populasi yang tidak bisa diubah langsung
    } 

    // mengembalikan individu terbaik yang ada saat ini, atau null jika belum ada populasi 
    public Individual getBestIndividual() { // getter individu terbaik
        return population.isEmpty() ? null : population.get(0); // kembalikan individu pertama atau null jika kosong
    } 
}