# KasirKu - Point of Sale System

**Dokumentasi Akademis untuk Tugas Besar Mata Kuliah Struktur Data dan Algoritma**

---

## I. Judul dan Deskripsi Singkat Proyek

---

**KasirKu** adalah sistem Point of Sale (POS) berbasis Command Line Interface (CLI) yang dirancang untuk mengelola transaksi penjualan di toko retail. Proyek ini dikembangkan sebagai tugas besar mata kuliah Struktur Data dan Algoritma untuk mendemonstrasikan pemahaman mendalam terhadap penggunaan berbagai struktur data yang efisien dalam konteks aplikasi nyata.

Sistem ini memungkinkan kasir untuk melakukan operasi dasar seperti melihat katalog produk, menambahkan barang ke keranjang belanja, membatalkan item terakhir (undo/void), dan memproses pembayaran dengan mekanisme antrian. Seluruh sistem dirancang dengan prinsip Object-Oriented Programming (OOP) dan separation of concerns untuk memastikan kode mudah dipelihara dan dipahami.

**Tujuan Pembelajaran:**
- Memahami penerapan berbagai struktur data (HashMap, Stack, Queue) dalam konteks aplikasi nyata
- Menganalisis time complexity dan mengoptimalkan performa operasi
- Mengimplementasikan design pattern yang baik dalam bahasa Java
- Mengelola inventory stok barang dengan pencarian O(1)
- Menangani operasi LIFO untuk fitur undo
- Menangani operasi FIFO untuk sistem antrian pembayaran

---

## II. Nama Anggota Kelompok

1. **Panji Afan Ridhani** - L0125089
2. **Rahadyan Hafiz Arkana** - L0125130
3. **Daffabian Farel Rizky Putra** - L0125134

---

## III. Penjelasan Fitur-Fitur Utama Program

Program KasirKu menyediakan tujuh fitur utama yang diakses melalui menu interaktif CLI. Berikut adalah penjelasan singkat setiap fitur:

### 1. Lihat Katalog Barang

Menampilkan daftar lengkap semua produk yang tersedia di inventory beserta informasi detail: ID produk, nama, harga satuan (dalam Rupiah), dan stok yang masih tersedia. Data ditampilkan dalam format tabel yang rapi dan terurut berdasarkan ID produk.

**Kegunaan:** Kasir dapat melihat semua barang yang dijual tanpa perlu membuka database secara manual. Informasi stok membantu mengidentifikasi item yang terbatas.

### 2. Tambah Barang ke Keranjang

Fitur ini memungkinkan kasir untuk menambahkan produk ke keranjang belanja aktif pelanggan. Kasir perlu memasukkan ID produk dan jumlah yang dibeli. Sistem akan secara otomatis:
- Mencari produk berdasarkan ID di database inventory
- Memvalidasi ketersediaan stok (pengecekan stok cukup atau tidak)
- Mengurangi jumlah stok dari inventory
- Menambahkan item ke keranjang belanja

**Kegunaan:** Fitur utama untuk memproses pembelian. Validasi stok mencegah overselling.

### 3. Void/Undo Barang Terakhir

Fitur ini memungkinkan kasir untuk membatalkan/menghapus item terakhir yang ditambahkan ke keranjang. Operasi ini berlaku dengan prinsip LIFO (Last In First Out), artinya hanya item terakhir yang bisa dibatalkan. Ketika item dibatalkan:
- Item dikeluarkan dari keranjang
- Stok barang dikembalikan ke inventory

**Kegunaan:** Mengatasi kesalahan input tanpa harus membuat transaksi ulang. Kasir bisa langsung meng-undo kesalahan dengan menekan tombol.

### 4. Lihat Keranjang Belanja Aktif

Menampilkan semua item yang saat ini ada di keranjang belanja beserta detail:
- Nama produk
- Jumlah yang dibeli
- Harga satuan
- Subtotal per item
- Total harga keseluruhan
- Jumlah item dalam keranjang

**Kegunaan:** Kasir dapat memverifikasi bahwa data yang diinput sudah benar sebelum checkout.

### 5. Selesai Belanja (Masuk Antrean Kasir)

