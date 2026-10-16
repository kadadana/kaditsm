FROM golang:1.27-alpine AS builder
WORKDIR /app

RUN apk add --no-cache git

COPY go.mod go.sum ./

RUN --mount=type=cache,target=/go/pkg/mod \
    go mod download

COPY . .

RUN --mount=type=cache,target=/go/pkg/mod \
    --mount=type=cache,target=/root/.cache/go-build \
    CGO_ENABLED=0 GOOS=linux go build -ldflags="-s -w" -o gateway ./cmd/gateway

FROM alpine:3.20
WORKDIR /app
RUN apk --no-cache add ca-certificates tzdata

COPY --from=builder /app/gateway .
EXPOSE 8080

ENTRYPOINT ["./gateway"]