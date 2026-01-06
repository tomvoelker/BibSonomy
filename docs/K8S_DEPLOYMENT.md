# Kubernetes Deployment (Webapp + REST API)

This repo provides two kustomize overlays for the new webapp + REST API.

## Prereqs
- Target namespace: `extsonomy`
- Images are built/pushed to GHCR by `.github/workflows/build-and-push-images.yml`

## Option A: Sample DB (ephemeral)
Creates a MariaDB pod with test data that resets on restart.

```bash
kubectl apply -k k8s/overlays/sample-db
```

## Option B: External Test DB (MySQL 5.0)
Uses an existing MySQL test DB. Update the secret placeholders first.

```bash
kubectl apply -k k8s/overlays/test-db
```

Update `k8s/overlays/test-db/rest-api-db-secret.yaml` with real values before applying.
