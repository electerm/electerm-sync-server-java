# Java Electerm sync server

[![Build Status](https://img.shields.io/endpoint.svg?url=https%3A%2F%2Factions-badge.atrox.dev%2Fatrox%2Fsync-dotenv%2Fbadge)](https://github.com/electerm/electerm-sync-server-java/actions)

A simple electerm data sync server.

## Use

```bash
git clone git@github.com:electerm/electerm-sync-server-java.git
cd electerm-sync-server-java

# create env file, then edit .env
cp sample.env .env

## run
gradlew run

## build
gradlew build

# would show something like
# server running at http://127.0.0.1:7837

# in electerm sync settings, set custom sync server with:
# server url: http://127.0.0.1:7837
# JWT_SECRET: your JWT_SECRET in .env
# JWT_USER_NAME: one JWT_USER in .env
```

## Test

```bash
gradlew test
```

## Write your own data store

Just take [src/ElectermSync/FileStore.java](src/ElectermSync/FileStore.java) as an example, write your own read/write method

## License

MIT