Ketika transaksi pelanggan selesai, kasir dapat menyelesaikan belanja dan memasukkan transaksi ke antrean pembayaran. Operasi ini akan:
- Membuat ID transaksi unik (TXN-XXXX)
- Mengumpulkan semua item keranjang dan total harga
- Memasukkan transaksi ke antrean pembayaran
- Membuat keranjang belanja baru untuk pelanggan berikutnya

**Kegunaan:** Menginisialisasi proses pembayaran dan mempersiapkan kasir untuk melayani pelanggan selanjutnya.

### 6. Proses Pembayaran Antrean Terdepan

Fitur ini memproses pembayaran pelanggan yang paling lama menunggu (berdasarkan antrian FIFO). Ketika diaktifkan:
- Sistem mengambil transaksi paling awal dari antrian
- Menampilkan invoice lengkap dengan semua detail belanja
- Mengeluarkan transaksi dari antrian (transaksi selesai)

**Kegunaan:** Melayani antrian pembayaran secara adil dan terukur. Pelanggan yang datang lebih dulu dilayani lebih dulu.

### 7. Keluar Program

Fitur ini mengakhiri program KasirKu. Semua data session akan hilang (program tidak menyimpan data ke disk).

**Kegunaan:** Menutup program dengan aman tanpa meninggalkan thread yang berjalan.

---

## IV. Struktur Data dan Algoritma yang Digunakan

### Struktur Data

#### 1. HashMap (dalam InventoryManager)

**Penggunaan:**
Struktur data HashMap digunakan untuk menyimpan data inventory produk. Setiap produk disimpan dengan pasangan key-value di mana key adalah ID produk (String) dan value adalah objek Product.

```java
private Map<String, Product> databaseBarang;  // HashMap<String, Product>
```

**Alasan Pemilihan:**
- **Pencarian Cepat (O(1)):** HashMap memberikan akses data dalam waktu konstan rata-rata. Ketika kasir ingin mencari produk berdasarkan ID, sistem tidak perlu melakukan iterasi linear seperti array atau linked list. Pencarian langsung dapat dilakukan melalui hash function.
- **Efisiensi untuk Inventory Besar:** Jika inventory berisi ribuan produk, waktu pencarian tetap O(1), sementara linear search akan menjadi O(N) yang jauh lebih lambat.
- **Mudah Dimodifikasi:** Penambahan atau penghapusan produk juga dapat dilakukan dalam O(1).

**Time Complexity:**
- `get(key)`: O(1) rata-rata, O(N) worst case (jika terjadi hash collision ekstrem)
- `put(key, value)`: O(1) rata-rata
- `remove(key)`: O(1) rata-rata

**Worst Case Handling:** Java 8+ mengoptimalkan worst case hash collision dengan menggunakan Red-Black Tree, sehingga worst case complexity berkurang menjadi O(log N).

---

#### 2. Stack (dalam ShoppingCart)

**Penggunaan:**
Struktur data Stack digunakan untuk mengelola keranjang belanja aktif. Setiap item yang ditambahkan ke keranjang disimpan dalam stack, dan operasi pembatalan (undo/void) menggunakan prinsip LIFO.

```java
private Stack<CartItem> cart;  // Stack<CartItem>
```

**Alasan Pemilihan:**
- **Operasi Undo yang Natural:** Dalam konteks kasir, fitur undo/void paling intuitif adalah membatalkan item terakhir yang diinput. Ini adalah definisi exact dari LIFO (Last In First Out). Stack adalah struktur data yang dirancang khusus untuk prinsip ini.
- **Efisiensi Operasi Push/Pop:** Menambah item ke keranjang (push) dan membatalkan item terakhir (pop) keduanya memiliki time complexity O(1) amortized.
- **Tidak Perlu Pencarian:** Tidak seperti array, stack tidak memerlukan pencarian untuk menemukan item yang akan dihapus karena selalu yang terakhir.

**Time Complexity:**
- `push(item)`: O(1) amortized (O(N) worst case saat resize array internal)
- `pop()`: O(1)
- `peek()`: O(1)
- `isEmpty()`: O(1)

**Implementasi Java:** Stack di Java didukung oleh Vector (array dinamis), yang mengalokasikan kapasitas lebih dari yang diperlukan untuk mengoptimalkan operasi push.

---

#### 3. Queue (dalam PaymentQueue)

