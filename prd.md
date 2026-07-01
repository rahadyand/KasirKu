# PRODUCT REQUIREMENTS DOCUMENT (PRD)
## Sistem Point of Sale (POS) CLI - Kasir Retail Berbasis Java

---

## 1. RINGKASAN EKSEKUTIF (Executive Summary)

### Deskripsi Produk
Sistem POS CLI adalah aplikasi kasir terminal berbasis Java murni (Pure Java) yang dirancang untuk mengelola transaksi penjualan retail dengan antarmuka berbasis baris perintah (Command Line Interface). Sistem ini mengintegrasikan manajemen inventori real-time, keranjang belanja interaktif, dan antrean pembayaran, memungkinkan operator kasir untuk memproses penjualan secara efisien dari satu titik kontrol.

Dibangun dengan prinsip Object-Oriented Programming (OOP) dan struktur data yang optimal, sistem ini mendemonstrasikan implementasi praktis dari HashMap (pencarian O(1)), Stack (undo/void LIFO), dan Queue (antrean pembayaran FIFO) dalam konteks bisnis retail nyata.

### Masalah Utama yang Diselesaikan
1. **Manajemen Stok Real-Time**: Mencegah overselling dengan validasi stok pada setiap transaksi  
2. **Operasi Undo/Void**: Memungkinkan pembatalan item terakhir dengan cepat menggunakan prinsip LIFO  
3. **Antrean Pembayaran Terstruktur**: Mengelola urutan pembayaran pelanggan secara FIFO untuk efisiensi kasir  
4. **Katalog Produk Cepat**: Pencarian produk instant tanpa delay menggunakan hash-based lookup  

---

## 2. TUJUAN & SASARAN (Goals & Objectives)

### Tujuan Fungsional
- **F1**: Menampilkan katalog lengkap produk dengan informasi ID, nama, harga, dan stok
- **F2**: Memungkinkan pelanggan menambah barang ke keranjang belanja dengan validasi stok
- **F3**: Mendukung pembatalan barang terakhir (Void/Undo) dan pengembalian stok otomatis
- **F4**: Menampilkan ringkasan keranjang belanja yang sedang aktif
- **F5**: Memproses checkout dan memasukkan transaksi ke antrean pembayaran
- **F6**: Memproses pembayaran transaksi terdepan dalam antrean dengan print invoice
- **F7**: Mengelola sistem hingga operator kasir siap keluar

### Tujuan Non-Fungsional
- **Performa**: Pencarian produk dan penambahan item harus O(1) / instant (< 100ms)
- **Keandalan**: Stok tidak boleh menjadi negatif; validasi real-time diperlukan
- **Usability**: UI terminal yang intuitif dengan menu navigasi yang jelas
- **Maintainability**: Kode terstruktur dengan OOP untuk kemudahan pemeliharaan dan ekspansi

---

## 3. TARGET PENGGUNA & AKTOR (User Personas / Actors)

### 1. **Operator Kasir (Cashier)**
- **Deskripsi**: Pengguna utama yang bertanggung jawab memproses transaksi penjualan di counter
- **Hak Akses**:
  - Melihat katalog barang lengkap
  - Menambah barang ke keranjang belanja
  - Melakukan void/undo pada barang terakhir yang ditambahkan
  - Melihat detail keranjang belanja aktif
  - Melakukan checkout dan memasukkan transaksi ke antrean
  - Memproses pembayaran transaksi antrian terdepan
  - Mencetak invoice pembayaran
- **Use Case Utama**: Menangani pelanggan dari awal (penambahan item) hingga pembayaran selesai

### 2. **Sistem Internal (Inventory Manager)**
- **Deskripsi**: Komponen backend yang berjalan otomatis untuk mengelola basis data produk
- **Hak Akses**:
  - Menyimpan dan mengupdate informasi produk (ID, nama, harga, stok)
  - Mencari produk berdasarkan ID
  - Mengambil daftar semua produk
  - Mengurangi/menambah stok saat transaksi (real-time adjustment)
- **Trigger**: Terintegrasi dalam setiap operasi penambahan/pembatalan item

### 3. **Manajemen Toko (Optional - Future)**
- **Deskripsi**: Pihak yang mungkin akan membutuhkan laporan atau analitik penjualan
- **Catatan**: Saat ini tidak diimplementasikan dalam versi CLI, tetapi arsitektur memungkinkan ekspansi ke fitur reporting

---

## 4. FITUR UTAMA & SPESIFIKASI KEBUTUHAN (Core Features & Requirements)

