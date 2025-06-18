#!/bin/bash
# Development run script

# Ensure environment variables are set
if [ -z "$APP_JWT_SECRET" ]; then
    echo "❌ APP_JWT_SECRET environment variable is not set!"
    echo "Please run: export APP_JWT_SECRET=\"your-key-here\""
    exit 1
fi

echo "🚀 Starting Stray Rescue Backend..."
echo "📊 JWT Secret: ${APP_JWT_SECRET:0:8}..."  # Show first 8 chars only
echo "⏰ JWT Expiration: ${APP_JWT_EXPIRATION_MS:-14400000}ms"

./mvnw spring-boot:run