**Penggunaan:**
Struktur data Queue digunakan untuk mengelola antrian pembayaran. Transaksi yang selesai ditambahkan ke belakang antrian (enqueue), dan pembayaran diproses dari depan antrian (dequeue) dengan prinsip FIFO.

```java
private Queue<Transaction> queue;  // Queue<Transaction> (LinkedList implementation)
```

**Alasan Pemilihan:**
- **Keadilan dalam Melayani:** Dalam sistem kasir nyata, antrian pembayaran harus mengikuti prinsip "siapa datang duluan, dilayani duluan" (FIFO). Queue adalah struktur data yang sempurna untuk kebutuhan ini.
- **Operasi Enqueue/Dequeue Efisien:** Menambah transaksi ke antrian (enqueue) dan mengambil transaksi pertama (dequeue) keduanya O(1).
- **Realistic Simulation:** Implementasi ini mensimulasikan sistem kasir real-world yang menggunakan sistem antrian.

**Time Complexity:**
- `add(element)` (enqueue): O(1)
- `poll()` (dequeue): O(1)
- `isEmpty()`: O(1)
- `size()`: O(1)

**Implementasi Java:** Queue di Java biasanya menggunakan LinkedList sebagai backing implementation untuk operasi O(1) yang konsisten tanpa perlu resize.

---

#### 4. ArrayList (dalam Transaction dan DisplayCatalog)

**Penggunaan:**
ArrayList digunakan untuk menyimpan daftar CartItem dalam satu Transaction, dan juga digunakan untuk menampilkan katalog produk yang sudah di-sort.

```java
List<CartItem> items = new ArrayList<>(cartItems);  // Dalam Transaction
List<Product> sortedProducts = new ArrayList<>(inventory.getAllProducts());  // Dalam displayCatalog
```

**Alasan Pemilihan:**
- **Akses Indeks Cepat:** ArrayList memberikan akses O(1) untuk mengakses elemen berdasarkan index, yang berguna untuk iterasi dalam menampilkan katalog atau detail transaksi.
- **Fleksibilitas Ukuran:** Tidak seperti array primitif, ArrayList dapat berkembang secara dinamis sesuai kebutuhan.
- **Kompatibilitas dengan Collection:** ArrayList kompatibel dengan berbagai method Collection di Java seperti sorting.

**Time Complexity:**
- `get(index)`: O(1)
- `add(element)`: O(1) amortized
- `remove(index)`: O(N) (membutuhkan shifting elemen)
- `sort()`: O(N log N) menggunakan algoritma Timsort

---

### Algoritma

#### 1. Linear Search (dalam InventoryManager)

**Penggunaan:**
Linear search digunakan melalui method `cariBarang(String id)` di InventoryManager untuk mencari produk berdasarkan ID.

```java
public Product cariBarang(String id) {
    return databaseBarang.get(id);  // HashMap internal melakukan hash-based search
}
```

**Catatan Penting:** Meskipun method ini menggunakan operasi `get()`, di balik layar HashMap menggunakan hash-based lookup bukan linear search. Time complexity adalah O(1) rata-rata, bukan O(N).

**Time Complexity:** O(1) rata-rata, O(N) worst case

---

#### 2. Insertion Sort (dalam DisplayCatalog)

**Penggunaan:**
Katalog produk ditampilkan dengan urutan berdasarkan ID produk yang terurut. Ini dilakukan melalui method `sort()` pada ArrayList.

```java
List<Product> sortedProducts = new ArrayList<>(inventory.getAllProducts());
sortedProducts.sort(Comparator.comparing(Product::getId));
```

**Alasan Pemilihan:**
- **Data Kecil hingga Medium:** Dengan maksimal 30 produk dalam inventory, algoritma sorting sederhana sudah cukup efisien.
- **Implementasi Bawaan:** Java menggunakan Timsort (hybrid antara merge sort dan insertion sort) yang optimal untuk data berukuran kecil dengan pola tertentu.

**Time Complexity:** 
- Average case: O(N log N) menggunakan Timsort
- Best case: O(N) jika data sudah terurut
- Worst case: O(N log N)

Dalam konteks ini dengan N=30 produk, perbedaan performa tidak signifikan, tetapi prinsipnya O(N log N) lebih baik daripada O(N²) jika menggunakan bubble sort.

---

#### 3. Traversal (dalam Tampilan Invoice dan Katalog)