### Fitur 1: Lihat Katalog Barang (View Product Catalog)
**ID Fitur**: F1  
**Trigger**: Menu Opsi 1  
**Deskripsi**: Menampilkan daftar lengkap semua produk yang tersedia di toko dengan detail lengkap

**Input**:  
- Tidak ada input dari pengguna (tampilan saja)

**Proses**:  
1. Ambil seluruh data produk dari InventoryManager menggunakan `getAllProducts()`
2. Iterasi setiap produk dan format tampilannya dalam tabel terstruktur
3. Tampilkan dalam format kolom: ID | Nama Produk | Harga Satuan | Stok

**Output**:  
```
================= KATALOG BARANG ==================
ID    | Nama Produk          | Harga Satuan  | Stok
---------------------------------------------------
P01   | Buku Tulis A5        | Rp    5,000.00| 30
P02   | Pensil 2B            | Rp    2,500.00| 50
P03   | Penghapus Karet      | Rp    1,500.00| 20
...
===================================================
```

**Validasi**:  
- Tidak ada validasi khusus (read-only operation)

**Penanganan Error**:  
- Jika katalog kosong, tetap menampilkan header tabel kosong

---

### Fitur 2: Tambah Barang ke Keranjang (Add Item to Cart)
**ID Fitur**: F2  
**Trigger**: Menu Opsi 2  
**Deskripsi**: Memungkinkan operator kasir untuk menambahkan barang ke keranjang belanja aktif dengan validasi stok

**Input**:  
- `ID Produk` (String): ID unik barang yang ingin ditambahkan
- `Jumlah Beli` (Integer): Kuantitas barang yang ingin dibeli

**Proses**:  
1. Validasi ID Produk:
   - Cari produk di InventoryManager menggunakan `cariBarang(id)` → O(1) lookup
   - Jika produk tidak ditemukan, tampilkan error dan hentikan proses
2. Validasi Jumlah Beli:
   - Jika jumlah ≤ 0, tampilkan error
3. Validasi Stok:
   - Bandingkan jumlah beli dengan stok tersedia di database
   - Jika stok tidak cukup, tampilkan error dengan stok saat ini
4. Proses Penambahan:
   - Kurangi stok produk di InventoryManager secara real-time: `product.setStok(stok - jumlah)`
   - Buat objek CartItem baru dengan produk dan jumlah
   - Push CartItem ke Stack keranjang belanja
5. Konfirmasi:
   - Tampilkan pesan sukses dengan detail barang dan jumlah

**Output**:  
```
Masukkan ID Produk: P01
Masukkan Jumlah Beli: 5
-> Sukses: Buku Tulis A5 (5 pcs) masuk ke keranjang.
```

**Validasi**:  
- ID produk harus ada di katalog
- Jumlah beli harus angka positif (> 0)
- Stok produk harus ≥ jumlah beli yang diminta

**Penanganan Error**:  
- Produk tidak ditemukan → "Error: ID Produk tidak ditemukan!"
- Input bukan angka → "Error: Input jumlah harus berupa angka bulat!"
- Stok tidak cukup → "Error: Stok tidak cukup! (Stok saat ini: N)"

---

### Fitur 3: Void/Undo Barang Terakhir (Undo Last Item)
**ID Fitur**: F3  
**Trigger**: Menu Opsi 3  
**Deskripsi**: Membatalkan penambahan barang terakhir ke keranjang dan mengembalikan stok ke inventori

**Input**:  
- Tidak ada input dari pengguna (otomatis mengambil item paling atas Stack)

**Proses**:  
1. Cek apakah keranjang kosong menggunakan `isEmpty()`
   - Jika kosong, tampilkan error dan hentikan
2. Pop item terakhir dari Stack: `cart.pop()` → O(1) operation
3. Ambil informasi produk dari CartItem yang di-pop
4. Kembalikan stok ke InventoryManager:
   - Cari produk di database: `im.cariBarang(productId)`
   - Tambahkan stok kembali: `product.setStok(currentStok + jumlahBeli)`
5. Konfirmasi:
   - Tampilkan pesan sukses dengan nama barang dan jumlah yang dibatalkan

**Output**:  
```
-> Sukses: Void berhasil. Barang [Buku Tulis A5] sebanyak 5 pcs dikembalikan ke katalog.
```

**Validasi**:  
- Keranjang tidak boleh kosong

**Penanganan Error**:  
- Keranjang kosong → "Error: Tidak ada barang di keranjang untuk di-void!"

---

