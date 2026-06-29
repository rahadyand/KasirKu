# KASIRKU - Point of Sale System
## Dokumentasi Struktur Proyek (Refactored)

---

## 📁 STRUKTUR FILE

```
kasirku-pos/
│
├── Models/
│   ├── Product.java              # Model data produk/barang
│   ├── CartItem.java             # Model item dalam keranjang belanja
│   └── Transaction.java          # Model transaksi pembayaran
│
├── Managers/
│   ├── InventoryManager.java     # Manager stok (HashMap - O(1) search)
│   ├── ShoppingCart.java         # Manager keranjang (Stack - LIFO)
│   └── PaymentQueue.java         # Manager antrean pembayaran (Queue - FIFO)
│
├── Database/
│   └── DatabaseInitializer.java  # Inisialisasi data dummy produk
│
└── Main.java                     # Entry point & CLI interface

```

---

## 📋 PENJELASAN SETIAP FILE

### 1. **Product.java** (Model Data Produk)
- **Tujuan**: Merepresentasikan objek produk di sistem POS
- **Atribut**: 
  - `id` : Kode unik produk
  - `nama` : Nama produk
  - `harga` : Harga satuan
  - `stok` : Jumlah stok tersedia
- **Metode**: Getter & Setter untuk semua atribut

---

### 2. **CartItem.java** (Model Item Keranjang)
- **Tujuan**: Merepresentasikan item dalam keranjang belanja
- **Atribut**:
  - `product` : Referensi ke objek Product
  - `jumlahBeli` : Kuantitas yang dibeli
- **Metode**:
  - `getSubtotal()` : Menghitung subtotal item (harga × qty)

---

### 3. **Transaction.java** (Model Transaksi)
- **Tujuan**: Merepresentasikan satu transaksi pembayaran
- **Atribut**:
  - `transactionId` : Kode transaksi unik
  - `items` : List CartItem dalam transaksi
  - `totalHarga` : Total pembayaran
- **Fungsi**: Menyimpan riwayat belanja untuk invoice

---

### 4. **InventoryManager.java** (Manager Stok - HashMap)
- **Tujuan**: Mengelola database produk & stok
- **Struktur Data**: `HashMap<String, Product>`
  - Key: ID Produk
  - Value: Objek Product
- **Kompleksitas Waktu**:
  - Pencarian (get): **O(1)** rata-rata, O(N) worst case
  - Penambahan (put): **O(1)** rata-rata
- **Metode Utama**:
  - `tambahBarang(Product)` : Menambah produk ke inventory
  - `cariBarang(String id)` : Mencari produk berdasarkan ID
  - `getAllProducts()` : Mendapatkan semua produk

---

### 5. **ShoppingCart.java** (Manager Keranjang - Stack)
- **Tujuan**: Mengelola keranjang belanja aktif pelanggan
- **Struktur Data**: `Stack<CartItem>`
  - Prinsip: LIFO (Last In First Out)
- **Kompleksitas Waktu**:
  - Push (tambah item): **O(1)** rata-rata, O(N) worst case (saat resize)
  - Pop (undo item): **O(1)** konstan
- **Metode Utama**:
  - `addItem(CartItem)` : Menambah item ke keranjang
  - `undoLastItem()` : Membatalkan item terakhir (Void/Undo)
  - `tampilkanKeranjang()` : Menampilkan isi keranjang
  - `isEmpty()` : Cek apakah keranjang kosong

---

### 6. **PaymentQueue.java** (Manager Antrean - Queue)
- **Tujuan**: Mengelola antrean pembayaran di kasir
- **Struktur Data**: `Queue<Transaction>`
  - Prinsip: FIFO (First In First Out)
  - Implementasi: LinkedList
- **Kompleksitas Waktu**:
  - Enqueue (masuk antrean): **O(1)** konstan
  - Dequeue (proses pembayaran): **O(1)** konstan
- **Metode Utama**:
  - `enqueue(Transaction)` : Menambah transaksi ke antrean
  - `dequeue()` : Memproses transaksi terdepan
  - `size()` : Jumlah transaksi dalam antrean
  - `isEmpty()` : Cek apakah antrean kosong

---

### 7. **DatabaseInitializer.java** (Inisialisasi Data)
- **Tujuan**: Mengelola semua data dummy/initial database
- **Keuntungan**: 
  - ✅ Memisahkan data dari logika bisnis
  - ✅ Mudah untuk menambah/mengubah data produk
  - ✅ Kode Main.java lebih bersih & fokus pada UI
