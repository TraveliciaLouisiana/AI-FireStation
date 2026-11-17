# Tubes-AI
Tugas 1

# Testing Algoritma Genetika (GA)

## 1. Persiapan

Jalankan perintah berikut dari folder utama project:

```bash
git fetch
git switch fitur/experiment-final
git pull
```

Pastikan folder `TestCases` berada sejajar dengan folder `src`.

## 2. Compile Program

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
```

## 3. Testing Tahap 1

Tahap 1 dilakukan untuk membandingkan 16 kombinasi model GA dan mencari model dengan cost terkecil.

Setiap anggota menjalankan 4 model dengan total 1.440 percobaan.

| Anggota | Representasi | Seleksi |
|---|---|---|
| 1 | INDEX_LIST | TOURNAMENT |
| 2 | INDEX_LIST | ROULETTE_WHEEL |
| 3 | BIT_STRING | TOURNAMENT |
| 4 | BIT_STRING | ROULETTE_WHEEL |

### Anggota 1 – INDEX_LIST + TOURNAMENT

Menguji kombinasi crossover ONE_POINT dan UNIFORM serta mutasi MOVE_ONE dan MOVE_TWO.

```bash
java -cp out experiment.Main tahap=1 peta=TestCases out=hasil/t1_1.csv model=INDEX_LIST_TOURNAMENTONE_POINTMOVE_ONE,INDEX_LISTTOURNAMENTONE_POINTMOVE_TWO,INDEX_LISTTOURNAMENTUNIFORMMOVE_ONE,INDEX_LISTTOURNAMENTUNIFORM_MOVE_TWO
```

### Anggota 2 – INDEX_LIST + ROULETTE_WHEEL

Menguji representasi INDEX_LIST dengan seleksi ROULETTE_WHEEL.

```bash
java -cp out experiment.Main tahap=1 peta=TestCases out=hasil/t1_2.csv model=INDEX_LIST_ROULETTE_WHEELONE_POINTMOVE_ONE,INDEX_LISTROULETTE_WHEELONE_POINTMOVE_TWO,INDEX_LISTROULETTE_WHEELUNIFORMMOVE_ONE,INDEX_LISTROULETTE_WHEELUNIFORM_MOVE_TWO
```

### Anggota 3 – BIT_STRING + TOURNAMENT

Menguji representasi BIT_STRING dengan seleksi TOURNAMENT.

```bash
java -cp out experiment.Main tahap=1 peta=TestCases out=hasil/t1_3.csv model=BIT_STRING_TOURNAMENTONE_POINTMOVE_ONE,BIT_STRINGTOURNAMENTONE_POINTMOVE_TWO,BIT_STRINGTOURNAMENTUNIFORMMOVE_ONE,BIT_STRINGTOURNAMENTUNIFORMMOVE_TWO
```

### Anggota 4 – BIT_STRING + ROULETTE_WHEEL

Menguji representasi BIT_STRING dengan seleksi ROULETTE_WHEEL.

```bash
java -cp out experiment.Main tahap=1 peta=TestCases out=hasil/t1_4.csv model=BIT_STRING_ROULETTE_WHEELONE_POINTMOVE_ONE,BIT_STRINGROULETTE_WHEELONE_POINTMOVE_TWO,BIT_STRINGROULETTE_WHEELUNIFORMMOVE_ONE,BIT_STRINGROULETTE_WHEELUNIFORMMOVE_TWO
```

## 4. Pengumpulan Hasil

Setelah testing selesai, pastikan file CSV sudah terbentuk di folder `hasil`, kemudian jalankan:

```bash
git add hasil/
git commit -m "Menambahkan hasil testing tahap 1"
git push origin fitur/experiment-final
```

## 5. Catatan

- Jangan mengubah kode atau parameter eksperimen.
- Jangan membuka CSV di Excel selama program berjalan.
- Pastikan laptop tetap menyala selama testing.
- Jika proses terhenti, jalankan kembali command yang sama.
- Setelah selesai, informasikan hasilnya kepada anggota kelompok.

## 6. Tahap Selanjutnya

Seluruh hasil CSV akan digabungkan untuk menghitung rata-rata cost dan menentukan 3 model terbaik. Model terpilih akan digunakan untuk eksperimen Tahap 2A, dilanjutkan Tahap 2B dan pengujian pada peta besar.