### Fitur 4: Lihat Keranjang Belanja Aktif (View Shopping Cart)
**ID Fitur**: F4  
**Trigger**: Menu Opsi 4  
**Deskripsi**: Menampilkan detail lengkap keranjang belanja yang sedang aktif, termasuk item, harga, dan subtotal

**Input**:  
- Tidak ada input dari pengguna (tampilan saja)

**Proses**:  
1. Ambil semua item dari Stack keranjang menggunakan `getCartItems()`
2. Hitung total belanja dengan iterasi: `sum(item.getSubtotal())`
3. Format dan tampilkan setiap item dalam tabel
4. Tampilkan total harga belanja

**Output**:  
```
=============== KERANJANG BELANJA AKTIF ===========
Item 1 | Buku Tulis A5        x 5   (Rp    5,000.00) -> Subtotal: Rp   25,000.00
Item 2 | Pensil 2B            x 10  (Rp    2,500.00) -> Subtotal: Rp   25,000.00
--------------------------------------------------
TOTAL HARGA: Rp 50,000.00
===================================================
```

**Validasi**:  
- Tidak ada validasi khusus

**Penanganan Error**:  
- Jika keranjang kosong, tampilkan "Keranjang belanja kosong"

---

### Fitur 5: Selesai Belanja / Checkout (Checkout to Payment Queue)
**ID Fitur**: F5  
**Trigger**: Menu Opsi 5  
**Deskripsi**: Mengakhiri transaksi belanja aktif dan memasukkan ke antrean pembayaran untuk diproses

**Input**:  
- Tidak ada input dari pengguna (otomatis memproses keranjang aktif)

**Proses**:  
1. Validasi:
   - Cek apakah keranjang kosong menggunakan `isEmpty()`
   - Jika kosong, tampilkan error dan hentikan
2. Buat Transaksi Baru:
   - Generate ID transaksi: `TXN-XXXX` (format dengan counter incrementing)
   - Buat objek Transaction baru dengan ID dan seluruh CartItems dari Stack
   - Transaction constructor otomatis menghitung total harga dari semua items
3. Enqueue:
   - Masukkan Transaction ke dalam PaymentQueue
4. Reset:
   - Buat ShoppingCart baru yang kosong untuk pelanggan berikutnya
5. Konfirmasi:
   - Tampilkan pesan sukses dengan ID transaksi yang dibuat

**Output**:  
```
-> Sukses: Transaksi TXN-0001 berhasil didaftarkan ke antrean kasir.
```

**Validasi**:  
- Keranjang tidak boleh kosong

**Penanganan Error**:  
- Keranjang kosong → "Error: Tidak dapat checkout karena keranjang belanja kosong!"

---

### Fitur 6: Proses Pembayaran Antrean (Process Payment Queue)
**ID Fitur**: F6  
**Trigger**: Menu Opsi 6  
**Deskripsi**: Memproses transaksi paling awal dalam antrean pembayaran dan mencetak invoice final

**Input**:  
- Tidak ada input dari pengguna (otomatis mengambil transaksi terdepan dalam Queue)

**Proses**:  
1. Validasi:
   - Cek apakah antrean pembayaran kosong menggunakan `isEmpty()`
   - Jika kosong, tampilkan pesan tidak ada transaksi dan hentikan
2. Dequeue:
   - Pop/ambil transaksi paling awal dari Queue: `dequeue()` → O(1) operation
3. Ekstrak Data Transaksi:
   - Ambil ID transaksi
   - Ambil daftar semua items yang dibeli
   - Hitung total harga dari semua item
4. Format Invoice:
   - Cetak header invoice dengan ID transaksi
   - Cetak daftar item dengan format: "Nama Barang x Qty (Harga Satuan) → Subtotal"
   - Cetak garis pemisah
   - Cetak total harga pembayaran
   - Cetak status "LUNAS"
5. Konfirmasi:
   - Tampilkan pesan sukses bahwa pembayaran telah diproses

**Output**:  
```
==================================================
                   INVOICE PEMBAYARAN
==================================================
Transaksi ID : TXN-0001
Jam           : [Timestamp]
--------------------------------------------------
Buku Tulis A5        x 5   (Rp    5,000.00) -> Subtotal: Rp   25,000.00
Pensil 2B            x 10  (Rp    2,500.00) -> Subtotal: Rp   25,000.00
--------------------------------------------------
TOTAL BAYAR  : Rp   50,000.00
STATUS       : LUNAS
==================================================
```

**Validasi**:  
- Antrean tidak boleh kosong

**Penanganan Error**:  
- Antrean kosong → "Error: Tidak ada transaksi untuk diproses!"

---