- **Metode Utama**:
  - `initializeProducts(InventoryManager)` : Load semua data dummy

---

### 8. **Main.java** (Entry Point & CLI)
- **Tujuan**: Menampilkan UI menu dan mengkoordinasi alur program
- **Komponen**:
  - **Main Method**: Loop utama program
  - **Menu Options**: 7 pilihan operasi
  - **Helper Methods**: Utility methods untuk setiap operasi
  - **UI Helpers**: clearScreen(), pauseAndClear()
- **Alur Program**:
  1. Inisialisasi Managers (InventoryManager, PaymentQueue)
  2. Load data dummy via DatabaseInitializer
  3. Loop menu utama dengan Scanner input
  4. Proses pilihan pengguna sesuai case
  5. Keluar saat user memilih opsi 7

---

## 🎯 KEUNTUNGAN PEMISAHAN FILE

### 1. **Separation of Concerns (SoC)**
- Setiap class punya tanggung jawab tunggal
- Model hanya handle data, Manager handle logika, Main handle UI

### 2. **Mudah di-Maintain**
- Perubahan di satu class tidak mempengaruhi class lain
- Debugging menjadi lebih mudah

### 3. **Reusable Code**
- Class dapat digunakan kembali di modul lain
- Tidak perlu copy-paste kode yang sama

### 4. **Dokumentasi Struktur Data & Algoritma**
- Setiap Manager class memiliki dokumentasi lengkap tentang:
  - Mengapa struktur data itu dipilih
  - Kompleksitas waktu (Time Complexity)
  - Average Case vs Worst Case
  - Trade-offs yang dipertimbangkan

### 5. **Testability**
- Setiap class dapat ditest secara independen (Unit Testing)
- Mempermudah verifikasi fungsionalitas

---

## 🚀 CARA MENGCOMPILE & MENJALANKAN

### Compile Semua File:
```bash
javac *.java
```

### Jalankan Program:
```bash
java Main
```

### Struktur Kompilasi:
```
1. Product.java            ✓
2. CartItem.java           ✓ (depends on Product)
3. Transaction.java        ✓ (depends on CartItem)
4. InventoryManager.java   ✓ (depends on Product)
5. ShoppingCart.java       ✓ (depends on CartItem, InventoryManager)
6. PaymentQueue.java       ✓ (depends on Transaction, CartItem)
7. DatabaseInitializer.java ✓ (depends on InventoryManager, Product)
8. Main.java               ✓ (depends on semua di atas)
```

---

## 📊 RINGKASAN STRUKTUR DATA & KOMPLEKSITAS

| Manager | Struktur | Prinsip | Search | Insert | Delete | Use Case |
|---------|----------|---------|--------|--------|--------|----------|
| InventoryManager | HashMap | Key-Value | O(1) | O(1) | O(1) | Lookup produk by ID |
| ShoppingCart | Stack | LIFO | O(N) | O(1)* | O(1) | Undo/Void terakhir |
| PaymentQueue | Queue | FIFO | O(N) | O(1) | O(1) | Antrean pembayaran |

*O(1) amortized (worst case O(N) saat resize)

---

## 💡 TIPS PENGEMBANGAN

### Jika ingin menambah fitur baru:

1. **Fitur: Search Produk by Nama**
   - Tambah method di `InventoryManager`
   - Tidak perlu modifikasi class lain

2. **Fitur: Diskon/Promo**
   - Tambah field `diskon` di `Product` atau `CartItem`
   - Update `getSubtotal()` di CartItem
   - Update invoice logic di `PaymentQueue.dequeue()`

3. **Fitur: History Transaksi**
   - Tambah `List<Transaction>` di `PaymentQueue`
   - Buat method `displayHistory()`

---

## 📚 REFERENSI PEMBELAJARAN

Kode ini dirancang untuk mengajarkan:
- ✅ OOP Principles (Encapsulation, Single Responsibility)
- ✅ Design Patterns (Manager Pattern, Initialization Pattern)
- ✅ Data Structures (HashMap, Stack, Queue, ArrayList)
- ✅ Time Complexity Analysis (Big-O Notation)
- ✅ CLI Application Development
- ✅ File Organization & Project Structure

---

**Dibuat untuk Tugas Besar Struktur Data & Algoritma**
**Universitas Sebelas Maret - Program Studi Ilmu Komputer**
