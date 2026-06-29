package Models;
/**
 * ==========================================
 * CLASS MODEL: PRODUCT
 * ==========================================
 * Representasi model data barang/produk di POS.
 * Menyimpan informasi ID, nama, harga, dan stok barang.
 */
public class Product {
    private String id;
    private String nama;
    private double harga;
    private int stok;

    public Product(String id, String nama, double harga, int stok) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.stok = stok;
    }

    // Getter dan Setter - Mengambil dan mengubah atribut produk
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public double getHarga() { return harga; }
    public void setHarga(double harga) { this.harga = harga; }
    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }

    @Override
    public String toString() {
        return String.format("ID: %-5s | Nama: %-20s | Harga: Rp %,10.2f | Stok: %3d", 
                id, nama, harga, stok);
    }
}