### Fitur 7: Keluar Program (Exit Application)
**ID Fitur**: F7  
**Trigger**: Menu Opsi 7  
**Deskripsi**: Menutup aplikasi POS dengan aman

**Input**:  
- Tidak ada input khusus

**Proses**:  
1. Tampilkan pesan penutupan
2. Set flag `running = false` untuk keluar dari loop utama
3. Close Scanner resource

**Output**:  
```
Keluar dari program POS. Terima kasih!
```

---

## 5. ALUR SISTEM & LOGIKA BISNIS (User Flow / Business Logic)

### Alur Umum Operasi POS (Happy Path)

```
START
  │
  ├─→ [Inisialisasi Sistem]
  │   ├─ Buat InventoryManager (HashMap kosong)
  │   ├─ Tambah 5 produk dummy ke katalog
  │   ├─ Buat PaymentQueue kosong
  │   ├─ Buat ShoppingCart kosong untuk pelanggan pertama
  │   └─ Tampilkan header "SISTEM KASIR POS CLI"
  │
  ├─→ [Loop Utama - Menunggu Input Menu]
  │   │
  │   ├─ CASE 1: Lihat Katalog
  │   │   └─→ Iterasi HashMap.values() → Tampilkan semua produk
  │   │
  │   ├─ CASE 2: Tambah Barang ke Keranjang
  │   │   ├─→ Input: ID Produk + Jumlah Beli
  │   │   ├─→ Lookup di InventoryManager O(1) → Validasi stok
  │   │   ├─→ Kurangi stok real-time di database
  │   │   ├─→ Push CartItem ke Stack keranjang
  │   │   └─→ Konfirmasi sukses
  │   │
  │   ├─ CASE 3: Void/Undo Barang Terakhir
  │   │   ├─→ Pop CartItem dari Stack O(1)
  │   │   ├─→ Kembalikan stok ke InventoryManager
  │   │   └─→ Konfirmasi pembatalan
  │   │
  │   ├─ CASE 4: Lihat Keranjang Belanja
  │   │   ├─→ Iterasi Stack keranjang
  │   │   ├─→ Hitung total = sum(subtotal setiap item)
  │   │   └─→ Tampilkan detail lengkap
  │   │
  │   ├─ CASE 5: Checkout (Masuk Antrean Kasir)
  │   │   ├─→ Validasi keranjang tidak kosong
  │   │   ├─→ Generate ID Transaksi TXN-XXXX
  │   │   ├─→ Buat Transaction object dari Stack keranjang
  │   │   ├─→ Enqueue ke PaymentQueue (FIFO)
  │   │   ├─→ Buat ShoppingCart baru kosong
  │   │   └─→ Konfirmasi masuk antrean
  │   │
  │   ├─ CASE 6: Proses Pembayaran Antrean Terdepan
  │   │   ├─→ Validasi antrean tidak kosong
  │   │   ├─→ Dequeue transaksi terdepan O(1)
  │   │   ├─→ Ekstrak item dan total harga
  │   │   ├─→ Format dan cetak INVOICE
  │   │   └─→ Konfirmasi pembayaran selesai
  │   │
  │   └─ CASE 7: Keluar Program
  │       ├─→ Set running = false
  │       └─→ Close Scanner resource
  │
  └─→ END

```

### Skenario Transaksi Lengkap (End-to-End)

**Pelanggan 1: Membeli 2 jenis barang**

