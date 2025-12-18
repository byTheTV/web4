#!/bin/sh

KEYCLOAK_URL="http://keycloak:8081"
REALM="area-check"
ADMIN_USER="admin"
ADMIN_PASS="admin"
MAX_RETRIES=30
RETRY_INTERVAL=5

echo ""
echo "Waiting for Keycloak to be ready..."

# Wait for Keycloak to be fully ready
retry_count=0
until curl -sf "${KEYCLOAK_URL}/health/ready" > /dev/null 2>&1; do
  retry_count=$((retry_count + 1))
  if [ $retry_count -ge $MAX_RETRIES ]; then
    echo "ERROR: Keycloak is not ready after $MAX_RETRIES attempts"
    exit 1
  fi
  echo "Waiting for Keycloak... ($retry_count/$MAX_RETRIES)"
  sleep $RETRY_INTERVAL
done

echo "Keycloak is ready!"
echo ""

# Additional wait to ensure admin API is fully initialized
sleep 10

echo "Getting admin token..."

TOKEN_RESPONSE=$(curl -s -X POST "${KEYCLOAK_URL}/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=${ADMIN_USER}" \
  -d "password=${ADMIN_PASS}" \
  -d "grant_type=password" \
  -d "client_id=admin-cli")

if [ $? -ne 0 ]; then
  echo "ERROR: Cannot connect to Keycloak at ${KEYCLOAK_URL}"
  exit 1
fi

TOKEN=$(echo "$TOKEN_RESPONSE" | grep -o '"access_token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "ERROR: Could not get token!"
  echo "Response: $TOKEN_RESPONSE"
  exit 1
fi

echo "Token received"
echo ""

echo "Enabling User Profile..."

ENABLE_RESPONSE=$(curl -s -w "\n%{http_code}" -X PUT "${KEYCLOAK_URL}/admin/realms/${REALM}" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"attributes":{"userProfileEnabled":"true"}}')

HTTP_CODE=$(echo "$ENABLE_RESPONSE" | tail -n1)
if [ "$HTTP_CODE" = "204" ] || [ "$HTTP_CODE" = "200" ]; then
  echo "User Profile enabled"
else
  echo "WARNING: Could not enable User Profile (HTTP $HTTP_CODE, maybe already enabled)"
fi

echo ""
echo "Configuring maxRadius attribute..."

# Create User Profile configuration with maxRadius
USER_PROFILE_CONFIG='{
  "attributes": [
    {
      "name": "username",
      "displayName": "${username}",
      "validations": {
        "length": { "min": 3, "max": 255 },
        "username-prohibited-characters": {},
        "up-username-not-idn-homograph": {}
      },
      "permissions": {
        "view": ["admin", "user"],
        "edit": ["admin", "user"]
      },
      "multivalued": false
    },
    {
      "name": "email",
      "displayName": "${email}",
      "validations": {
        "email": {},
        "length": { "max": 255 }
      },
      "required": {
        "roles": ["user"]
      },
      "permissions": {
        "view": ["admin", "user"],
        "edit": ["admin", "user"]
      },
      "multivalued": false
    },
    {
      "name": "firstName",
      "displayName": "${firstName}",
      "validations": {
        "length": { "max": 255 },
        "person-name-prohibited-characters": {}
      },
      "permissions": {
        "view": ["admin", "user"],
        "edit": ["admin", "user"]
      },
      "multivalued": false
    },
    {
      "name": "lastName",
      "displayName": "${lastName}",
      "validations": {
        "length": { "max": 255 },
        "person-name-prohibited-characters": {}
      },
      "permissions": {
        "view": ["admin", "user"],
        "edit": ["admin", "user"]
      },
      "multivalued": false
    },
    {
      "name": "maxRadius",
      "displayName": "Maximum Radius (R)",
      "validations": {
        "length": { "min": 1, "max": 10 }
      },
      "annotations": {
        "inputType": "text",
        "inputHelperTextBefore": "Enter a value between 0.5 and 5.0"
      },
      "required": {
        "roles": ["user"]
      },
      "permissions": {
        "view": ["admin", "user"],
        "edit": ["admin", "user"]
      },
      "multivalued": false
    }
  ],
  "groups": [
    {
      "name": "user-metadata",
      "displayHeader": "User metadata",
      "displayDescription": "Attributes, which refer to user metadata"
    }
  ]
}'

PROFILE_RESPONSE=$(curl -s -w "\n%{http_code}" -X PUT "${KEYCLOAK_URL}/admin/realms/${REALM}/users/profile" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d "$USER_PROFILE_CONFIG")

HTTP_CODE=$(echo "$PROFILE_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$PROFILE_RESPONSE" | sed '$d')

if [ "$HTTP_CODE" = "200" ] || [ "$HTTP_CODE" = "204" ]; then
  echo "User Profile configured!"
  echo ""
  echo "======================================================="
  echo "SUCCESS! User Profile with maxRadius is configured"
  echo "======================================================="
  echo ""
  echo "Test registration:"
  echo "  1. Open: http://localhost:3000"
  echo "  2. Click: 'Login via Keycloak' -> 'Register'"
  echo "  3. Field 'Maximum Radius (R)' should be present"
  echo ""
  echo "Test users:"
  echo "  user1 / user1   (maxRadius: 1.5)"
  echo "  demo / demo     (maxRadius: 2.0)"
  echo "  admin / admin   (maxRadius: 5.0)"
  echo ""
else
  echo "ERROR configuring User Profile (HTTP $HTTP_CODE):"
  echo "$RESPONSE_BODY"
  exit 1
fi
