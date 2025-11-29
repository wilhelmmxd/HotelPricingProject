import sqlite3
import json
from datetime import datetime

db_path = "hotel_pricing.db"

def print_tables(cursor):
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table';")
    tables = cursor.fetchall()
    if not tables:
        print("❌ No tables found in database")
        return

    print("📋 TABLES IN DATABASE:")
    print("─" * 70)

    for table in tables:
        table_name = table[0]
        print(f"\n📌 Table: {table_name}")

        cursor.execute(f"PRAGMA table_info({table_name});")
        columns = cursor.fetchall()
        print("   Columns:")
        for col in columns:
            col_id, col_name, col_type, not_null, default, pk = col
            print(f"     • {col_name} ({col_type})" + (" [PRIMARY KEY]" if pk else ""))

        cursor.execute(f"SELECT COUNT(*) FROM {table_name};")
        row_count = cursor.fetchone()[0]
        print(f"   📊 Total Records: {row_count}")

        if table_name == "hotel_prices" and row_count > 0:
            print("\n   📋 Last 5 Records:")
            print(f"   {'-' * 65}")
            cursor.execute(f"PRAGMA table_info({table_name});")
            col_names = [col[1] for col in cursor.fetchall()]
            cursor.execute("SELECT * FROM hotel_prices ORDER BY id DESC LIMIT 5;")
            rows = cursor.fetchall()
            for row in rows:
                print(f"   ID: {row[0]}")
                for i, col_name in enumerate(col_names[1:], 1):
                    print(f"      {col_name}: {row[i]}")
                print()
        elif row_count == 0:
            print("   ℹ️  No data in table yet\n")

def print_cheapest_per_city(cursor):
    print("\n" + "╔" + "═"*68 + "╗")
    print("║" + " "*19 + "TOP 10 CHEAPEST BY CITY" + " "*23 + "║")
    print("╚" + "═"*68 + "╝\n")

    # Get distinct cities
    try:
        cursor.execute("SELECT DISTINCT city FROM hotel_prices ORDER BY city ASC")
        cities = [r[0] for r in cursor.fetchall()]
    except Exception:
        print("❌ Could not query hotel_prices (table may be missing).")
        return

    for city in cities:
        print(f"📍 {city}")
        cursor.execute(
            """
            SELECT check_in_date, price, hotel_name
            FROM hotel_prices
            WHERE city = ?
            ORDER BY price ASC
            LIMIT 10
            """,
            (city,)
        )
        rows = cursor.fetchall()
        if not rows:
            print("   ℹ️  No records yet\n")
            continue

        for i, (check_in, price, hotel) in enumerate(rows, 1):
            print(f"   {i:2d}. {check_in}: ${price} — {hotel}")
        print()

try:
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    print("✅ Connected to hotel_pricing.db\n")

    print_tables(cursor)
    print_cheapest_per_city(cursor)

    conn.close()
    print("✅ Database inspection complete!")

except FileNotFoundError:
    print(f"❌ Database file not found: {db_path}")
except Exception as e:
    print(f"❌ Error: {e}")