```
STEP 1: Lihat Katalog (Menu 1)
├─ Tampilkan semua 5 produk dummy
├─ Pelanggan tertarik dengan P01 (Buku) dan P02 (Pensil)

STEP 2: Tambah P01 ke Keranjang (Menu 2)
├─ Input: ID = "P01", Qty = 5
├─ Lookup InventoryManager.get("P01") → Product found (O(1))
├─ Validasi stok: 30 >= 5 ✓
├─ Reduce stok: 30 - 5 = 25 (real-time)
├─ Stack.push(CartItem[P01, 5])
├─ Konfirmasi: "Sukses: Buku Tulis A5 (5 pcs) masuk ke keranjang"

STEP 3: Tambah P02 ke Keranjang (Menu 2)
├─ Input: ID = "P02", Qty = 10
├─ Lookup InventoryManager.get("P02") → Product found (O(1))
├─ Validasi stok: 50 >= 10 ✓
├─ Reduce stok: 50 - 10 = 40 (real-time)
├─ Stack.push(CartItem[P02, 10])
├─ Konfirmasi: "Sukses: Pensil 2B (10 pcs) masuk ke keranjang"

STEP 4: Lihat Keranjang (Menu 4)
├─ Item 1: Buku Tulis A5 x 5 (Rp 5,000) → Subtotal: Rp 25,000
├─ Item 2: Pensil 2B x 10 (Rp 2,500) → Subtotal: Rp 25,000
├─ TOTAL: Rp 50,000

STEP 5: Checkout (Menu 5)
├─ Validasi keranjang not empty ✓
├─ Generate txnId = "TXN-0001"
├─ Create Transaction(TXN-0001, [CartItem1, CartItem2])
│  └─ Transaction.totalHarga = 25,000 + 25,000 = Rp 50,000
├─ Queue.enqueue(txn) → masuk ke antrean pembayaran
├─ currentCart = new ShoppingCart() → reset untuk pelanggan berikutnya
├─ Konfirmasi: "Sukses: Transaksi TXN-0001 berhasil didaftarkan"

STEP 6: Proses Pembayaran (Menu 6)
├─ Queue.dequeue() → ambil TXN-0001 dari terdepan (FIFO)
├─ Print INVOICE:
│  │
│  ├─ ==================================================
│  ├─                  INVOICE PEMBAYARAN
│  ├─ ==================================================
│  ├─ Transaksi ID : TXN-0001
│  ├─ --------------------------------------------------
│  ├─ Buku Tulis A5 x 5 (Rp 5,000.00) → Rp 25,000.00
│  ├─ Pensil 2B x 10 (Rp 2,500.00) → Rp 25,000.00
│  ├─ --------------------------------------------------
│  ├─ TOTAL BAYAR: Rp 50,000.00
│  ├─ STATUS: LUNAS
│  └─ ==================================================
├─ Konfirmasi: Pembayaran selesai

SELESAI - Siap untuk pelanggan berikutnya
```

---

## 6. ARSITEKTUR TEKNIS & DEPENDENSI (Technical Architecture & Stack)

### 6.1 Teknologi yang Digunakan

| Komponen | Teknologi | Versi | Keterangan |
|----------|-----------|-------|-----------|
| **Bahasa Pemrograman** | Java | Java 8+ | Pure Java, no external dependencies |
| **Build Tool** | N/A | N/A | Compiled dengan `javac`, run dengan `java` |
| **UI/UX** | Command Line Interface (CLI) | Text-based | Scanner input, System.out output |
| **Database** | In-Memory (No persistence) | N/A | Data hilang saat program ditutup |

### 6.2 Struktur Data Utama

| Struktur | Implementasi | Use Case | Time Complexity | Space |
|----------|--------------|----------|-----------------|-------|
| **Product Catalog** | HashMap<String, Product> | Pencarian produk by ID | O(1) avg, O(N) worst | O(N) |
| **Shopping Cart** | Stack<CartItem> | LIFO undo/void operation | O(1) push/pop | O(N) |
| **Payment Queue** | Queue<Transaction> | FIFO pembayaran | O(1) enqueue/dequeue | O(M) |

**Penjelasan Kompleksitas**:
- **HashMap**: Menggunakan hash function untuk instant lookup ID produk tanpa iterasi linear
- **Stack**: Push/Pop di top adalah O(1); ideal untuk fitur undo/void
- **Queue**: FIFO order memastikan pelanggan dilayani dengan urutan yang adil

### 6.3 Arsitektur Kelas

```
┌─────────────────────────────────────────────────────┐
│                    MAIN CLASS                       │
│  - Entry Point                                      │
│  - Menu Loop & User Input Handling                  │
│  - CLI Formatter & Clear Screen                     │
└──────────────────┬──────────────────────────────────┘
                   │ uses
        ┌──────────┴───────────┬───────────┐
        │                      │           │
        ▼                      ▼           ▼
┌──────────────────┐  ┌──────────────────┐  ┌──────────────┐
│ InventoryManager │  │ ShoppingCart     │  │ PaymentQueue │
│ ─────────────────│  │ ─────────────────│  │ ──────────────│
│ - HashMap<K,V>  │  │ - Stack<Item>    │  │ - Queue<Txn> │
│ - cariBarang()  │  │ - addItem()      │  │ - enqueue()  │
│ - tambahBarang()│  │ - undoLastItem() │  │ - dequeue()  │
│ - getAllProd()  │  │ - tampilkan()    │  │              │
└──────────────────┘  │ - isEmpty()      │  └──────────────┘
        △             │ - getCartItems() │
        │             └──────────────────┘
        │ manages              │ contains
        │                      ▼
        │             ┌──────────────────┐
        │             │   CartItem       │
        │             │ ─────────────────│
        │             │ - product        │
        │             │ - jumlahBeli     │
        │             │ - getSubtotal()  │
        │             └──────────────────┘
        │                     │ wraps
        │                     ▼
        └──────────── ┌──────────────────┐
                     │    Product       │
                     │ ─────────────────│
                     │ - id             │
                     │ - nama           │
                     │ - harga          │
                     │ - stok           │
                     │ - getters/setters│
                     └──────────────────┘
```

