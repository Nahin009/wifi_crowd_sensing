import json
import csv

# Load JSON data
with open("All_data.json", "r") as json_file: 
    data = json.load(json_file)

entries = []
scan_lists = []

entry_id = 1
scan_id = 1
for room_no, room_data in data.items():
    for entry in room_data.values():
        # Calculate number of scans and average data per scan
        scan_list = entry.get("ScanList", [])
        num_scans = len(scan_list)
        
        if num_scans == 0:
            continue  # Skip entries with 0 scans
        
        avg_data_per_scan = (
            sum(len(scan_group) for scan_group in scan_list) / num_scans if num_scans > 0 else 0
        )
        
        # Extract metadata for entries.csv
        entries.append([
            entry_id,
            room_no,
            entry.get("StudentID"),
            entry.get("DeviceModel"),
            entry.get("BatteryLifeBefore"),
            entry.get("BatteryLifeAfter"),
            entry.get("DateTime"),
            num_scans,
            round(avg_data_per_scan, 2)  # Rounded to 2 decimal places
        ])
        
        # Extract scan list for scan_lists.csv
        for group_index, scan_group in enumerate(scan_list):
            for scan in scan_group:
                scan_lists.append([
                    scan_id,
                    entry_id,
                    group_index,
                    scan.get("Frequency"),
                    scan.get("MacAddress"),
                    scan.get("SSID"),
                    scan.get("Strength")
                ])
                scan_id += 1
        
        entry_id += 1

folder = "All_data/"
# Write entries.csv
with open(folder + "entries.csv", "w", newline="") as entries_file:
    writer = csv.writer(entries_file)
    writer.writerow(["entryId", "roomNo", "StudentID", "DeviceModel", "BatteryLifeBefore", "BatteryLifeAfter", "DateTime", "NumScans", "AvgDataPerScan"])
    writer.writerows(entries)

# Write scan_lists.csv
with open(folder + "scan_lists.csv", "w", newline="") as scan_lists_file:
    writer = csv.writer(scan_lists_file)
    writer.writerow(["scanId", "entryId", "scanGroupIndex", "Frequency", "MacAddress", "SSID", "Strength"])
    writer.writerows(scan_lists)

print("CSV files created successfully!")
