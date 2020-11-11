CREATE TABLE IF NOT EXISTS transaksi (`idTransaksi` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tipeTransaksi` TEXT NOT NULL, `idKategori` INTEGER NOT NULL, `idMataUang` INTEGER NOT NULL, `nominal` REAL NOT NULL);CREATE TABLE IF NOT EXISTS mata_uang (`idMataUang` INTEGER NOT NULL, `namaUang` TEXT NOT NULL, `namaUangShort` TEXT NOT NULL, PRIMARY KEY(`idMataUang`));CREATE TABLE IF NOT EXISTS kategori (`idKategori` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `namaKategori` TEXT, `idIcon` INTEGER NOT NULL);CREATE TABLE IF NOT EXISTS TransaksiKategori (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `id_kategori` INTEGER, `id_transaksi` INTEGER);CREATE TABLE IF NOT EXISTS sub_kategori (`idSubKategori` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `idKategori` INTEGER NOT NULL, `namaSubKategori` TEXT, `idIcon` INTEGER);CREATE TABLE IF NOT EXISTS icon_transaksi (`idIcon` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `namaIcon` TEXT NOT NULL, `lokasiIcon` TEXT NOT NULL);CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT);INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '6486bebaff28ef16c9a55fc7cebc5bfe');INSERT INTO mata_uang (namaUang, namaUangShort) VALUES ('Rupiah', 'IDR');INSERT INTO mata_uang (namaUang, namaUangShort) VALUES ('Euro', 'EUR');INSERT INTO mata_uang (namaUang, namaUangShort) VALUES ('United Stated Dollar', 'USD');INSERT INTO icon_transaksi (namaIcon, lokasiIcon) VALUES ('bag_icon', '');INSERT INTO icon_transaksi (namaIcon, lokasiIcon) VALUES ('bag_icon', '');INSERT INTO icon_transaksi (namaIcon, lokasiIcon) VALUES ('bag_icon', '');INSERT INTO icon_transaksi (namaIcon, lokasiIcon) VALUES ('bag_icon', '');INSERT INTO icon_transaksi (namaIcon, lokasiIcon) VALUES ('bag_icon', '');INSERT INTO kategori (namaKategori, idIcon) VALUES ('Food', 1);INSERT INTO kategori (namaKategori, idIcon) VALUES ('Bills', 1);INSERT INTO kategori (namaKategori, idIcon) VALUES ('Transportation', 1);INSERT INTO kategori (namaKategori, idIcon) VALUES ('Home', 1);INSERT INTO kategori (namaKategori, idIcon) VALUES ('Car', 1);INSERT INTO kategori (namaKategori, idIcon) VALUES ('Entertainment', 1);INSERT INTO kategori (namaKategori, idIcon) VALUES ('Shopping', 1);INSERT INTO kategori (namaKategori, idIcon) VALUES ('Telephone', 1);INSERT INTO sub_kategori (idKategori, namaSubKategori, idIcon) VALUES (1, 'Sarapan', 1);INSERT INTO sub_kategori (idKategori, namaSubKategori, idIcon) VALUES (1, 'Pulsa', 1);INSERT INTO sub_kategori (idKategori, namaSubKategori, idIcon) VALUES (1, 'Oli Motor', 1);INSERT INTO sub_kategori (idKategori, namaSubKategori, idIcon) VALUES (1, 'Token Listrik', 1);INSERT INTO sub_kategori (idKategori, namaSubKategori, idIcon) VALUES (1, 'Baju', 1);INSERT INTO transaksi (tipeTransaksi, idKategori, idMataUang, nominal) VALUES ('PENGELUARAN', 1, 1, 50000.0);INSERT INTO transaksi (tipeTransaksi, idKategori, idMataUang, nominal) VALUES ('PEMASUKAN', 1, 1, 60000.0);INSERT INTO transaksi (tipeTransaksi, idKategori, idMataUang, nominal) VALUES ('PENGELUARAN', 1, 1, 51200.0);INSERT INTO transaksi (tipeTransaksi, idKategori, idMataUang, nominal) VALUES ('PENGELUARAN', 1, 1, 533200.0);INSERT INTO transaksi (tipeTransaksi, idKategori, idMataUang, nominal) VALUES ('PENGELUARAN', 1, 1, 50660.0);INSERT INTO transaksi (tipeTransaksi, idKategori, idMataUang, nominal) VALUES ('PENGELUARAN', 1, 1, 78980.0);INSERT INTO transaksi (tipeTransaksi, idKategori, idMataUang, nominal) VALUES ('PENGELUARAN', 1, 1, 500230.0);INSERT INTO transaksi (tipeTransaksi, idKategori, idMataUang, nominal) VALUES ('PENGELUARAN', 1, 1, 3120.0);