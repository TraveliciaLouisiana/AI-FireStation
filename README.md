# Tubes-AI
Tugas 1

# Panduan Menjalankan Testing

## 1. Persiapan

Jalankan perintah berikut dari folder utama project:

```bash
git fetch
git switch fitur/experiment-final
git pull
```

Pastikan folder `TestCases` berada sejajar dengan folder `src`.

## 2. Compile Program

Jalankan perintah berikut melalui **PowerShell**:

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
```

## 3. Menjalankan Testing Tahap 1

Tahap 1 bertujuan membandingkan **16 kombinasi model Genetic Algorithm (GA)** untuk mengetahui model yang menghasilkan cost paling kecil.

Setiap anggota menguji 4 kombinasi model dengan total **1.440 percobaan**.

### Anggota 1 – INDEX_LIST + TOURNAMENT

Menguji representasi kromosom `INDEX_LIST` dengan metode seleksi `TOURNAMENT`. Menggunakan dua jenis crossover (`ONE_POINT` dan `UNIFORM`) serta dua jenis mutasi (`MOVE_ONE` dan `MOVE_TWO`).

```bash
java -cp out experiment.Main tahap=1 peta=TestCases out=hasil/t1_1.csv model=INDEX_LIST_TOURNAMENTONE_POINTMOVE_ONE,INDEX_LISTTOURNAMENTONE_POINTMOVE_TWO,INDEX_LISTTOURNAMENTUNIFORMMOVE_ONE,INDEX_LISTTOURNAMENTUNIFORM_MOVE_TWO
```

### Anggota 2 – INDEX_LIST + ROULETTE_WHEEL

Menguji representasi kromosom `INDEX_LIST` dengan metode seleksi `ROULETTE_WHEEL`, menggunakan variasi crossover dan mutasi yang sama.

```bash
java -cp out experiment.Main tahap=1 peta=TestCases out=hasil/t1_2.csv model=INDEX_LIST_ROULETTE_WHEELONE_POINTMOVE_ONE,INDEX_LISTROULETTE_WHEELONE_POINTMOVE_TWO,INDEX_LISTROULETTE_WHEELUNIFORMMOVE_ONE,INDEX_LISTROULETTE_WHEELUNIFORM_MOVE_TWO
```

### Anggota 3 – BIT_STRING + TOURNAMENT

Menguji representasi kromosom `BIT_STRING` dengan metode seleksi `TOURNAMENT`, menggunakan dua variasi crossover dan dua variasi mutasi.

```bash
java -cp out experiment.Main tahap=1 peta=TestCases out=hasil/t1_3.csv model=BIT_STRING_TOURNAMENTONE_POINTMOVE_ONE,BIT_STRINGTOURNAMENTONE_POINTMOVE_TWO,BIT_STRINGTOURNAMENTUNIFORMMOVE_ONE,BIT_STRINGTOURNAMENTUNIFORM_MOVE_TWO
```

### Anggota 4 – BIT_STRING + ROULETTE_WHEEL

Menguji representasi kromosom `BIT_STRING` dengan metode seleksi `ROULETTE_WHEEL`, menggunakan variasi crossover dan mutasi yang sama.

```bash
java -cp out experiment.Main tahap=1 peta=TestCases out=hasil/t1_4.csv model=BIT_STRING_ROULETTE_WHEELONE_POINTMOVE_ONE,BIT_STRINGROULETTE_WHEELONE_POINTMOVE_TWO,BIT_STRINGROULETTE_WHEELUNIFORMMOVE_ONE,BIT_STRINGROULETTE_WHEELUNIFORMMOVE_TWO
```

### Keterangan Istilah

| Istilah | Keterangan |
|---|---|
| `INDEX_LIST` dan `BIT_STRING` | Representasi kromosom yang digunakan |
| `TOURNAMENT` dan `ROULETTE_WHEEL` | Metode seleksi individu |
| `ONE_POINT` dan `UNIFORM` | Metode crossover |
| `MOVE_ONE` dan `MOVE_TWO` | Variasi metode mutasi |

## 4. Setelah Testing Selesai

Pastikan file CSV sudah terbentuk di dalam folder `hasil`.

File hasil testing yang diharapkan:

```text
hasil/
├── t1_1.csv
├── t1_2.csv
├── t1_3.csv
└── t1_4.csv
```

Setelah itu, push hasil testing ke branch `fitur/experiment-final`:

```bash
git add hasil/
git commit -m "Menambahkan hasil testing tahap 1"
git push origin fitur/experiment-final
```

## 5. Catatan Penting

- Jangan mengubah kode atau parameter eksperimen.
- Jangan membuka file CSV di Excel saat program sedang berjalan.
- Pastikan laptop tetap menyala selama proses testing.
- Jika proses terhenti, jalankan kembali command yang sama.
- Setelah testing selesai, kabari anggota kelompok lainnya.
