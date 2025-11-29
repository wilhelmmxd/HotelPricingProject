#!/usr/bin/env python3
"""
Initialize SQLite database schema for HotelPricingProject.
Creates table `hotel_prices` with AUTOINCREMENT primary key if not present.
"""

import sqlite3

def init_db(db_path: str = "hotel_pricing.db"):
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()

    cursor.execute(
        """
        CREATE TABLE IF NOT EXISTS hotel_prices (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            hotel_name TEXT NOT NULL,
            city TEXT NOT NULL,
            check_in_date TEXT NOT NULL,
            check_out_date TEXT NOT NULL,
            price REAL NOT NULL,
            rating TEXT,
            address TEXT,
            scraped_date TEXT NOT NULL
        )
        """
    )

    conn.commit()
    conn.close()
    print(f"✅ Initialized schema in {db_path}")

if __name__ == "__main__":
    init_db()