### 6.4 Kebutuhan Non-Fungsional

#### Keamanan
- **Validasi Input**: Semua input user divalidasi sebelum diproses
  - Tipe data: String untuk ID, Integer untuk Qty
  - Range: Quantity harus > 0
  - Keberadaan: ID produk harus ada di katalog
- **Proteksi Stok**: Stok tidak boleh negatif; validasi real-time sebelum transaksi
- **Konsistensi Data**: Stok berkurang di inventory saat item ditambah, dikembalikan saat di-void

#### Performa
- **Pencarian Produk**: HashMap O(1) → responsif untuk katalog besar (hingga 10,000+ produk)
- **Operasi Keranjang**: Stack O(1) push/pop → instant, bahkan dengan 1000+ items
- **Antrean Pembayaran**: Queue O(1) → proses pembayaran tidak tergantung jumlah transaksi

#### Usability
- **Clear Screen**: Cross-platform clear screen (Windows: `cmd cls`, Unix: ANSI escape)
- **Menu Navigation**: Simple numbered menu (1-7) dengan feedback untuk setiap aksi
- **Status Display**: Real-time status bar menampilkan jumlah item keranjang & ukuran antrean
- **Error Handling**: Pesan error yang jelas dan actionable untuk user

#### Maintainability
- **OOP Principles**: Separasi concern ke dalam kelas-kelas terpisah (SRP)
- **Dokumentasi Kode**: Comments detail untuk setiap struktur data & kompleksitas algoritma
- **Exception Handling**: Try-catch untuk input parsing dengan pesan error yang helpful

---

## 7. SKEMA DATA (Data Model)

### Entity Relationship Diagram

```
                    ┌─────────────────┐
                    │   InventoryDB   │
                    │  (HashMap)      │
                    └────────┬────────┘
                             │
                 ┌───────────┴───────────┐
                 │                       │
                 ▼                       ▼
            [P01]─────────────────   [P02]───────────
            Product                  Product
            ├─ id: "P01"              ├─ id: "P02"
            ├─ nama: "Buku Tulis A5"  ├─ nama: "Pensil 2B"
            ├─ harga: 5000.0          ├─ harga: 2500.0
            └─ stok: 25 (after sale)  └─ stok: 40 (after sale)

    ┌──────────────────────┐
    │  ShoppingCart        │
    │  (Stack<CartItem>)   │
    └──────────┬───────────┘
               │
        ┌──────┴──────┐
        │             │
        ▼             ▼
    [CartItem-1]   [CartItem-2]
    ├─ product ─→ Product P01
    ├─ jmlBeli: 5
    └─ subtotal: 25000.0

    [CartItem-2]
    ├─ product ─→ Product P02
    ├─ jmlBeli: 10
    └─ subtotal: 25000.0

    ┌──────────────────────┐
    │  PaymentQueue        │
    │  (Queue<Transaction>)│
    └──────────┬───────────┘
               │
               ▼
        [Transaction-1]
        ├─ id: "TXN-0001"
        ├─ items: [CartItem-1, CartItem-2]
        └─ totalHarga: 50000.0
```

### 7.1 Product (Model Data Produk)

```java
class Product {
    String id              // Unique identifier (e.g., "P01")
    String nama            // Product name (e.g., "Buku Tulis A5")
    double harga           // Unit price (e.g., 5000.0)
    int stok               // Current stock (e.g., 30)
}
```

**Catatan Penting**: Stok adalah **mutable** dan berubah real-time saat transaksi terjadi.

### 7.2 CartItem (Item di Keranjang Belanja)

```java
class CartItem {
    Product product        // Reference ke produk yang dibeli
    int jumlahBeli         // Quantity (e.g., 5)
    
    // Computed field (calculated on-the-fly)
    double getSubtotal() { 
        return product.harga * jumlahBeli  // e.g., 5000 * 5 = 25000
    }
}
```

### 7.3 Transaction (Model Transaksi Pembayaran)

```java
class Transaction {
    String transactionId   // Unique ID (e.g., "TXN-0001")
    List<CartItem> items   // Snapshot dari items di keranjang
    double totalHarga      // Total pembayaran (calculated)
}
```

