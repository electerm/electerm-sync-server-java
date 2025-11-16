# Java Electerm Sync Server

[![Build Status](https://github.com/electerm/electerm-sync-server-java/actions/workflows/linux.yml/badge.svg)](https://github.com/electerm/electerm-sync-server-java/actions)

[English](README.md) | [中文](README_CN.md)

A simple, lightweight data synchronization server for [Electerm](https://github.com/electerm/electerm), built with Java and Spark. It provides a REST API for syncing bookmarks, history, and other data across Electerm instances.

## Prerequisites

- **Java 17**: Required for building and running the server. Download from [Adoptium](https://adoptium.net/) or your preferred JDK provider.
- **Gradle**: Included via the Gradle Wrapper (`gradlew`), so no separate installation needed.
- **Git**: For cloning the repository.

## Installation

1. **Clone the repository**:

   ```bash
   git clone https://github.com/electerm/electerm-sync-server-java.git
   cd electerm-sync-server-java
   ```

2. **Set up the environment file**:

   ```bash
   cp sample.env .env
   ```

   Edit `.env` with your preferred settings (see Configuration below).

## Configuration

The server uses environment variables from a `.env` file for configuration. Key settings include:

- `JWT_SECRET`: A secret key for JWT token signing (generate a strong random string).
- `JWT_USER_NAME`: Username for authentication.
- `JWT_USER_PASSWORD`: Password for authentication.
- `PORT`: Server port (default: 7837).
- `DB_URL`: H2 database URL (default: embedded database).

Example `.env`:

```properties
JWT_SECRET=your-super-secret-key-here
JWT_USER_NAME=admin
JWT_USER_PASSWORD=securepassword
PORT=7837
DB_URL=jdbc:h2:./electerm_sync_db
```

**Security Note**: Never commit `.env` to version control. Use strong, unique values for secrets.

## Running the Server

### Development Mode
For quick testing or development:
```bash
./gradlew run
```
- Starts the server on `http://127.0.0.1:7837`.
- Output will show: `server running at http://127.0.0.1:7837`.
- Press `Ctrl+C` to stop.

### Production Mode
1. **Build the application**:
   ```bash
   ./gradlew build
   ```

2. **Extract the distribution**:
   ```bash
   tar -xvf build/distributions/electerm-sync-server-java.tar
   cd electerm-sync-server-java
   ```

3. **Run the server**:
   ```bash
   ./bin/electerm-sync-server-java
   ```
   - Runs in the foreground. Use `&` to background it: `./bin/electerm-sync-server-java &`.
   - Access at `http://127.0.0.1:7837` (or your configured port).

## Using with Electerm

1. In Electerm, go to **Settings > Sync**.
2. Select **Custom Sync Server**.
3. Set:
   - **Server URL**: `http://127.0.0.1:7837` (or your server's URL).
   - **JWT Secret**: The `JWT_SECRET` from your `.env`.
   - **Username**: The `JWT_USER_NAME` from your `.env`.
4. Save and sync your data.

The API endpoint is `http://127.0.0.1:7837/api/sync`.

## Testing

Run the test suite:
```bash
./gradlew test
```
- Uses JUnit for unit tests.
- Reports are in `build/reports/tests/test/index.html`.

## Customization

### Writing Your Own Data Store

The server uses an H2 database by default, but you can implement custom storage.

1. Implement the data store interface by extending or referencing `DataStore.java`.
2. Update `App.java` to use your custom store.
3. Rebuild and run.

Example: Take [src/main/java/ElectermSync/DataStore.java](src/main/java/ElectermSync/DataStore.java) as a reference for read/write methods.

## Docker

For containerized deployment, see [electerm-sync-server-java-docker](https://github.com/Aliang-code/electerm-sync-server-java-docker).

## Sync Servers in Other Languages

Explore alternatives: [Custom Sync Server Wiki](https://github.com/electerm/electerm/wiki/Custom-sync-server).

## License

MIT