**Penggunaan:**
Traversal (iterasi) digunakan untuk menampilkan semua item dalam keranjang, transaksi, dan katalog.

```java
// Iterasi keranjang
for (CartItem item : cart) {
    System.out.println(item);
}

// Iterasi katalog
for (Product p : sortedProducts) {
    System.out.printf("%-5s | %-20s | Rp %,11.2f | %-4d\n", ...);
}
```

**Time Complexity:** O(N) di mana N adalah jumlah item yang ditraversal.

**Alasan:** Tidak ada cara yang lebih efisien untuk menampilkan N item kepada pengguna selain melakukan iterasi satu per satu.

---

#### 4. Validation Logic (dalam AddItemToCart)

**Penggunaan:**
Sebelum menambahkan item ke keranjang, sistem melakukan validasi:
1. Apakah produk dengan ID tersebut ada di inventory
2. Apakah stok cukup untuk jumlah yang diminta

```java
Product p = inventory.cariBarang(id);  // O(1) lookup
if (p == null) {
    // Handle: produk tidak ditemukan
}

if (p.getStok() < item.getJumlahBeli()) {
    // Handle: stok tidak cukup
}
```

**Time Complexity:** O(1) karena hanya melakukan one-time lookup dan numeric comparison.

---

### Ringkasan Perbandingan Struktur Data

| Struktur Data | Lokasi | Push/Add | Pop/Remove | Search | Alasan Pemilihan |
|---|---|---|---|---|---|
| HashMap | InventoryManager | O(1) | O(1) | O(1) | Pencarian produk cepat |
| Stack | ShoppingCart | O(1)* | O(1) | O(N) | Operasi undo LIFO natural |
| Queue | PaymentQueue | O(1) | O(1) | O(N) | Antrian FIFO adil & natural |
| ArrayList | Transaction, Display | O(1)* | O(N) | O(N) | Akses indeks, iterasi |

*Amortized time complexity

---

## V. Panduan Instalasi dan Menjalankan Program

### Prasyarat Sistem

Sebelum menjalankan program, pastikan sistem Anda memiliki:

- **Java Development Kit (JDK)** versi 8 atau lebih tinggi
- **Terminal/Command Prompt** untuk menjalankan perintah
- **Text Editor atau IDE** (Optional, untuk membaca/mengubah kode)

Untuk mengecek versi Java yang terinstal:

```bash
java -version
javac -version
```

---

### Step-by-Step Installation

#### Langkah 1: Persiapkan Struktur Folder

Buat struktur folder sesuai dengan organization yang telah ditentukan:

**Pada Linux/macOS:**

```bash
mkdir -p kasirku-pos/{Models,Managers,Database}
cd kasirku-pos
```

**Pada Windows (Command Prompt):**

```cmd
mkdir kasirku-pos
cd kasirku-pos
mkdir Models
mkdir Managers
mkdir Database
```

**Pada Windows (PowerShell):**

```powershell
mkdir kasirku-pos
cd kasirku-pos
mkdir Models, Managers, Database
```

---

#### Langkah 2: Organisir File Java

Letakkan setiap file Java di folder yang sesuai dengan package-nya:

**Models Package:**
- `Models/Product.java`
- `Models/CartItem.java`
- `Models/Transaction.java`

**Managers Package:**
- `Managers/InventoryManager.java`
- `Managers/ShoppingCart.java`
- `Managers/PaymentQueue.java`

**Database Package:**
- `Database/DatabaseInitializer.java`

**Root Directory:**
- `Main.java`

Struktur akhir folder akan terlihat seperti:

```
kasirku-pos/
├── Models/
│   ├── Product.java
│   ├── CartItem.java
│   └── Transaction.java
├── Managers/
│   ├── InventoryManager.java
│   ├── ShoppingCart.java
│   └── PaymentQueue.java
├── Database/
│   └── DatabaseInitializer.java
└── Main.java
```

---

#### Langkah 3: Compile Program

**Opsi A: Menggunakan Build Script (Recommended)**

Jika menggunakan Linux/macOS dengan file `build.sh`:

```bash
chmod +x build.sh
./build.sh
```

Jika menggunakan Windows dengan file `build.bat`:

```cmd
build.bat
```

**Opsi B: Menggunakan Makefile**

Jika sistem Anda memiliki `make` command:

```bash
make run
```