**Snapshot**: Transaction menyimpan **kopian** CartItems dari Stack (convert Stack → ArrayList) untuk record permanen transaksi.

### 7.4 Sample Data Initialization

```java
// Produk dummy yang diinisialisasi saat startup
inventory.tambahBarang(new Product("P01", "Buku Tulis A5", 5000, 30));
inventory.tambahBarang(new Product("P02", "Pensil 2B", 2500, 50));
inventory.tambahBarang(new Product("P03", "Penghapus Karet", 1500, 20));
inventory.tambahBarang(new Product("P04", "Penggaris Besi 30cm", 4500, 15));
inventory.tambahBarang(new Product("P05", "Spidol Papan Tulis", 8000, 10));
```

---

## 8. ASUMSI & PERTANYAAN TERBUKA (Assumptions & Open Questions)

### 8.1 Asumsi yang Dibuat Saat Analisis

| # | Asumsi | Justifikasi |
|---|--------|-------------|
| 1 | **Data Persistent**: Program tidak menyimpan transaksi setelah ditutup | Tidak ada file I/O atau database external di kode |
| 2 | **Single Cashier**: Hanya 1 operator kasir yang menggunakan sistem pada saat yang sama | Tidak ada multi-threading atau concurrent access handling |
| 3 | **No Authentication**: Tidak ada login atau role-based access control | Menu terbuka untuk semua user tanpa validasi |
| 4 | **Fixed Product Catalog**: Katalog produk dimulai dari 5 produk dummy yang hardcoded | Tidak ada fitur add/edit/delete produk saat runtime |
| 5 | **Real-time Stok Decrement**: Stok berkurang segera saat item ditambah ke keranjang (bukan saat checkout) | Logika pada line 190: `p.setStok(p.getStok() - item.getJumlahBeli())` |
| 6 | **No Discount/Tax**: Harga akhir = sum(quantity × unit price) tanpa pajak atau diskon | Tidak ada logic perhitungan pajak atau promo |
| 7 | **Currency is IDR (Rp)**: Simbol mata uang hard-coded sebagai "Rp" dalam format print | Format output menggunakan locale IDR |
| 8 | **LIFO Void**: Void selalu membatalkan item terakhir yang ditambahkan | Operasi `pop()` pada Stack, bukan pilih item spesifik |

### 8.2 Pertanyaan Strategis untuk Engineering Team

#### **Pertanyaan 1: Data Persistence**
> **Q**: Apakah sistem perlu menyimpan transaksi history ke file/database setelah program ditutup?  
> **Analisis**: Saat ini semua data hilang setelah exit. Untuk production use, mungkin diperlukan logging transaksi.  
> **Opsi A**: Simpan ke file CSV/JSON setelah setiap transaksi ✓ Audit trail  
> **Opsi B**: Koneksi ke database relasional (MySQL/PostgreSQL) untuk scalability  
> **Opsi C**: Keep in-memory untuk demo/testing saja

#### **Pertanyaan 2: Multi-Cashier Support**
> **Q**: Apakah sistem akan mendukung multiple kasir bekerja bersamaan di shift yang sama?  
> **Analisis**: Kode saat ini hanya support 1 instance ShoppingCart & 1 Scanner. Untuk > 1 kasir perlu:  
> - Separate ShoppingCart per cashier
> - Thread-safe data structures (ConcurrentHashMap, synchronized collections)
> - Centralized PaymentQueue untuk antrean bersama

#### **Pertanyaan 3: Product Catalog Management**
> **Q**: Bagaimana cara menambah/mengedit/menghapus produk saat sistem running?  
> **Analisis**: Saat ini hanya hardcoded 5 produk di startup. Untuk operasional long-term:  
> - Tambah menu "Admin Mode" untuk CRUD produk
> - Load katalog dari file eksternal saat startup
> - Real-time inventory sync dengan sistem gudang

#### **Pertanyaan 4: Stok Real-Time Behavior**
> **Q**: Jika customer membatalkan checkout SETELAH ditambahkan ke keranjang (kira-kira item sudah di-void), apakah stok sudah dikembalikan?  
> **Analisis**: YA, stok dikembalikan otomatis via `undoLastItem()` yang memanggil `im.cariBarang()` dan `setStok()`. Logika ini solid.  
> **Clarification**: Tapi gimana jika customer tidak jadi bayar di antrean kasir? Stok sudah dipotong tapi transaksi dibatalkan?

