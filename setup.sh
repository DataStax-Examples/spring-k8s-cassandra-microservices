echo "🚀 Go to https://astra.datastax.com/organizations?create_service_account.
Grab your service account credentials and paste them here: "
  read -r SERVICE_ACCOUNT
  export SERVICE_ACCOUNT="${SERVICE_ACCOUNT}"

echo "Getting your Astra DevOps API token..."
DEVOPS_TOKEN=$(curl -s --request POST \
  --url "https://api.astra.datastax.com/v2/authenticateServiceAccount" \
  --header 'content-type: application/json' \
  --data "$SERVICE_ACCOUNT" | jq -r '.token')

echo "Getting databases..."
DBS=$(curl -s --request GET \
  --url "https://api.astra.datastax.com/v2/databases?include=nonterminated&provider=all&limit=25" \
  --header "authorization: Bearer ${DEVOPS_TOKEN}" \
  --header 'content-type: application/json')

# TODO: Allow the user to select the DB
NUM_DBS=$(echo "${DBS}" | jq -c 'length')
FIRST_DB_ID=$(echo "${DBS}" | jq -c '.[0].id')
FIRST_DB_REGION=$(echo "${DBS}" | jq -c '.[0].info.region')
FIRST_DB_USER=$(echo "${DBS}" | jq -c '.[0].info.user')

# TODO: Allow the user to select a keyspace
FIRST_DB_KEYSPACE=$(echo "${DBS}" | jq -c '.[0].info.keyspaces[0]')
FIRST_DB_SECURE_BUNDLE_URL=$(echo "${DBS}" | jq -c '.[0].info.datacenters[0].secureBundleUrl')

export ASTRA_SECURE_BUNDLE_URL="${FIRST_DB_SECURE_BUNDLE_URL}"
gp env ASTRA_SECURE_BUNDLE_URL="${FIRST_DB_SECURE_BUNDLE_URL}" &>/dev/null

# Download the secure connect bundle
curl -s -L $(echo $FIRST_DB_SECURE_BUNDLE_URL | sed "s/\"//g") -o astra-creds.zip

export ASTRA_DB_BUNDLE="astra-creds.zip"
gp env ASTRA_DB_BUNDLE="astra-creds.zip" &>/dev/null

export ASTRA_DB_USERNAME="${FIRST_DB_USER}"
gp env ASTRA_DB_USERNAME="${FIRST_DB_USER}" &>/dev/null

export ASTRA_DB_KEYSPACE="${FIRST_DB_KEYSPACE}"
gp env ASTRA_DB_KEYSPACE="${FIRST_DB_KEYSPACE}" &>/dev/null

export ASTRA_DB_ID="${FIRST_DB_ID}"
gp env ASTRA_DB_ID="${FIRST_DB_ID}" &>/dev/null

export ASTRA_DB_REGION="${FIRST_DB_REGION}"
gp env ASTRA_DB_REGION="${FIRST_DB_REGION}" &>/dev/null

if [[ -z "$ASTRA_DB_PASSWORD" ]]; then
  echo "What is your Astra DB password? 🔒"
  read -s ASTRA_DB_PASSWORD
  export ASTRA_DB_PASSWORD="${ASTRA_DB_PASSWORD}"
  gp env ASTRA_DB_PASSWORD="${ASTRA_DB_PASSWORD}" &>/dev/null
fi

echo "You're all set 👌"
