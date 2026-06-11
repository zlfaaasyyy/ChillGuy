# ChillGuy 

**Your 7-Day Glow Up Starts Here**

ChillGuy adalah aplikasi Android bertema **Kesehatan & Kebugaran** yang membantu pengguna menjalani program latihan selama 7 hari, mendengarkan musik workout, serta memantau progres latihan harian. Aplikasi ini dikembangkan sebagai proyek Final Lab Mobile 2026.

---

## ✨ Fitur Utama

### 🗓️ 7-Day Workout Plan
Program latihan selama 7 hari dengan timer hitung mundur pada setiap gerakan.

### 🎵 Workout Music
Menampilkan daftar lagu workout menggunakan Deezer Public API dan memutar preview lagu secara langsung di aplikasi.

### 📊 Progress Tracker
Menyimpan progres latihan yang telah diselesaikan dan menampilkan perkembangan pengguna.

### 🌙 Dark / Light Mode
Pengguna dapat mengganti tema aplikasi melalui halaman Profile.

### 📴 Offline Support
Data progres latihan tetap dapat diakses tanpa koneksi internet.

### 🔐 Authentication
Fitur Register dan Login dengan sesi pengguna yang tersimpan secara lokal.

---

## 🛠️ Teknologi yang Digunakan

- Java
- XML
- Material Components
- Navigation Component
- Retrofit
- Room Database
- SharedPreferences
- Glide
- Deezer Public API

---

## 🚀 Cara Penggunaan

### Memulai Aplikasi

1. Jalankan aplikasi.
2. Jika belum memiliki akun, pilih **Get Started** untuk registrasi.
3. Jika sudah memiliki akun, pilih **I Already Have an Account** untuk login.
4. Setelah berhasil login, pengguna akan langsung masuk ke halaman utama aplikasi.

### Workout

1. Buka tab **Workout**.
2. Pilih hari latihan yang ingin dilakukan.
3. Tekan tombol **Start** untuk memulai latihan.
4. Timer akan berjalan otomatis pada setiap gerakan.
5. Setelah latihan selesai, progres akan tersimpan secara otomatis.

### Music

1. Buka tab **Music**.
2. Pilih lagu yang ingin diputar.
3. Tekan tombol play untuk mendengarkan preview lagu.
4. Jika terjadi gangguan koneksi, gunakan tombol **Refresh** untuk memuat ulang data.

### Profile

Pada halaman Profile, pengguna dapat:

- Melihat statistik latihan.
- Mengubah tema aplikasi (Dark/Light Mode).
- Logout dari aplikasi.

---

## ⚙️ Implementasi Teknis

### Activity dan Intent

Aplikasi menggunakan beberapa Activity untuk menangani alur autentikasi, navigasi utama, dan halaman detail workout. Perpindahan antar Activity dilakukan menggunakan Intent, termasuk pengiriman data workout ke halaman detail.

### Fragment dan Navigation Component

Empat Fragment utama (`Home`, `Workout`, `Music`, dan `Profile`) dikelola menggunakan Navigation Component dan terhubung dengan Bottom Navigation.

### RecyclerView

RecyclerView digunakan untuk menampilkan daftar workout, daftar lagu, dan daftar gerakan latihan.

### Background Thread

Operasi jaringan dan database dijalankan menggunakan `ExecutorService` sehingga proses tidak menghambat Main Thread.

### Networking API

Fitur Music memanfaatkan **Retrofit** untuk mengambil data lagu dari **Deezer Public API**.

### Penyimpanan Data Lokal

- **Room Database** digunakan untuk menyimpan progres workout.
- **SharedPreferences** digunakan untuk menyimpan data sesi login dan preferensi tema.

### Dark & Light Theme

Tema aplikasi diimplementasikan menggunakan resource `values` dan `values-night`, serta dapat diubah melalui halaman Profile.

---

## 💻 Instalasi dan Menjalankan Aplikasi

Clone repository:

```bash
git clone https://github.com/USERNAME/ChillGuy.git
```

Buka project menggunakan Android Studio, lakukan Gradle Sync, lalu jalankan aplikasi menggunakan emulator atau perangkat Android.

```bash
Shift + F10
```

atau melalui menu:

```text
Run → Run 'app'
```

> Tidak diperlukan API Key karena aplikasi menggunakan Deezer Public API.

---

## 👤 Author

**Zalfa Syauqiyah Hamka**  
**H071241041**

Final Lab Mobile 2026  
Tema: **Kesehatan & Kebugaran**
