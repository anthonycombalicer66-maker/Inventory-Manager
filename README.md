# Mini Inventory Program (Java Swing + MySQL)

Add, view, update, and delete products. Data is stored in MySQL (XAMPP).

## Files

- `InventoryApp.java` - the app
- `inventory_db.sql` - creates the database, table, and sample data
- `mysql-connector-j-26.10.0.jar` - MySQL driver (download separately)

## Requirements

- **XAMPP** (MySQL): https://www.apachefriends.org
- **JDK 11+**: https://adoptium.net (check with `javac -version`)
- **MySQL Connector/J**: https://dev.mysql.com/downloads/connector/j/
  - Select **Platform Independent**, download the **ZIP Archive**.
  - Click "No thanks, just start my download."
  - Extract it and copy `mysql-connector-j-26.10.0.jar` out of the folder.

## Setup

1. Put these in one folder (e.g. `Downloads\InventoryApp`):
   ```
   InventoryApp.java
   inventory_db.sql
   mysql-connector-j-26.10.0.jar
   ```
2. Open XAMPP Control Panel and **Start MySQL** (also start Apache if you want phpMyAdmin).
3. Create the database (optional, the app also does this itself):
   - Open http://localhost/phpmyadmin
   - Go to **Import**, choose `inventory_db.sql`, click **Import**.

## Run

1. In the project folder, click the File Explorer address bar, type `cmd`, press Enter.
2. Compile:
   ```
   javac -cp ".;mysql-connector-j-26.10.0.jar" InventoryApp.java
   ```
3. Run:
   ```
   java -cp ".;mysql-connector-j-26.10.0.jar" InventoryApp
   ```

On macOS/Linux, use `:` instead of `;`.
If your jar has a different version, change the file name in both commands.

## Database Settings

Defaults match XAMPP (`root`, empty password, port 3306). If yours differ, edit these lines in `InventoryApp.java`:

```java
private static final String DB_HOST = "jdbc:mysql://localhost:3306/";
private static final String DB_NAME = "inventory_db";
private static final String DB_USER = "root";
private static final String DB_PASS = "";
```

## Using the App

- **Add:** fill in the fields, click **Submit**
- **Edit:** click a row, change the fields, click **Update**
- **Delete:** click a row, click **Delete**, confirm
- **Clear:** resets the form

## Troubleshooting

| Error | Fix |
|-------|-----|
| `'javac' is not recognized` | Install the JDK and reopen Command Prompt |
| `No suitable driver found` | Jar name is wrong or the jar is not in the same folder |
| `Communications link failure` / `Connection refused` | Start MySQL in XAMPP (port 3306) |
| `Access denied for user 'root'` | Set your MySQL password in `DB_PASS` |
| MySQL won't start in XAMPP | Another service is using port 3306; stop it or change the port |
