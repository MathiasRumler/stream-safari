# Stage 1: Build Svelte UI
FROM node:22-alpine AS svelte-build
WORKDIR /app

# Create the directory structure so the build can output to the correct location
# svelte.config.js is configured to output to ../src/main/resources/static
RUN mkdir -p src/main/resources/static

WORKDIR /app/svelte-ui
COPY svelte-ui/package.json svelte-ui/package-lock.json ./
RUN npm ci

COPY svelte-ui/ .
RUN npm run build

# Stage 2: Run Spring Boot Application
FROM eclipse-temurin:25
WORKDIR /app

# Copy Gradle files
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Copy source code
COPY src ./src

# Copy the built Svelte assets into the Spring Boot static resources directory
COPY --from=svelte-build /app/src/main/resources/static ./src/main/resources/static

# Grant execution permissions to the Gradle wrapper
RUN chmod +x gradlew

# Pre-compile classes to speed up container startup
RUN ./gradlew classes --no-daemon

EXPOSE 8080
ENTRYPOINT ["./gradlew", "bootRun", "--no-daemon"]