Atau hanya compile tanpa langsung run:

```bash
make compile
```

**Opsi C: Manual Compilation (Universal)**

Compile secara manual dengan urutan yang benar (penting untuk menghindari dependency errors):

Pertama, compile package Models:

```bash
javac Models/*.java
```

Kedua, compile package Managers (bergantung pada Models):

```bash
javac -cp . Managers/*.java
```

Ketiga, compile package Database (bergantung pada Models dan Managers):

```bash
javac -cp . Database/*.java
```

Keempat, compile Main.java (bergantung pada semua package):

```bash
javac -cp . Main.java
```

**Penjelasan flag `-cp .`:** Flag ini memberitahu compiler untuk mencari file class di current directory (`.`) karena package-package lain sudah dikompilasi.

---

#### Langkah 4: Verifikasi Kompilasi

Pastikan tidak ada error selama proses kompilasi. Jika ada error, periksa:

1. Apakah semua file Java sudah dipindahkan ke folder yang benar
2. Apakah baris pertama setiap file mengandung `package` declaration yang benar
3. Apakah semua import statements sudah ada di file yang membutuhkannya

Untuk memverifikasi bahwa kompilasi berhasil, cek apakah file `.class` sudah dibuat:

```bash
ls -la Models/*.class      # Linux/macOS
ls -la Models\*.class      # Windows Command Prompt
Get-ChildItem Models/*.class  # Windows PowerShell
```

---

#### Langkah 5: Jalankan Program

Setelah kompilasi berhasil, jalankan program dengan:

```bash
java Main
```

Program akan menampilkan menu utama:

```
==================================================
          KasirKu - Point of Sale System          
==================================================

========== MENU UTAMA KasirKu ==========
1. Lihat Katalog Barang
2. Tambah Barang ke Keranjang
3. Void/Undo Barang Terakhir
4. Lihat Keranjang Belanja Aktif
5. Selesai Belanja (Masuk Antrean Kasir)
6. Proses Pembayaran Antrean Terdepan
7. Keluar Program
====================================

[STATUS] Item Keranjang: 0 | Antrean Kasir: 0
Pilih Opsi (1-7):
```

---

### Troubleshooting Umum

**Error: "javac: command not found"**