#### **Pertanyaan 5: Payment Processing & Currency Exchange**
> **Q**: Apakah sistem perlu support payment methods (cash, credit, transfer)?  
> **Analisis**: Saat ini hanya hardcoded "LUNAS" di invoice, tidak ada payment method atau change calculation.  
> **Requirement**: Tambahkan kemampuan:
> - Pilih payment method (Cash, Debit, E-Wallet)
> - Hitung kembalian jika payment > total harga
> - Reconciliation kasir di akhir shift

#### **Pertanyaan 6: Discounts, Tax & Promos**
> **Q**: Bagaimana jika sistem perlu support diskon per item, tax, atau promo bundling?  
> **Analisis**: Tidak ada logic untuk pajak/diskon di Transaction class. Harus extend CartItem/Transaction:
> ```java
> class CartItem {
>     double discountPercentage;  // e.g., 10%
>     double taxRate;             // e.g., 10% PPN
>     
>     public double getSubtotalAfterDiscount() {
>         return (product.harga * jumlahBeli) * (1 - discountPercentage/100);
>     }
>     
>     public double getTotalWithTax() {
>         return getSubtotalAfterDiscount() * (1 + taxRate/100);
>     }
> }
> ```

#### **Pertanyaan 7: Invoice & Receipts**
> **Q**: Apakah invoice perlu diprint ke thermal printer atau disimpan sebagai PDF?  
> **Analisis**: Saat ini print langsung ke Console. Untuk toko fisik:
> - Integrasi thermal printer library (e.g., Java Thermal Printer)
> - Generate PDF receipt via iText atau Apache PDFBox
> - Email receipt ke customer

#### **Pertanyaan 8: Error Recovery & Rollback**
> **Q**: Jika terjadi crash saat processing transaksi, bagaimana stok di-recover?  
> **Analisis**: Saat ini tidak ada error recovery. Kita perlu:
> - Transaction log untuk rollback
> - Atomic operations (all-or-nothing commitment)
> - Database transactions untuk consistency

#### **Pertanyaan 9: Reporting & Analytics**
> **Q**: Apakah sistem perlu support laporan penjualan harian, mingguan, atau top products?  
> **Analisis**: Tidak ada reporting feature. Untuk management dashboard:
> - Daily sales summary (total revenue, jumlah transaksi)
> - Product performance (most sold, least sold)
> - Cashier performance metrics
> - Inventory turnover analysis

#### **Pertanyaan 10: Security & Access Control**
> **Q**: Bagaimana user authentication dan role-based access?  
> **Analisis**: Tidak ada. Untuk live POS:
> - Cashier login dengan username/password
> - Manager login untuk access admin features
> - Audit log setiap aksi (siapa, kapan, apa)
> - Pin pad untuk void/refund (requires approval)

---

## 9. ROADMAP IMPLEMENTASI & REKOMENDASI

### Fase 1: MVP (Current State)
✅ **Selesai** - Sistem dasar sudah implementasi dengan optimal

### Fase 2: Production Hardening (3-4 minggu)
- [ ] Tambahkan file persistence (JSON/CSV logging)
- [ ] Implementasi proper exception handling & retry logic
- [ ] Tambahkan input validation yang lebih robust
- [ ] Performance testing dengan 10,000+ products

### Fase 3: Feature Enhancement (2-3 bulan)
- [ ] Tambah support untuk payment methods & change calculation
- [ ] Implementasi discount & tax calculation
- [ ] Tambah basic reporting & daily reconciliation
- [ ] Admin panel untuk product management

### Fase 4: Scalability & Integration (3-6 bulan)
- [ ] Migrate ke database relasional
- [ ] Support multi-cashier dengan synchronization
- [ ] Integrasi thermal printer & payment gateway
- [ ] API untuk integration dengan backend system
- [ ] Mobile app untuk cashier operations

---

## 10. KESIMPULAN

Sistem POS CLI yang Anda bangun adalah **demonstrasi excellent** dari implementasi praktis struktur data fundamental dalam konteks bisnis real-world. Penggunaan HashMap, Stack, dan Queue menunjukkan deep understanding tentang:

✅ **Time Complexity Awareness** - Setiap struktur data dipilih untuk optimal performance  
✅ **OOP Principles** - Separasi concern, encapsulation, dan reusability  
✅ **Real-time Consistency** - Stok management yang solid dan tidak ambigu  
✅ **User Experience** - CLI yang intuitif dengan clear feedback  

Produk ini **siap untuk expansion** ke fitur-fitur production-grade dengan roadmap yang jelas di atas.

---

**Document prepared by**: Senior Technical Product Manager  
**Reverse-Engineering Date**: Juni 2026  
**Code Review Status**: Approved for Analysis

Yes