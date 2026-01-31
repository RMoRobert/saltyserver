# Salty Server

Salty Server is a companion to the Salty desktop or mobile apps that allows syncing content among multiple devices (without needing to worry about storing the data file in a shared location and concerns regarding simultaneous usage) as well as a basic web interface for viewing synced data.

Salty Server is *not* a standalone app; it must be used with the desktop or mobile apps to sync data (although technically you could use the same API endpoints for CRUD operations to get data in or out yourself...).

**Salty Server is currently beta quality. Do not use in a production environment without adequate testing.** Salty Server is provided as-is under the terms of the LICENSE. As an open-source project created largely for my own personal use, no support is guaranteed, but feel free to use GitHub features to discuss, etc.

## Compiling

To compile: run Gradle `bootRun`, `bootJar`, or other tasks. Target is JVM 25. Written with Spring Boot and various Spring Boot Starters, including H2 for database.

## Usage

For actual usage: if not bare metal, it is suggested to run the JAR in Docker or a similar containerized setup. The `Dockerfile` in this repo contains a good starting point that should work as-is for most users. The `docker-readme-deployoffline.md` file contains instructions for building a Docker image for various platforms as well as how to deploy your built image without needing a centralized repository.+

The default administrator username is **admin**, and the default password is as set in your environment (Docker Compose or `.env` file) or `application.properties`. It is suggested to change this value after initial startup of the application as well as to set other suggested environment variables to unique values for your setup.

After booting the app, log in as admin and create an account as a "USER" type. Use this user account in the Salty desktop or mobile apps to sync. If you have different users -- or different app databases you wish to keep separate -- create a different user for each. Recipes and other data (categories, tags, etc.) are linked to user accounts. For a simple home setup, you will likely need only one user account (and the admin account; it is suggested to keep these separate).