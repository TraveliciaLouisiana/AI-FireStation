# Tubes-AI
Tugas 1

Cara Menjalankan Testing
1. Persiapan
Jalankan dari folder utama project:
git fetch
git switch fitur/experiment-final
git pull
Pastikan folder TestCases berada sejajar dengan folder src.
2. Compile Program
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
3. Jalankan Testing Tahap 1
Tahap 1 bertujuan membandingkan 16 kombinasi model GA untuk mengetahui model mana yang menghasilkan cost paling kecil.
Setiap anggota menguji 4 kombinasi model dengan total 1.440 percobaan.
Anggota 1 – INDEX_LIST + TOURNAMENT
Menguji representasi kromosom INDEX_LIST dengan metode seleksi TOURNAMENT. Menggunakan dua jenis crossover (ONE_POINT dan UNIFORM) serta dua jenis mutasi (MOVE_ONE dan MOVE_TWO).
java -cp out experiment.Main tahap=1 peta=TestCases out=hasil/t1_1.csv model=INDEX_LIST_TOURNAMENTONE_POINTMOVE_ONE,INDEX_LISTTOURNAMENTONE_POINTMOVE_TWO,INDEX_LISTTOURNAMENTUNIFORMMOVE_ONE,INDEX_LISTTOURNAMENTUNIFORM_MOVE_TWO
Anggota 2 – INDEX_LIST + ROULETTE_WHEEL
Menguji representasi kromosom INDEX_LIST dengan metode seleksi ROULETTE_WHEEL, menggunakan variasi crossover dan mutasi yang sama.
java -cp out experiment.Main tahap=1 peta=TestCases out=hasil/t1_2.csv model=INDEX_LIST_ROULETTE_WHEELONE_POINTMOVE_ONE,INDEX_LISTROULETTE_WHEELONE_POINTMOVE_TWO,INDEX_LISTROULETTE_WHEELUNIFORMMOVE_ONE,INDEX_LISTROULETTE_WHEELUNIFORM_MOVE_TWO
Anggota 3 – BIT_STRING + TOURNAMENT
Menguji representasi kromosom BIT_STRING dengan metode seleksi TOURNAMENT, menggunakan dua variasi crossover dan dua variasi mutasi.
java -cp out experiment.Main tahap=1 peta=TestCases out=hasil/t1_3.csv model=BIT_STRING_TOURNAMENTONE_POINTMOVE_ONE,BIT_STRINGTOURNAMENTONE_POINTMOVE_TWO,BIT_STRINGTOURNAMENTUNIFORMMOVE_ONE,BIT_STRINGTOURNAMENTUNIFORM_MOVE_TWO
Anggota 4 – BIT_STRING + ROULETTE_WHEEL
Menguji representasi kromosom BIT_STRING dengan metode seleksi ROULETTE_WHEEL, menggunakan variasi crossover dan mutasi yang sama.
java -cp out experiment.Main tahap=1 peta=TestCases out=hasil/t1_4.csv model=BIT_STRING_ROULETTE_WHEELONE_POINTMOVE_ONE,BIT_STRINGROULETTE_WHEELONE_POINTMOVE_TWO,BIT_STRINGROULETTE_WHEELUNIFORMMOVE_ONE,BIT_STRINGROULETTE_WHEELUNIFORMMOVE_TWO
Keterangan istilah:
INDEX_LIST dan BIT_STRING: representasi kromosom yang digunakan.
TOURNAMENT dan ROULETTE_WHEEL: metode seleksi individu.
ONE_POINT dan UNIFORM: metode crossover.
MOVE_ONE dan MOVE_TWO: variasi metode mutasi.
4. Setelah Testing Selesai
Pastikan file CSV sudah terbentuk di folder hasil, kemudian push ke branch:
git add hasil/
git commit -m "Menambahkan hasil testing tahap 1"
git push origin fitur/experiment-final
Catatan Penting
Jangan mengubah kode atau parameter eksperimen.
Jangan membuka file CSV di Excel saat program berjalan.
Pastikan laptop tetap menyala selama testing.
Jika proses terhenti, jalankan kembali command yang sama.
Setelah selesai, kabari anggota kelompok lainnya.


