## Panduan Menjalankan Testing

## 1. Persiapan

Jalankan dari folder utama project:

```powershell
git fetch
git switch fitur/experiment-final
git pull
```

Pastikan folder `TestCases` berada sejajar dengan folder `src`.

## 2. Compile Program

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
```

## 3. Jalankan Testing Tahap 1

Tahap 1 bertujuan membandingkan 16 kombinasi model GA untuk mengetahui model mana yang menghasilkan cost paling kecil.

Setiap anggota menguji 4 kombinasi model dengan total 1.440 percobaan.

**Anggota 1 – INDEX_LIST + TOURNAMENT**

Menguji representasi kromosom `INDEX_LIST` dengan metode seleksi `TOURNAMENT`. Menggunakan dua jenis crossover (`ONE_POINT` dan `UNIFORM`) serta dua jenis mutasi (`MOVE_ONE` dan `MOVE_TWO`).

```powershell
java -cp out experiment.Main tahap=1 peta=TestCases out=hasil/t1_1.csv model=INDEX_LIST__TOURNAMENT__ONE_POINT__MOVE_ONE,INDEX_LIST__TOURNAMENT__ONE_POINT__MOVE_TWO,INDEX_LIST__TOURNAMENT__UNIFORM__MOVE_ONE,INDEX_LIST__TOURNAMENT__UNIFORM__MOVE_TWO
```

**Anggota 2 – INDEX_LIST + ROULETTE_WHEEL**

Menguji representasi kromosom `INDEX_LIST` dengan metode seleksi `ROULETTE_WHEEL`, menggunakan variasi crossover dan mutasi yang sama.

```powershell
java -cp out experiment.Main tahap=1 peta=TestCases out=hasil/t1_2.csv model=INDEX_LIST__ROULETTE_WHEEL__ONE_POINT__MOVE_ONE,INDEX_LIST__ROULETTE_WHEEL__ONE_POINT__MOVE_TWO,INDEX_LIST__ROULETTE_WHEEL__UNIFORM__MOVE_ONE,INDEX_LIST__ROULETTE_WHEEL__UNIFORM__MOVE_TWO
```

**Anggota 3 – BIT_STRING + TOURNAMENT**

Menguji representasi kromosom `BIT_STRING` dengan metode seleksi `TOURNAMENT`, menggunakan dua variasi crossover dan dua variasi mutasi.

```powershell
java -cp out experiment.Main tahap=1 peta=TestCases out=hasil/t1_3.csv model=BIT_STRING__TOURNAMENT__ONE_POINT__MOVE_ONE,BIT_STRING__TOURNAMENT__ONE_POINT__MOVE_TWO,BIT_STRING__TOURNAMENT__UNIFORM__MOVE_ONE,BIT_STRING__TOURNAMENT__UNIFORM__MOVE_TWO
```

**Anggota 4 – BIT_STRING + ROULETTE_WHEEL**

Menguji representasi kromosom `BIT_STRING` dengan metode seleksi `ROULETTE_WHEEL`, menggunakan variasi crossover dan mutasi yang sama.

```powershell
java -cp out experiment.Main tahap=1 peta=TestCases out=hasil/t1_4.csv model=BIT_STRING__ROULETTE_WHEEL__ONE_POINT__MOVE_ONE,BIT_STRING__ROULETTE_WHEEL__ONE_POINT__MOVE_TWO,BIT_STRING__ROULETTE_WHEEL__UNIFORM__MOVE_ONE,BIT_STRING__ROULETTE_WHEEL__UNIFORM__MOVE_TWO
```

**Keterangan istilah:**

- `INDEX_LIST` dan `BIT_STRING`: representasi kromosom yang digunakan.
- `TOURNAMENT` dan `ROULETTE_WHEEL`: metode seleksi individu.
- `ONE_POINT` dan `UNIFORM`: metode crossover.
- `MOVE_ONE` dan `MOVE_TWO`: variasi metode mutasi.

## 4. Setelah Testing Selesai

Pastikan file CSV sudah terbentuk di folder `hasil`, kemudian push ke branch:

```powershell
git add hasil/
git commit -m "Menambahkan hasil testing tahap 1"
git push origin fitur/experiment-final
```

## Catatan Penting

- Jangan mengubah kode atau parameter eksperimen.
- Jangan membuka file CSV di Excel saat program berjalan.
- Pastikan laptop tetap menyala selama testing.
- Jika proses terhenti, jalankan kembali command yang sama.
- Setelah selesai, kabari anggota kelompok lainnya.
"""
