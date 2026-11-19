# pilih_nilai.py
# Fungsi: memeringkat nilai satu hyper-parameter (Tahap 2A) atau kombinasi (Tahap 2B).
# Cara pakai (dari folder root repo):
#   python pilih_nilai.py populasi hasil/t2a_M1_pop.csv
#   python pilih_nilai.py pc hasil/t2a_M1_pc.csv
#   python pilih_nilai.py kombinasi hasil/t2b_M1.csv     (untuk Tahap 2B: populasi + pc)
# Alasan tiap langkah: tiap peta punya skala cost berbeda, jadi nilai dibandingkan
# lewat peringkat PER PETA (1 = cost rata-rata terkecil), baru peringkatnya dirata-ratakan.
# Hanya memakai library bawaan Python.
import csv, sys, statistics, collections

def main():
    if len(sys.argv) < 3:
        print("Pemakaian: python pilih_nilai.py <kolom|kombinasi> file1.csv [file2.csv ...]")
        return
    kolom = sys.argv[1]
    baris = []
    for f in sys.argv[2:]:
        with open(f, newline="", encoding="utf-8") as h:
            baris += [r for r in csv.DictReader(h) if r["status"] == "OK"]
    if not baris:
        print("Tidak ada baris OK."); return

    def kunci(r):  # nilai yang sedang dibandingkan
        if kolom == "kombinasi":
            return "pop=" + r["populasi"] + " pc=" + r["pc"]
        return r[kolom]

    # rata-rata cost per (peta, nilai) lintas seed
    cost = collections.defaultdict(list)
    for r in baris:
        cost[(r["file_peta"], kunci(r))].append(float(r["cost"]))
    rata = {k: statistics.mean(v) for k, v in cost.items()}
    nilai = sorted({k[1] for k in rata})
    # hanya peta yang datanya lengkap untuk SEMUA nilai yang ikut diperingkat
    semua_peta = sorted({k[0] for k in rata})
    peta = [p for p in semua_peta if all((p, v) in rata for v in nilai)]
    if len(peta) < len(semua_peta):
        print("PERINGATAN: ada", len(semua_peta) - len(peta), "peta yang datanya belum lengkap, dilewati. Pastikan run sudah selesai.")

    # peringkat tiap nilai di tiap peta
    rank = collections.defaultdict(list)
    for p in peta:
        urut = sorted(nilai, key=lambda v: rata[(p, v)])
        for i, v in enumerate(urut, start=1):
            rank[v].append(i)

    hasil = []
    for v in nilai:
        semua = [float(r["cost"]) for r in baris if kunci(r) == v]
        wkt = statistics.mean(float(r["waktu_ms"]) for r in baris if kunci(r) == v)
        hasil.append((statistics.mean(rank[v]), statistics.mean(semua), len(semua), wkt, v))
    hasil.sort()
    print("Jumlah peta:", len(peta), "| baris OK:", len(baris))
    print(f"{'#':>2} {'rata_peringkat':>14} {'rata_cost':>10} {'n_run':>6} {'waktu_ms':>9}  nilai")
    for i, (rk, c, n, w, v) in enumerate(hasil, start=1):
        print(f"{i:>2} {rk:>14.2f} {c:>10.4f} {n:>6} {w:>9.0f}  {v}")
    print("\nTerbaik :", hasil[0][4])
    if len(hasil) > 1:
        print("Terbaik ke-2:", hasil[1][4], "(dipakai untuk Tahap 2B)")

main()
