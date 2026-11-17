
package ga; // class ini menjadi titik awal saat program dijalankan

import map.Grid; // menggunakan class grid untuk menyimpan informasi peta
import map.MapReader; // menggunakan class mapreader untuk membaca file peta
import map.DistanceMatrix; // menggunakan class distancematrix untuk menghitung jarak bfs
import map.OutputWriter; // menggunakan class outputwriter untuk membuat keluaran sesuai soal
import java.io.IOException; // menangani kesalahan saat membaca atau menulis file
import java.util.Random; // membuat angka acak untuk proses genetic algorithm

// class main menghubungkan proses membaca peta, menjalankan ga, dan menampilkan hasil
public class Main {

    // method main dijalankan pertama kali ketika program dimulai
    public static void main(String[] args) {

        // pastikan pengguna sudah memberikan nama file peta sebagai argumen pertama
        if (args.length < 1) {
            System.err.println("penggunaan: java ga.Main <file-peta> [representasi] [seleksi] [crossover] [mutasi] [populasi] [budget] [pc] [pm] [elitisme] [stagnasi] [waktu-ms] [seed] [file-output]");
            return; // hentikan program karena file peta belum diberikan
        }

        try {
            // mengambil lokasi file peta dari argumen pertama
            String mapPath = args[0];

            // menentukan representasi kromosom, nilai awalnya menggunakan bit string
            GAConfig.RepresentationType representation = args.length > 1
                    ? GAConfig.RepresentationType.valueOf(args[1].toUpperCase())
                    : GAConfig.RepresentationType.BIT_STRING;

            // menentukan metode seleksi parent, nilai awalnya menggunakan tournament
            GAConfig.SelectionType selection = args.length > 2
                    ? GAConfig.SelectionType.valueOf(args[2].toUpperCase())
                    : GAConfig.SelectionType.TOURNAMENT;

            // menentukan metode crossover, nilai awalnya menggunakan one point
            GAConfig.CrossoverType crossover = args.length > 3
                    ? GAConfig.CrossoverType.valueOf(args[3].toUpperCase())
                    : GAConfig.CrossoverType.ONE_POINT;

            // menentukan metode mutasi, nilai awalnya menggunakan move one
            GAConfig.MutationType mutation = args.length > 4
                    ? GAConfig.MutationType.valueOf(args[4].toUpperCase())
                    : GAConfig.MutationType.MOVE_ONE;

            // menentukan jumlah individu dalam satu populasi, defaultnya 100
            int populationSize = args.length > 5 ? Integer.parseInt(args[5]) : 100;

            // menentukan batas maksimal evaluasi fitness, defaultnya 10000
            int evaluationBudget = args.length > 6 ? Integer.parseInt(args[6]) : 10000;

            // menentukan probabilitas crossover, defaultnya 0.8 atau 80 persen
            double crossoverRate = args.length > 7 ? Double.parseDouble(args[7]) : 0.8;

            // menentukan probabilitas mutasi, defaultnya 0.05 atau 5 persen
            double mutationRate = args.length > 8 ? Double.parseDouble(args[8]) : 0.05;

            // menentukan persentase individu terbaik yang dipertahankan, defaultnya 0.2 atau 20 persen
            double elitismPercentage = args.length > 9 ? Double.parseDouble(args[9]) : 0.20;

            // menentukan batas generasi tanpa perbaikan cost, defaultnya 100 generasi
            int stagnationLimit = args.length > 10 ? Integer.parseInt(args[10]) : 100;

            // menentukan batas waktu program dalam milidetik, defaultnya 60000 atau 60 detik
            long timeLimitMillis = args.length > 11 ? Long.parseLong(args[11]) : 60000L;

            // menentukan seed supaya proses angka acak bisa diulang, defaultnya memakai waktu saat ini
            long seed = args.length > 12 ? Long.parseLong(args[12]) : System.currentTimeMillis();

            // menentukan lokasi file output jika diberikan oleh pengguna
            String outputPath = args.length > 13 ? args[13] : null;

            // membaca file peta dan mengubah isinya menjadi objek grid
            Grid grid = MapReader.read(mapPath);

            // memastikan jumlah fire station tidak melebihi jumlah sel kosong
            if (grid.p > grid.numEmpty) {
                throw new IllegalArgumentException("jumlah fire station melebihi jumlah sel kosong");
            }

            // menghitung jarak dari setiap rumah ke seluruh sel kosong menggunakan bfs
            DistanceMatrix distanceMatrix = new DistanceMatrix(grid);

            // membuat konfigurasi genetic algorithm dari hyperparameter yang sudah ditentukan
            GAConfig config = new GAConfig(
                    populationSize, // jumlah individu dalam populasi
                    evaluationBudget, // batas jumlah evaluasi fitness
                    crossoverRate, // peluang terjadinya crossover
                    mutationRate, // peluang terjadinya mutasi
                    elitismPercentage, // persentase individu terbaik yang dipertahankan
                    stagnationLimit, // batas generasi stagnan
                    timeLimitMillis, // batas waktu eksekusi dalam milidetik
                    3, // jumlah kandidat yang dibandingkan pada tournament selection
                    representation, // jenis representasi kromosom yang digunakan
                    selection, // jenis seleksi parent yang digunakan
                    crossover, // jenis crossover yang digunakan
                    mutation // jenis mutasi yang digunakan
            );

            // membuat random berdasarkan seed yang sudah dipilih
            Random random = new Random(seed);

            // membuat mesin genetic algorithm dengan data peta, jarak bfs, dan konfigurasi
            GeneticAlgorithmEngine engine = new GeneticAlgorithmEngine(
                    random, grid, distanceMatrix.dist, config
            );

            // menjalankan genetic algorithm sampai salah satu batas berhenti tercapai
            Individual bestIndividual = engine.run();

            // mengambil daftar indeks sel kosong yang dipilih sebagai lokasi fire station
            int[] fireStationCells = bestIndividual.getChromosome().decode();

            // menyusun hasil menjadi format sesuai ketentuan soal
            String result = OutputWriter.format(
                    grid, fireStationCells, bestIndividual.getCost()
            );

            // jika lokasi file output diberikan, hasil ditulis ke file tersebut
            if (outputPath != null) {
                OutputWriter.write(
                        outputPath, grid, fireStationCells, bestIndividual.getCost()
                );
            } else {
                // jika tidak ada file output, hasil ditampilkan langsung ke layar
                System.out.print(result);
            }

        } catch (IOException e) {
            // menampilkan pesan jika file peta tidak bisa dibaca atau file output gagal ditulis
            System.err.println("terjadi kesalahan file: " + e.getMessage());

        } catch (IllegalArgumentException e) {
            // menampilkan pesan jika argumen atau isi peta tidak sesuai
            System.err.println("input tidak valid: " + e.getMessage());

        } catch (Exception e) {
            // menangani kesalahan lain yang mungkin terjadi saat program dijalankan
            System.err.println("program mengalami kesalahan: " + e.getMessage());
        }
    }
}
