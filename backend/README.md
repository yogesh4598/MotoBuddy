# MotoBuddy PHP REST API Backend

This is the lightweight PHP REST API backend for the MotoBuddy Android application.

## 1. Setup MySQL Database
1. Open XAMPP / WAMP or your MySQL Server.
2. Open **phpMyAdmin** or MySQL CLI and import `schema.sql`:
   ```sql
   mysql -u root -p < schema.sql
   ```
   This creates the `motobuddy` database with `users`, `motorcycles`, `trips`, and `maintenance` tables.

## 2. Start PHP Built-in Server
Open your terminal in this `backend/` folder and start the server:
```bash
php -S 0.0.0.0:8000
```

## 3. Endpoints Configured for Android
The Android application connects to `http://10.0.2.2:8000/api/v1/` automatically when running in the Android Studio Emulator.

- **`POST /api/v1/auth/login.php`** — Authenticates user and returns Bearer token
- **`POST /api/v1/auth/register.php`** — Creates a new user profile
- **`POST /api/v1/mechanic/chat.php`** — Diagnostic AI Mechanic endpoint