Solusi: JDK belum terinstal atau tidak ada di PATH sistem. Instal JDK dari [oracle.com/java](https://www.oracle.com/java/) atau gunakan package manager:

- Ubuntu: `sudo apt-get install default-jdk`
- macOS: `brew install openjdk`
- Windows: Download dari official Oracle website

---

**Error: "package Models does not exist"**

Solusi: Pastikan:
1. Folder `Models/` sudah dibuat
2. File `Product.java`, `CartItem.java`, `Transaction.java` ada di folder `Models/`
3. Baris pertama file-file tersebut mengandung `package Models;`
4. Compile urut: Models → Managers → Database → Main

---

**Error: "cannot find symbol: class Product"**

Solusi: Tambahkan import statement di file yang membutuhkannya:

```java
import Models.Product;
```

---

**Error: Main.class not found when running**

Solusi: Pastikan Anda menjalankan program dari root directory `kasirku-pos/`, bukan dari subfolder.

---

## VI. Penggunaan Library Eksternal

Program KasirKu menggunakan **Library Bawaan (Built-in) Java Standard Library** tanpa memerlukan library eksternal tambahan.

Tabel berikut menunjukkan library bawaan yang digunakan:

| Library | Versi | Fungsi dalam Proyek | Langkah Instalasi |
|---------|-------|-------------------|--------------------|
| `java.util` | Java SE 8+ | Menyediakan Collection Framework (HashMap, Stack, Queue, ArrayList, List, Scanner, Comparator) yang merupakan inti dari struktur data program | Included dalam JDK, tidak perlu instalasi tambahan |
| `java.io` | Java SE 8+ | Menangani input/output dasar (tidak digunakan secara eksplisit tetapi tersedia untuk ekspansi future) | Included dalam JDK |

---

### Penjelasan Library yang Digunakan

#### java.util

Library `java.util` adalah bagian dari Java Collection Framework dan menyediakan implementasi struktur data yang kami gunakan:

**Komponen yang Digunakan:**

1. **HashMap** - Implementasi hash table dari interface Map
   - Digunakan dalam: `InventoryManager.java`
   - Import: `import java.util.HashMap;` atau `import java.util.*;`

2. **Stack** - Implementasi struktur data stack (LIFO)
   - Digunakan dalam: `ShoppingCart.java`
   - Import: `import java.util.Stack;` atau `import java.util.*;`

3. **Queue** - Interface untuk operasi queue
   - Digunakan dalam: `PaymentQueue.java`
   - Import: `import java.util.Queue;` atau `import java.util.*;`

4. **LinkedList** - Implementasi linked list yang juga mengimplementasikan Queue
   - Digunakan dalam: `PaymentQueue.java` (sebagai backing implementation untuk Queue)
   - Import: `import java.util.LinkedList;` atau `import java.util.*;`

5. **ArrayList** - Implementasi dynamic array
   - Digunakan dalam: `Transaction.java`, `displayCatalog()` di Main.java
   - Import: `import java.util.ArrayList;` atau `import java.util.*;`

6. **Collection** - Base interface untuk semua collection
   - Digunakan dalam: `InventoryManager.java` untuk method `getAllProducts()`
   - Import: `import java.util.Collection;` atau `import java.util.*;`

7. **Comparator** - Interface untuk mendefinisikan urutan custom
   - Digunakan dalam: `displayCatalog()` di Main.java untuk sorting produk
   - Import: `import java.util.Comparator;` atau `import java.util.*;`

8. **Scanner** - Untuk membaca input dari pengguna
   - Digunakan dalam: `Main.java` untuk menu interaktif
   - Import: `import java.util.Scanner;` atau `import java.util.*;`

---

### Mengapa Tidak Ada Library Eksternal?

Keputusan untuk menggunakan hanya standard library memiliki beberapa alasan:

1. **Tujuan Pembelajaran:** Mata kuliah Struktur Data dan Algoritma bertujuan untuk mengajarkan implementasi dan penggunaan struktur data dasar. Menggunakan library eksternal mungkin tidak menunjukkan pemahaman mendalam.

2. **Kompatibilitas:** Standard library tersedia di semua instalasi Java tanpa konfigurasi tambahan, memastikan program dapat dijalankan di mana saja.

3. **Kesederhanaan:** Program ini adalah demonstration system, bukan production system yang memerlukan library eksternal seperti database driver atau framework web.

4. **Pembelajaran Mendalam:** Dengan hanya menggunakan standard library, developer dapat fokus pada struktur data dan algoritma tanpa distraksi dari framework ecosystem.

---

### Verifikasi Library yang Tersedia

Untuk memverifikasi bahwa library bawaan sudah tersedia, cek dengan perintah:

```bash
javap -classpath $JAVA_HOME/lib java.util.HashMap
javap -classpath $JAVA_HOME/lib java.util.Stack
javap -classpath $JAVA_HOME/lib java.util.Queue
```

Jika output menampilkan class definition, maka library sudah tersedia.

---

### Kesimpulan Library

KasirKu adalah proyek **pure Java** yang mendemonstrasikan penggunaan struktur data Collection Framework dari standard library. Tidak ada perlu instalasi library eksternal, sehingga setup menjadi sederhana dan fokus dapat diarahkan pada pemahaman struktur data dan algoritma itu sendiri.

---

## VII. Kesimpulan

Program KasirKu berhasil mendemonstrasikan penerapan praktis dari berbagai struktur data fundamental dalam konteks aplikasi real-world. Berikut adalah pembelajaran kunci yang dapat diambil:

1. **HashMap** memberikan akses O(1) untuk lookup berdasarkan key, ideal untuk inventory management
2. **Stack** mewujudkan prinsip LIFO yang natural untuk operasi undo/void
3. **Queue** mengimplementasikan FIFO yang adil untuk sistem antrian pembayaran
4. **ArrayList** memberikan akses O(1) berdasarkan index untuk iterasi dan sorting

Seluruh struktur data ini dipilih berdasarkan analisis time complexity dan karakteristik operasi yang dibutuhkan. Program ini menunjukkan bahwa pemilihan struktur data yang tepat adalah kunci untuk mengoptimalkan performa aplikasi.

---

**Universitas Sebelas Maret**  
**Program Studi Ilmu Komputer**  
**Mata Kuliah: Struktur Data dan Algoritma**  
**Dibuat: Juni 2026**