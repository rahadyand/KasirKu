package Managers;
 
import java.util.*;
import Models.CartItem;
import Models.Transaction;

/**
 * ==========================================
 * CLASS MANAGER: PAYMENT QUEUE (QUEUE)
 * ==========================================
 * Manajemen antrean pembayaran kasir menggunakan Queue<Transaction>.
 * Mengimplementasikan prinsip FIFO (First In First Out).
 */
public class PaymentQueue {
    private Queue<Transaction> queue;

    public PaymentQueue() {
        // Menggunakan Queue untuk mengimplementasikan prinsip FIFO (First In First Out)
        this.queue = new LinkedList<>();
    }

    /**
     * ========================================================================
     * ANALISIS STRUKTUR DATA & KOMPLEKSITAS WAKTU: ENQUEUE (TAMBAH KE ANTREAN)
     * ========================================================================
     * - Mengapa Menggunakan Queue untuk Antrean Pembayaran?
     *   Antrean pembayaran di kasir mengikuti prinsip "siapa datang terlebih dahulu, 
     *   siapa dilayani terlebih dahulu" (FIFO - First In First Out). Queue sangat 
     *   cocok untuk implementasi ini.
     * 
     * - Kompleksitas Waktu (Time Complexity) ENQUEUE:
     *   * Rata-rata (Average Case): O(1) / Constant Time.
     *   * Penjelasan: Operasi enqueue menambahkan elemen ke belakang (tail) Queue. 
     *     LinkedList menambahkan elemen di akhir dengan waktu konstan.
     */
    public void enqueue(Transaction transaction) {
        queue.add(transaction);
    }

    /**
     * ========================================================================
     * ANALISIS STRUKTUR DATA & KOMPLEKSITAS WAKTU: DEQUEUE (PROSES ANTREAN)
     * ========================================================================
     * - Operasi Dequeue:
     *   Mengeluarkan transaksi yang paling lama menunggu (transaksi pertama) dari antrean.
     *   Sesuai dengan prinsip FIFO.
     * 
     * - Kompleksitas Waktu (Time Complexity) DEQUEUE:
     *   * Rata-rata (Average Case): O(1) / Constant Time.
     *   * Penjelasan: Operasi dequeue hanya menghapus elemen dari depan (head) Queue 
     *     tanpa perlu shifting atau reordering elemen lainnya di LinkedList.
     */
    public void dequeue() {
        if (queue.isEmpty()) {
            System.out.println("-> Error: Tidak ada transaksi dalam antrean!");
            return;
        }
        
        Transaction txn = queue.poll();
        System.out.println("\n================= INVOICE PEMBAYARAN ================");
        System.out.println("Transaction ID   : " + txn.getTransactionId());
        System.out.println("---------------------------------------------------");
        
        int itemCount = 0;
        for (CartItem item : txn.getItems()) {
            System.out.println(item);
            itemCount++;
        }
        
        System.out.println("---------------------------------------------------");
        System.out.printf("Total Item: %d | Total Pembayaran: Rp %,10.2f\n", itemCount, txn.getTotalHarga());
        System.out.println("======================================================");
        System.out.println("-> Sukses: Transaksi " + txn.getTransactionId() + " selesai diproses!");
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public Queue<Transaction> getQueue() {
        return queue;
    }
}
