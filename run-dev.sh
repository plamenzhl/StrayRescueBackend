#!/bin/bash
# Development run script for StrayRescue Backend

# Load environment variables from .env file if it exists
if [ -f .env ]; then
    echo "📂 Loading environment variables from .env file..."
    export $(cat .env | grep -v '^#' | xargs)
else
    echo "⚠️  No .env file found. Make sure environment variables are set."
fi

check_env_var() {
    if [ -z "${!1}" ]; then
        echo "❌ Required environment variable $1 is not set!"
        echo "   Please set it in your .env file or environment"
        return 1
    fi
}

echo "🔍 Checking required environment variables..."

# Check JWT configuration
if ! check_env_var "JWT_SECRET"; then
    echo "   You can generate one with: openssl rand -base64 32"
    exit 1
fi

# Check database configuration
check_env_var "DB_USERNAME" || exit 1
check_env_var "DB_PASSWORD" || exit 1

echo "✅ All required environment variables are set!"
echo ""
echo "🔍 DEBUG: Environment variables loaded:"
echo "JWT_SECRET: ${JWT_SECRET:0:8}..."
echo "DB_USERNAME: $DB_USERNAME"
echo "AWS_REGION: $AWS_REGION"
echo "AWS_S3_BUCKET_NAME: $AWS_S3_BUCKET_NAME"
echo "AWS_ACCESS_KEY_ID: ${AWS_ACCESS_KEY_ID:0:8}..."
echo ""
echo "🚀 Starting Stray Rescue Backend..."
echo "📊 JWT Secret: ${JWT_SECRET:0:8}..."  # Show first 8 chars only
echo "⏰ JWT Expiration: ${JWT_EXPIRATION_MS:-14400000}ms"
echo "🗄️  Database User: $DB_USERNAME"
echo "☁️  AWS Region: ${AWS_REGION:-eu-north-1}"
echo "🪣  S3 Bucket: ${AWS_S3_BUCKET_NAME:-stray-rescue-images-plamen}"

if [ -n "$AWS_ACCESS_KEY_ID" ]; then
    echo "🔑 AWS Access Key: ${AWS_ACCESS_KEY_ID:0:8}..."
else
    echo "🔑 AWS: Using default credential chain"
fi

echo ""
./mvnw spring-boot:run